package com.shawn.googlemarket.manager;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.bean.DownloadInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.IOUtils;
import com.shawn.googlemarket.utils.LogUtils;
import com.shawn.googlemarket.utils.UIUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shawn on 2016/5/21.
 */
public class DownloadManager {
    public static final int STATE_UNDO = 1;
    public static final int STATE_WAITING = 2;
    public static final int STATE_DOWNLOADING = 3;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_ERROR = 5;
    public static final int STATE_SUCCESS = 6;

    // 下载对象的集合, 使用线程安全的HashMap
    private ConcurrentHashMap<String,DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>();

    // 下载任务的集合
    private ConcurrentHashMap<String,DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();

    private static DownloadManager mDM = new DownloadManager();

    private DownloadManager() {
    }

    public static DownloadManager getInstance(){
        return mDM;
    }


    // 4. 观察者集合
    private ArrayList<DownloadObserver> mObservers = new ArrayList<>();
    /**
     * 1. 声明观察者的接口
     */
    public interface DownloadObserver{
        // 下载状态发生变化
        void onDownloadStateChanged(DownloadInfo downloadInfo);

        // 下载进度发生变化
        void onDownloadProgressChanged(DownloadInfo downloadInfo);
    }

    // 2. 注册观察者
    public synchronized void registerObserver(DownloadObserver observer){
        if(observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    // 3. 注销观察者
    public synchronized void unregisterObserver(DownloadObserver observer){
        if(observer != null && mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    // 5.通知下载状态发生变化
    public synchronized void notifyDownloadStateChanged(DownloadInfo downloadInfo){
        for (DownloadObserver observer : mObservers){
            observer.onDownloadStateChanged(downloadInfo);
        }
    }

    // 6.通知下载进度发生变化
    public synchronized void notifyDownloadProgressChanged(DownloadInfo downloadInfo){
        for (DownloadObserver observer : mObservers){
            observer.onDownloadProgressChanged(downloadInfo);
        }
    }

    // 开始下载
    public synchronized void downLoad(AppInfo info){
        // 如果对象是第一次下载, 需要创建一个新的DownloadInfo对象,从头下载
        // 如果之前下载过, 要接着下载,实现断点续传
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if(downloadInfo == null) {
            downloadInfo = DownloadInfo.copy(info);// 生成一个下载的对象

            // 将下载对象放入集合中
            mDownloadInfoMap.put(downloadInfo.id, downloadInfo);
            downloadInfo.currentState = DownloadManager.STATE_WAITING;// 状态切换为等待下载
            notifyDownloadStateChanged(downloadInfo);// 通知所有的观察者, 状态发生变化了

        }
        // 初始化下载任务, 并放入线程池中运行
        DownloadTask task = mDownloadTaskMap.get(downloadInfo.id);
        if(task == null) {
            task = new DownloadTask(downloadInfo);
        }
        ThreadManager.getThreadPool().execute(task);
        // 将下载任务放入集合中
        mDownloadTaskMap.put(downloadInfo.id,task);
    }

    class DownloadTask implements Runnable{
        private DownloadInfo downloadInfo;
        private String path;
        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            downloadInfo.currentState = DownloadManager.STATE_DOWNLOADING;
            notifyDownloadStateChanged(downloadInfo);

            File file = new File(downloadInfo.path);
            if(! file.exists() || file.length() != downloadInfo.currentPos
                    || downloadInfo.currentPos == 0) {
                // 从头开始下载
                // 删除无效文件
                file.delete();// 文件如果不存在也是可以删除的, 只不过没有效果而已
                downloadInfo.currentPos = 0;

                // 从头开始下载
                path = HttpHelper.PATH + "download?name=" + downloadInfo.downloadUrl;

            }else {
                // 断点续传
                // range 表示请求服务器从文件的哪个位置开始返回数据
                path = HttpHelper.PATH + "download?name="
                        + downloadInfo.downloadUrl + "&range=" + file.length();
            }

            HttpURLConnection connection = null;
            InputStream is = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(path);
                out = new FileOutputStream(file, true);// 要在原有文件基础上追加数据
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    is = connection.getInputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024 * 4];

                    // 只有状态是正在下载, 才继续轮询. 解决下载过程中中途暂停的问题
                    while ((len = is.read(buffer)) != -1
                            && downloadInfo.currentState == STATE_DOWNLOADING) {
                        out.write(buffer, 0, len);
                        out.flush();// 把剩余数据刷入本地
                        // 更新下载进度
                        downloadInfo.currentPos += len;
                        notifyDownloadProgressChanged(downloadInfo);
                    }

                } else {
                    // 网络异常
                    file.delete();// 删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    downloadInfo.currentPos = 0;
                    notifyDownloadStateChanged(downloadInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
                IOUtils.close(is);
                IOUtils.close(out);
                // 文件下载结束
                if (file.length() == downloadInfo.size) {
                    // 文件完整, 表示下载成功
                    downloadInfo.currentState = STATE_SUCCESS;
                    notifyDownloadStateChanged(downloadInfo);
                } else if (downloadInfo.currentState == STATE_PAUSE) {
                    // 中途暂停
                    notifyDownloadStateChanged(downloadInfo);
                } else {
                    // 下载失败
                    file.delete();// 删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    downloadInfo.currentPos = 0;
                    notifyDownloadStateChanged(downloadInfo);
                }
            }
            // 从集合中移除下载任务
            mDownloadTaskMap.remove(downloadInfo.id);
        }
    }

    // 下载暂停
    public synchronized void pause(AppInfo info){
        // 取出下载对象
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if(downloadInfo != null) {
            // 只有在正在下载和等待下载时才需要暂停
            if(downloadInfo.currentState == DownloadManager.STATE_DOWNLOADING
                    || downloadInfo.currentState == DownloadManager.STATE_WAITING) {
                DownloadTask task = mDownloadTaskMap.get(info.id);
                if(task != null) {
                    // 移除下载任务, 如果任务还没开始,正在等待, 可以通过此方法移除
                    // 如果任务已经开始运行, 需要在run方法里面进行中断
                    ThreadManager.getThreadPool().cancel(task);
                }

                // 将下载状态切换为暂停
                downloadInfo.currentState = DownloadManager.STATE_PAUSE;
                notifyDownloadStateChanged(downloadInfo);
            }
        }
    }

    // 开始安装
    public synchronized void install(AppInfo info){
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        if (downloadInfo != null) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path),
                    "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);

            mDownloadInfoMap.remove(downloadInfo);
        }

    }

    // 根据应用信息返回下载对象
    public DownloadInfo getDownloadInfo(AppInfo info){
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
        return downloadInfo;
    }
}

package com.shawn.googlemarket.bean;

import android.os.Environment;

import com.shawn.googlemarket.manager.DownloadManager;

import java.io.File;

/**
 * Created by shawn on 2016/5/21.
 */
public class DownloadInfo {
    public String id;
    public String name;
    public String downloadUrl;
    public long size;
    public String packageName;

    public long currentPos;// 当前下载位置
    public int currentState;// 当前下载状态
    public String path;// 下载到本地文件的路径

    public static final String GOOGLE_MARKET = "GOOGLE_MARKET";// sdcard根目录文件夹名称
    public static final String DONWLOAD = "download";// 子文件夹名称, 存放下载的文件

    public static DownloadInfo copy(AppInfo info){
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.id = info.id;
        downloadInfo.name = info.name;
        downloadInfo.downloadUrl = info.downloadUrl;
        downloadInfo.size = info.size;
        downloadInfo.packageName = info.packageName;

        downloadInfo.currentPos = 0;
        downloadInfo.currentState = DownloadManager.STATE_UNDO;
        downloadInfo.path = downloadInfo.getFilePath();
        return downloadInfo;
    }

    // 获取下载进度(0-1)
    public float getProgress() {
        if (size == 0) {
            return 0;
        }

        float progress = currentPos / (float) size;
        return progress;
    }

    // 获取文件下载路径
    public String getFilePath() {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuffer sb = new StringBuffer();
        sb.append(sdcard);
        sb.append(File.separator);
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DONWLOAD);
        if(createDir(sb.toString())) {
            return sb.toString() + File.separator + name + ".apk";// 返回文件路径
        }
        return null;
    }

    public boolean createDir(String dir) {
        File file = new File(dir);
        if(!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }
}

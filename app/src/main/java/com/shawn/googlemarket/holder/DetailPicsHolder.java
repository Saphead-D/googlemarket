package com.shawn.googlemarket.holder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.ScreenActivity;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/20.
 */
public class DetailPicsHolder extends BaseHolder<AppInfo> {

    private ImageView[] ivPics;
    private BitmapUtils mBitmapUtils;
    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_picinfo);
        ivPics = new ImageView[5];
        ivPics[0] = (ImageView) view.findViewById(R.id.iv_pic1);
        ivPics[1] = (ImageView) view.findViewById(R.id.iv_pic2);
        ivPics[2] = (ImageView) view.findViewById(R.id.iv_pic3);
        ivPics[3] = (ImageView) view.findViewById(R.id.iv_pic4);
        ivPics[4] = (ImageView) view.findViewById(R.id.iv_pic5);
        mBitmapUtils = BitmapHelper.getBitmapUtils();
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        final ArrayList<String> screen = data.screen;

        for (int i = 0; i < 5; i++) {
            if(i < screen.size()) {
                mBitmapUtils.display(ivPics[i], HttpHelper.PATH + "image?name=" + screen.get(i));

                final int index = i;
                ivPics[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UIUtils.getContext(), ScreenActivity.class);
                        intent.putExtra("index", index);
                        intent.putStringArrayListExtra("picList",screen);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        UIUtils.getContext().startActivity(intent);
                    }
                });
            }else {
                ivPics[i].setVisibility(View.GONE);
            }
        }
    }
}

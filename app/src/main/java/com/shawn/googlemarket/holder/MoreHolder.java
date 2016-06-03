package com.shawn.googlemarket.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawn.googlemarket.R;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/14.
 */
public class MoreHolder extends BaseHolder<Integer> {
    public static final int STATE_MORE_MORE = 0;
    public static final int STATE_MORE_ERROR = 1;
    public static final int STATE_MORE_NONE = 2;
    private LinearLayout ll_load_more;
    private TextView tv_load_error;

    public MoreHolder(boolean hasMore) {
        setData(hasMore?STATE_MORE_MORE:STATE_MORE_NONE);
    }

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_more);
        ll_load_more = (LinearLayout) view.findViewById(R.id.ll_load_more);
        tv_load_error = (TextView) view.findViewById(R.id.tv_load_error);
        return view;
    }

    @Override
    public void refreshView(Integer data) {
        switch (data) {
            case  STATE_MORE_MORE:
                ll_load_more.setVisibility(View.VISIBLE);
                tv_load_error.setVisibility(View.GONE);
                break;
            case  STATE_MORE_NONE:
                ll_load_more.setVisibility(View.GONE);
                tv_load_error.setVisibility(View.GONE);
                break;
            case  STATE_MORE_ERROR:
                ll_load_more.setVisibility(View.GONE);
                tv_load_error.setVisibility(View.VISIBLE);
                break;
            default:break;
        }
    }
}

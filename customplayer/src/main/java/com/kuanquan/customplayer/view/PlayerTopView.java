package com.kuanquan.customplayer.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuanquan.customplayer.R;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放头部栏控件
 */
public class PlayerTopView extends FrameLayout {

    private RelativeLayout mRelativeLayout;
    private ImageView mImageView;
    private TextView mTextView;

    public PlayerTopView(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayerTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_topbar, this, true);
        mRelativeLayout = rootView.findViewById(R.id.play_top_bar);
        mImageView = rootView.findViewById(R.id.play_top_bar_back);
        mTextView = rootView.findViewById(R.id.play_top_bar_title);
    }

    public ImageView getImageView(){
        return mImageView;
    }

    public void setTitle(String title){
        if (!TextUtils.isEmpty(title)) {
            mTextView.setText(title);
        }else {
            mTextView.setText("");
        }
    }

}

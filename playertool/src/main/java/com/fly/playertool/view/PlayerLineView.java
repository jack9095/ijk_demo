package com.fly.playertool.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fly.playertool.R;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放底部栏控件
 */
public class PlayerLineView extends FrameLayout {

    public PlayerLineView(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayerLineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerLineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_line, this, true);
    }

}

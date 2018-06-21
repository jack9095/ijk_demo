package com.kuanquan.ijk_demo;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fly.playertool.common.PlayerManager;
import com.fly.playertool.utils.CommonUtil;
import com.fly.playertool.utils.DisplayUtil;
import com.fly.playertool.utils.ScreenRotateUtil;
import com.fly.playertool.utils.UrlUtil;
import com.fly.playertool.view.BasePlayerView;
import com.fly.playertool.view.PlayerView;
import com.fly.playertool.widget.IjkVideoView;

public class PlayActivity extends AppCompatActivity implements PlayerManager.PlayerStateListener, ScreenRotateUtil.ScreenRotateListener, BasePlayerView.PlayerListener {

    private PlayerManager player;
    private PlayerView mPlayerView;
    private RelativeLayout video_view_layout;
    private View rootView;
    private ScreenRotateUtil mScreenRotateUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_play, null);
        setContentView(rootView);
        mScreenRotateUtil = new ScreenRotateUtil(this, this);
        mScreenRotateUtil.enable();
        CommonUtil.setViewTreeObserver(rootView); // 虚拟按键的隐藏方法
        mPlayerView = findViewById(R.id.video_view);
        mPlayerView.setPlayerListener(this);
        video_view_layout = findViewById(R.id.video_view_layout);
        initPlayer();
    }

    private void initPlayer() {
        //初始化播放器
        player = new PlayerManager(mPlayerView.mIjkVideoView, this);
        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
        player.setPlayerStateListener(this);
        player.live(false);
        player.play(UrlUtil.url4);
        mPlayerView.centerPlay.setVisibility(View.GONE);
        mPlayerView.startPlayerUI();
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onPlay() {
        mPlayerView.playing();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            player.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.start();
    }

    @Override
    public void onPortrait() {
        setLayoutParams(DisplayUtil.dip2px(this, 202));
    }

    @Override
    public void onLandscape() {
        setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onReverseLandscape() {
        setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void setLayoutParams(int matchParent) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) video_view_layout.getLayoutParams();
        params.height = matchParent;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        video_view_layout.setLayoutParams(params);//将设置好的布局参数应用到控件中
    }

    @Override
    public void goBack() {
        if (ScreenRotateUtil.isLandscape(this)) {
            mScreenRotateUtil.manualSwitchingPortrait();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        goBack();
    }

    @Override
    public void screen(int type) {
        if (type == 1) { // 到横屏
            mScreenRotateUtil.manualSwitchingLandscape();
        } else {
            mScreenRotateUtil.manualSwitchingPortrait();
        }

    }
}

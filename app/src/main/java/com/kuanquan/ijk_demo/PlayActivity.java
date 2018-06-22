package com.kuanquan.ijk_demo;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.fly.playertool.common.PlayerManager;
import com.fly.playertool.utils.CommonUtil;
import com.fly.playertool.utils.DisplayUtil;
import com.fly.playertool.utils.MediaUtils;
import com.fly.playertool.utils.ScreenRotateUtil;
import com.fly.playertool.utils.ScreenWakeLockUtil;
import com.fly.playertool.utils.UrlUtil;
import com.fly.playertool.view.BasePlayerView;
import com.fly.playertool.view.PlayerView;

public class PlayActivity extends AppCompatActivity implements PlayerManager.PlayerStateListener, ScreenRotateUtil.ScreenRotateListener, BasePlayerView.PlayerListener {

    private PlayerManager player;
    private PlayerView mPlayerView;
    private ScreenRotateUtil mScreenRotateUtil;
    private ScreenWakeLockUtil mScreenWakeLockUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().from(this).inflate(R.layout.activity_play, null);
        setContentView(rootView);
        mScreenRotateUtil = new ScreenRotateUtil(this, this);
        mScreenRotateUtil.enable();
        CommonUtil.setViewTreeObserver(rootView); // 虚拟按键的隐藏方法
        mScreenWakeLockUtil = new ScreenWakeLockUtil(this);
        mScreenWakeLockUtil.onCreate();
        mPlayerView = findViewById(R.id.video_view);
        mPlayerView.setPlayerRotation();
        mPlayerView.setPlayerListener(this);
        mPlayerView.setActivity(this);
        initPlayer();

        mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PlayActivity","点击");
                mPlayerView.hideShowViewAll();
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PlayActivity","点击");
            }
        });
    }

    private void initPlayer() {  //初始化播放器管理
        player = new PlayerManager(mPlayerView.mIjkVideoView, this);
        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
        player.setPlayerStateListener(this);
        player.live(false);
        player.play(UrlUtil.url4);
        mPlayerView.centerPlay.setVisibility(View.GONE);
    }

    @Override
    public void onComplete() {
        mPlayerView.pausePlayerUI();
    }

    @Override
    public void onError() {
        mPlayerView.pausePlayerUI();
    }

    @Override
    public void onLoading() {
        mPlayerView.pausePlayerUI();
    }

    @Override
    public void onPlay() {
        mPlayerView.startPlayerUI();
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
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
        params.height = DisplayUtil.dip2px(this, 202);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        mPlayerView.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mPlayerView.mPlayerBottomView.getLineView().setVisibility(View.GONE);

    }

    @Override
    public void onLandscape() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        mPlayerView.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mPlayerView.mPlayerBottomView.getLineView().setVisibility(View.VISIBLE);
    }

    @Override
    public void goBack() {
        if (ScreenRotateUtil.isLandscape(this)) {
            mScreenRotateUtil.setBack(true);
            mScreenRotateUtil.manualSwitchingPortrait();
        } else {
            mScreenWakeLockUtil.finish();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenRotateUtil.disable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaUtils.muteAudioFocus(this, true); // 恢复系统其它媒体的状态
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaUtils.muteAudioFocus(this, false);  // 暂停系统其它媒体的状态
        mScreenWakeLockUtil.onResume();
    }
}
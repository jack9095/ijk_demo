package com.kuanquan.ijk_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import com.kuanquan.playerlibrary.utils.MediaUtils;
import com.kuanquan.playerlibrary.utils.ScreenRotateUtil;
import com.kuanquan.playerlibrary.utils.ScreenUtils;
import com.kuanquan.playerlibrary.utils.ScreenWakeLockUtil;
import com.kuanquan.playerlibrary.utils.UrlUtil;
import com.kuanquan.playerlibrary.view.VideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 较为完美版的
 */
public class CommonVideoActivity extends AppCompatActivity implements ScreenRotateUtil.ScreenRotateListener, VideoView.PlayerListener{

    private VideoView mVideoView;
    private ScreenRotateUtil mScreenRotateUtil;
    private ScreenWakeLockUtil mScreenWakeLockUtil;
    private int height; // 屏幕高度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_video);
        mVideoView = findViewById(R.id.common_player);
        height = ScreenUtils.getScreenHeight(this);
        mVideoView.setVideoheight(mVideoView, height / 3);
        mScreenRotateUtil = new ScreenRotateUtil(this, this);
        mScreenRotateUtil.enable();
        mVideoView.setStartIjkMediaPlayer();
        mScreenWakeLockUtil = new ScreenWakeLockUtil(this);
        mScreenWakeLockUtil.onCreate();
        mVideoView.setPlayerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        mVideoView.setPath(UrlUtil.url4);
    }

    @Override
    public void onPortrait() {
        mVideoView.setVideoheight(mVideoView,height / 3);
        mVideoView.mPlayerBottomView.getLineView().setVisibility(View.GONE);

    }

    @Override
    public void onLandscape() {
        mVideoView.setVideoheight(mVideoView,ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoView.mPlayerBottomView.getLineView().setVisibility(View.VISIBLE);
    }

    @Override
    public void goBack() {
        if (ScreenRotateUtil.isLandscape(this)) {
            mVideoView.setVideoPath(UrlUtil.url5);
//            mScreenRotateUtil.setBack(true);
//            mScreenRotateUtil.manualSwitchingPortrait();
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
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenRotateUtil.disable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
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

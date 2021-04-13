package com.kuanquan.customplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kuanquan.customplayer.utils.CommonUtil;
import com.kuanquan.customplayer.utils.DisplayUtil;
import com.kuanquan.customplayer.utils.LogUtil;
import com.kuanquan.customplayer.utils.MediaUtils;
import com.kuanquan.customplayer.utils.ScreenRotateUtil;
import com.kuanquan.customplayer.utils.ScreenUtils;
import com.kuanquan.customplayer.utils.ScreenWakeLockUtil;
import com.kuanquan.customplayer.utils.UrlUtil;
import com.kuanquan.customplayer.view.BasePlayerView;
import com.kuanquan.customplayer.view.PlayerView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

//public class CustomPlayerActivity extends AppCompatActivity implements ScreenRotateUtil.ScreenRotateListener, BasePlayerView.PlayerListener{
public class CustomPlayerActivity extends AppCompatActivity {
    private PlayerView mPlayerView;
//    private ScreenRotateUtil mScreenRotateUtil;
//    private ScreenWakeLockUtil mScreenWakeLockUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_custom_player);
        View rootView = getLayoutInflater().from(this).inflate(R.layout.activity_custom_player, null);
        setContentView(rootView);
//        mScreenRotateUtil = new ScreenRotateUtil(this, this);
//        mScreenRotateUtil.enable();
        CommonUtil.setViewTreeObserver(rootView); // 虚拟按键的隐藏方法
//        mScreenWakeLockUtil = new ScreenWakeLockUtil(this);
//        mScreenWakeLockUtil.onCreate();
        mPlayerView = findViewById(R.id.video_view);
//        mPlayerView.setPlayerListener(this);
        mPlayerView.setActivity(this);
        mPlayerView.setStartIjkMediaPlayer();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        initPlayer();

        mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerView.hideShowViewAll();
                LogUtil.e("***** 最外层布局点击事件 ***** = ",indexnnn++);
            }
        });
    }
    int indexnnn;
    private void initPlayer() {  //初始化播放器管理
        mPlayerView.initSurfaceView();
        mPlayerView.setVideoPath(UrlUtil.url4);
        mPlayerView.start();
        mPlayerView.centerPlay.setVisibility(View.GONE);
    }

//    @Override
//    public void onPortrait() {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
//        params.height = ScreenUtils.getScreenHeight(this) / 3;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
//        mPlayerView.setLayoutParams(params);//将设置好的布局参数应用到控件中
//
//        mPlayerView.mPlayerBottomView.getLineView().setVisibility(View.GONE);
//
//    }
//
//    @Override
//    public void onLandscape() {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
//        mPlayerView.setLayoutParams(params);//将设置好的布局参数应用到控件中
//
//        mPlayerView.mPlayerBottomView.getLineView().setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void goBack() {
//        if (ScreenRotateUtil.isLandscape(this)) {
//            mScreenRotateUtil.setBack(true);
//            mScreenRotateUtil.manualSwitchingPortrait();
//        } else {
//            mScreenWakeLockUtil.finish();
//            finish();
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        goBack();
//    }
//
//    @Override
//    public void screen(int type) {
//        if (type == 1) { // 到横屏
//            mScreenRotateUtil.manualSwitchingLandscape();
//        } else {
//            mScreenRotateUtil.manualSwitchingPortrait();
//        }
//    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            mPlayerView.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPlayerView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mScreenRotateUtil.disable();
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
//        mScreenWakeLockUtil.onResume();
    }
}

package com.kuanquan.customplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.kuanquan.customplayer.utils.ScreenRotateUtil;
import com.kuanquan.customplayer.utils.ScreenUtils;
import com.kuanquan.customplayer.utils.ScreenWakeLockUtil;
import com.kuanquan.customplayer.utils.UrlUtil;
import com.kuanquan.customplayer.view.PlayerBottomView;
import com.kuanquan.customplayer.view.PlayerTopView;
import com.kuanquan.customplayer.widget.VideoPlayerIJK;
import com.kuanquan.customplayer.widget.VideoPlayerListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity implements ScreenRotateUtil.ScreenRotateListener{
    public PlayerTopView mPlayerTopView;         // 顶部栏
    public PlayerBottomView mPlayerBottomView;         // 底部栏
    VideoPlayerIJK ijkPlayer;
    private int index;
    private ScreenRotateUtil mScreenRotateUtil;
    private ScreenWakeLockUtil mScreenWakeLockUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        //加载so文件
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
//            this.finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        mScreenRotateUtil = new ScreenRotateUtil(this, this);
        mScreenRotateUtil.enable();
        mScreenWakeLockUtil = new ScreenWakeLockUtil(this);
        mScreenWakeLockUtil.onCreate();
        ijkPlayer = findViewById(R.id.ijk_player);
        mPlayerTopView = findViewById(R.id.player_view_player_play_top_view);
        mPlayerBottomView = findViewById(R.id.player_view_player_play_bottom_view);
        ijkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("PlayActivity","播放器点击事件 = " + index++);
                hideShowViewAll();
            }
        });

        Log.e("VideoActivity","********onCreate*******w");
        ijkPlayer.setVideoPath(UrlUtil.url4);
        ijkPlayer.createSurfaceView();
//        ijkPlayer.load();
//        ijkPlayer.start();
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
//                mp.seekTo(0);
//                mp.start();
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                // 下面是部分 what 值的含义
//                int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
//                int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
//                int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
//                int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
//                int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
//                int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
//                int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
            }

            @Override
            public void onSeekComplete(IMediaPlayer mp) {

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                //获取到视频的宽和高
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * 显示标题状态栏
     */
    public void hideShowViewAll() {
        if (mPlayerTopView.getVisibility() == View.VISIBLE) {
            mPlayerTopView.setVisibility(View.GONE);    // 顶部标题栏布局的控制
        }else{
            mPlayerTopView.setVisibility(View.VISIBLE);    // 顶部标题栏布局的控制
        }

        if (mPlayerBottomView.getVisibility() == View.VISIBLE) {
            mPlayerBottomView.setVisibility(View.GONE);    // 底部标题栏布局的控制
        }else{
            mPlayerBottomView.setVisibility(View.VISIBLE);    // 底部标题栏布局的控制
        }
    }

    @Override
    public void onPortrait() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ijkPlayer.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(this) / 3;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        ijkPlayer.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mPlayerBottomView.getLineView().setVisibility(View.GONE);

    }

    @Override
    public void onLandscape() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ijkPlayer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        ijkPlayer.setLayoutParams(params);//将设置好的布局参数应用到控件中

        mPlayerBottomView.getLineView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (ScreenRotateUtil.isLandscape(this)) {
            mScreenRotateUtil.setBack(true);
            mScreenRotateUtil.manualSwitchingPortrait();
        } else {
            mScreenWakeLockUtil.finish();
            ijkPlayer.stop();
            onStop();
            finish();
        }
    }
}

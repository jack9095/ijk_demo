package com.kuanquan.ijk_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.fly.playertool.common.PlayerManager;
import com.fly.playertool.utils.ScreenUtils;
import com.fly.playertool.widget.IjkVideoView;

public class PlayActivity extends AppCompatActivity implements PlayerManager.PlayerStateListener{
    private String url1 = "rtmp://203.207.99.19:1935/live/CCTV5";
    private String url3 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url4 = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";
    private String url5 = "http://mp4.vjshi.com/2013-05-28/2013052815051372.mp4";
    private String url6 = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
    private PlayerManager player;
    private IjkVideoView mIjkVideoView;
    private RelativeLayout video_view_layout;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_play, null);
        setContentView(rootView);
        /**虚拟按键的隐藏方法*/
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                //比较Activity根布局与当前布局的大小
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
            }
        });
        mIjkVideoView = findViewById(R.id.video_view);
        video_view_layout = findViewById(R.id.video_view_layout);
//        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) video_view_layout.getLayoutParams();
//        params.height = ScreenUtils.getScreenHeight(this) / 3;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
//        video_view_layout.setLayoutParams(params);//将设置好的布局参数应用到控件中
        initPlayer();
    }

    private void initPlayer() {
        //初始化播放器
        player = new PlayerManager(mIjkVideoView,this);
        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
        player.setPlayerStateListener(this);
        player.live(false);
        player.play(url4);
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
}

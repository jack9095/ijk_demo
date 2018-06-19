package com.kuanquan.ijk_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import common.PlayerManager;
import widget.IjkVideoView;

public class MainActivity extends AppCompatActivity implements PlayerManager.PlayerStateListener{
    private String url1 = "rtmp://203.207.99.19:1935/live/CCTV5";
    private String url2 = "http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8";
    private String url3 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url4 = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";
    private String url5 = "http://mp4.vjshi.com/2013-05-28/2013052815051372.mp4";
    private String url6 = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
    private PlayerManager player;
    private IjkVideoView mIjkVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIjkVideoView = findViewById(R.id.video_view);
        initPlayer();
    }

    private void initPlayer() {
        //初始化播放器
        player = new PlayerManager(mIjkVideoView);
//        player.setFullScreenOnly(false);
        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
        player.playInFullScreen(false);
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
        if (level == TRIM_MEMORY_BACKGROUND) {

        }
    }
}

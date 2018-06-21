package com.kuanquan.ijk_demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.fly.playertool.bean.VideoijkBean;
import com.fly.playertool.utils.CommonUtil;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.MediaUtils;
import com.fly.playertool.utils.ScreenUtils;
import com.fly.playertool.view.BasePlayerView;
import com.fly.playertool.view.PlayerView;
import com.fly.playertool.widget.PlayStateParams;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BasePlayerView.PlayerListener {
    private String url1 = "rtmp://203.207.99.19:1935/live/CCTV5";
    private String url3 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url4 = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";
    private String url5 = "http://mp4.vjshi.com/2013-05-28/2013052815051372.mp4";
    private String url6 = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";

    private PlayerView player;
    private Context mContext;
    private List<VideoijkBean> list;
    private PowerManager.WakeLock wakeLock;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_main, null);
        setContentView(rootView);
        player = findViewById(R.id.video_view);
        player.setStartIjkMediaPlayer();
        player.setPlayerListener(this);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) player.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight(this) / 3;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        player.setLayoutParams(params);//将设置好的布局参数应用到控件中
//        initPlayer();
        LogUtil.e("onCreate++++++++++++onCreate");
    }

//    private void initPlayer() {
//        // 虚拟按键的隐藏方法
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                //比较Activity根布局与当前布局的大小
//                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
//                if (heightDiff > 100) {
//                    //大小超过100时，一般为显示虚拟键盘事件
//                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                } else {
//                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
//                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                }
//            }
//        });
//
//        // 常亮
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
//        wakeLock.acquire();
//        list = new ArrayList<>();
//        //有部分视频加载有问题，这个视频是有声音显示不出图像的，没有解决http://fzkt-biz.oss-cn-hangzhou.aliyuncs.com/vedio/2f58be65f43946c588ce43ea08491515.mp4
//        //这里模拟一个本地视频的播放，视频需要将testvideo文件夹的视频放到安卓设备的内置sd卡根目录中
//        String url1 = CommonUtil.getLocalVideoPath("my_video.mp4");
//        if (!new File(url1).exists()) {
//            url1 = "http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4";
//        }
//        VideoijkBean m1 = new VideoijkBean();
//        m1.setStream("标清");
//        m1.setUrl(url1);
//        VideoijkBean m2 = new VideoijkBean();
//        m2.setStream("高清");
//        m2.setUrl(url4);
//        list.add(m1);
//        list.add(m2);
//        player = new PlayerView(this);
//        player.setTitle("美食");
//        player.setScaleType(PlayStateParams.fillparent);
//        player.setPlaySource(list);
//        player.setChargeTie(true,60000);
//        player.startPlay();
//
//        Glide.with(mContext)
//                .load("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3909870665,3259015587&fm=27&gp=0.jpg")
//                .placeholder(R.color.cl_default)
//                .error(R.color.cl_error)
//                .into(player.cover);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        // 恢复系统其它媒体的状态
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        // 暂停系统其它媒体的状态
        MediaUtils.muteAudioFocus(mContext, false);
        // 激活设备常亮状态
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null) {
            return;
        }
        super.onBackPressed();
        // 恢复设备亮度状态
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            player.onPause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.onResume();
    }

    @Override
    public void goBack() {
//        finish();
        startActivity(new Intent(this,PlayActivity.class));
    }

    @Override
    public void screen(int type) {

    }
}

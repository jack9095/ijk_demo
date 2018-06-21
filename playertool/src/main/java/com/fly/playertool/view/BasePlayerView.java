package com.fly.playertool.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fly.playertool.R;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.NetworkUtils;
import com.fly.playertool.utils.ScreenUtils;
import com.fly.playertool.widget.IjkVideoView;
import com.fly.playertool.widget.PlayStateParams;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class BasePlayerView extends FrameLayout implements View.OnClickListener {
    public IjkVideoView mIjkVideoView; // 播放器的封装 (原生的Ijkplayer)
    public ImageView cover;            // 播放器的封面图片
    public LinearLayout replayLayout;         // 重新播放的布局
    public ImageView replayImage;         // 重新播放的图标
    public LinearLayout netTieLayout;         // 网络提示的布局
    public TextView netTieText;         // 网络提示的 继续按钮
    public LinearLayout freeTieLayout;         // 最大试看时长的布局
    public TextView freeTieText;         // 最大试看时长 购买按钮
    public LinearLayout loadingLayout;         // 加载的布局
    public TextView loadingSpeedText;         // 加载中 网络速度
    public PlayerTopView mPlayerTopView;         // 顶部栏
    public PlayerBottomView mPlayerBottomView;         // 底部栏
    public PlayerLineView mPlayerLineView;         // 分辨率 （高清、标清） 选择
    public ImageView centerPlay;         // 中间的播放标记
    public RelativeLayout mRelativeLayout;         // 播放器的整个界面的布局

    /**
     * 获取当前设备的宽度
     */
    public int screenWidthPixels;


    public BasePlayerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public BasePlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BasePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    protected void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_view_player, this, true);
        mRelativeLayout = rootView.findViewById(R.id.player_view_player);
        mIjkVideoView = rootView.findViewById(R.id.player_view_player_video_view);
        cover = rootView.findViewById(R.id.player_view_player_iv_cover);
        replayLayout = rootView.findViewById(R.id.player_view_player_replay_layout);
        replayImage = rootView.findViewById(R.id.player_view_player_replay_icon);
        netTieLayout = rootView.findViewById(R.id.player_view_player_netTie);
        netTieText = rootView.findViewById(R.id.player_view_player_netTie_icon);
        freeTieLayout = rootView.findViewById(R.id.player_view_player_video_freeTie);
        freeTieText = rootView.findViewById(R.id.player_view_player_video_freeTie_icon);
        loadingLayout = rootView.findViewById(R.id.player_view_player_loading);
        loadingSpeedText = rootView.findViewById(R.id.player_view_player_speed);
        mPlayerTopView = rootView.findViewById(R.id.player_view_player_play_top_view);
        mPlayerBottomView = rootView.findViewById(R.id.player_view_player_play_bottom_view);
        mPlayerLineView = rootView.findViewById(R.id.player_view_player_play_line_view);
        centerPlay = rootView.findViewById(R.id.player_view_player_center_icon);

        mPlayerBottomView.setIjkVideoView(mIjkVideoView);
        replayImage.setOnClickListener(this);
        netTieText.setOnClickListener(this);
        freeTieText.setOnClickListener(this);
        mPlayerTopView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getZoomView().setOnClickListener(this);
        mPlayerBottomView.getLineView().setOnClickListener(this);

        screenWidthPixels = ScreenUtils.getScreenWidth(getContext());
    }

    /**
     * 设置视频名称
     */
    public void setTitle(String title) {
        mPlayerTopView.setTitle(title);
    }

    /**
     * 设置播放位置
     */
    public void seekTo(int playtime) {
        mIjkVideoView.seekTo(playtime);
    }

    /**
     * 播放的时候是否需要网络提示，默认显示网络提示，true为显示网络提示，false不显示网络提示
     */
    protected boolean isGNetWork = true;

    /**
     * 设置2/3/4/5G和WiFi网络类型提示，
     *
     * @param isGNetWork true为进行2/3/4/5G网络类型提示
     *                   false 不进行网络类型提示
     */
    public void setNetWorkTypeTie(boolean isGNetWork) {
        this.isGNetWork = isGNetWork;
    }

    /**
     * 隐藏所有界面
     */
    protected void hideAll() {
        mPlayerTopView.setVisibility(View.GONE);    // 顶部标题栏布局的控制
        mPlayerBottomView.setVisibility(View.GONE); // 底部导航栏布局的控制
        hideStatusUI();
    }

    /**
     * 隐藏状态界面
     */
    protected void hideStatusUI() {
        centerPlay.setVisibility(View.GONE);     // 中间播放按钮布局的控制
        mPlayerLineView.setVisibility(View.GONE);// 分辨率布局的控制
        replayLayout.setVisibility(View.GONE);   // 重新播放布局的控制
        netTieLayout.setVisibility(View.GONE);   // 网络提示布局的控制
        freeTieLayout.setVisibility(View.GONE);  // 最大试看时长提示布局的控制
        loadingLayout.setVisibility(View.GONE);  // 加载中布局的控制
    }

    public void onPause() {
        mIjkVideoView.pause();
    }

    /**
     * 暂停状态界面
     */
    public void pausePlayerUI() {
        mPlayerBottomView.getImageView().setImageResource(R.drawable.player_left_bottom_play);
        centerPlay.setImageResource(R.drawable.player_center_play);
    }

    /**
     * 暂停状态界面
     */
    public void startPlayerUI() {
        mPlayerBottomView.getImageView().setImageResource(R.drawable.player_left_bottom_pause);
        centerPlay.setImageResource(R.drawable.player_center_pause);
    }

    public void onResume() {
//        mIjkVideoView.resume();
//        mIjkVideoView.seekTo(currentPosition);
    }

    public void onDestroy() {
        mIjkVideoView.stopPlayback();
    }

    /**
     * 同步进度
     */
    public long syncProgress() {
        long position = mIjkVideoView.getCurrentPosition(); // 当前时长
        long duration = mIjkVideoView.getDuration();  // 视频总时长
        LogUtil.e(" ***总时长 = *** " + duration);
        LogUtil.e(" ***开始播放 = *** " + position);
        mPlayerBottomView.setTotalTime(duration);
        mPlayerBottomView.setCurrentTime(position);
        mPlayerBottomView.setSeekBarTo(position);
        return position;
    }

    /**
     * 视频播放控件事件回调
     */
    public PlayerListener mPlayerListener;

    /**
     * 设置播放控件事件监听回调
     */
    public void setPlayerListener(PlayerListener playerListener) {
        this.mPlayerListener = playerListener;
    }

    @Override
    public void onClick(View view) {

    }

    public interface PlayerListener {
        void goBack();      // 播放器左上角返回按钮

        void screen(int type);      // 切换横竖屏  type 1 横屏  2竖屏

    }

    /**
     * 状态改变同步UI
     */
    public void statusChange(int newStatus) {
        if (newStatus == PlayStateParams.STATE_COMPLETED) {
            LogUtil.e("播放结束");
        } else if (newStatus == PlayStateParams.STATE_PREPARING
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
            // 视频缓冲
            hideStatusUI();
            loadingLayout.setVisibility(View.VISIBLE); // 显示加载布局
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
                || newStatus == PlayStateParams.STATE_PLAYING
                || newStatus == PlayStateParams.STATE_PREPARED
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
                || newStatus == PlayStateParams.STATE_PAUSED) {

            // 视频缓冲结束后隐藏缩列图
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideAll();
                    // 延迟0.5秒隐藏视频封面隐藏
                    cover.setVisibility(View.GONE);
                }
            }, 500);
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_INTERRUPT) {
            // 直播停止推流
            if (!(isGNetWork &&
                    (NetworkUtils.getNetworkType(getContext()) == 4
                            || NetworkUtils.getNetworkType(getContext()) == 5
                            || NetworkUtils.getNetworkType(getContext()) == 6))) {
            } else {
                netTieLayout.setVisibility(VISIBLE);
            }
        } else if (newStatus == PlayStateParams.STATE_ERROR
                || newStatus == PlayStateParams.MEDIA_INFO_UNKNOWN
                || newStatus == PlayStateParams.MEDIA_ERROR_IO
                || newStatus == PlayStateParams.MEDIA_ERROR_MALFORMED
                || newStatus == PlayStateParams.MEDIA_ERROR_UNSUPPORTED
                || newStatus == PlayStateParams.MEDIA_ERROR_TIMED_OUT
                || newStatus == PlayStateParams.MEDIA_ERROR_SERVER_DIED) {

            netTieLayout.setVisibility(VISIBLE);

        }
    }

    public void setStartIjkMediaPlayer(){
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            LogUtil.e("GiraffePlayer" + e);
        }
    }
}

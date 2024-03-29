package com.kuanquan.customplayer.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuanquan.customplayer.R;
import com.kuanquan.customplayer.widget.VideoPlayerIJK;
import com.kuanquan.customplayer.utils.LogUtil;
import com.kuanquan.customplayer.utils.ScreenUtils;
import com.kuanquan.customplayer.widget.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class BasePlayerView extends FrameLayout implements View.OnClickListener {
    public VideoPlayerIJK mIjkVideoView; // 播放器的封装 (原生的Ijkplayer)
    public ImageView cover;            // 播放器的封面图片
//    public LinearLayout replayLayout;         // 重新播放的布局
//    public ImageView replayImage;         // 重新播放的图标
//    public LinearLayout netTieLayout;         // 网络提示的布局
//    public TextView netTieText;         // 网络提示的 继续按钮
//    public LinearLayout freeTieLayout;         // 最大试看时长的布局
//    public TextView freeTieText;         // 最大试看时长 购买按钮
    public LinearLayout loadingLayout;         // 加载的布局
    public TextView loadingSpeedText;         // 加载中 网络速度
    public PlayerTopView mPlayerTopView;         // 顶部栏
    public PlayerBottomView mPlayerBottomView;         // 底部栏
    public PlayerLineView mPlayerLineView;         // 分辨率 （高清、标清） 选择
    public ImageView centerPlay;         // 中间的播放标记
    public RelativeLayout mRelativeLayout;         // 播放器的整个界面的布局

    // 触摸手势
    public LinearLayout volumeLinearLayout;// 声音布局
    public ImageView volumeIcon;// 声音icon
    public TextView volumeText;// 声音大小

    public LinearLayout brightnessLinearLayout;// 亮度布局
    public ImageView brightnessIcon;// 亮度icon
    public TextView brightnessText;// 亮度大小

    public LinearLayout fastForwardLinearLayout;// 快进快退布局
    public TextView fastForwardText;// 快进或者快退了多少
    public TextView currentTimePosition;// 当前的播放点
    public TextView totalTimePosition;// 视频总时长

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
//        replayLayout = rootView.findViewById(R.id.player_view_player_replay_layout);
//        replayImage = rootView.findViewById(R.id.player_view_player_replay_icon);
//        netTieLayout = rootView.findViewById(R.id.player_view_player_netTie);
//        netTieText = rootView.findViewById(R.id.player_view_player_netTie_icon);
//        freeTieLayout = rootView.findViewById(R.id.player_view_player_video_freeTie);
//        freeTieText = rootView.findViewById(R.id.player_view_player_video_freeTie_icon);
        loadingLayout = rootView.findViewById(R.id.player_view_player_loading);
        loadingSpeedText = rootView.findViewById(R.id.player_view_player_speed);
        mPlayerTopView = rootView.findViewById(R.id.player_view_player_play_top_view);
        mPlayerBottomView = rootView.findViewById(R.id.player_view_player_play_bottom_view);
        mPlayerLineView = rootView.findViewById(R.id.player_view_player_play_line_view);
        centerPlay = rootView.findViewById(R.id.player_view_player_center_icon);
        volumeLinearLayout = rootView.findViewById(R.id.player_touch_gestures_volume_ll);
        volumeIcon = rootView.findViewById(R.id.player_touch_gestures_volume_icon);
        volumeText = rootView.findViewById(R.id.player_touch_gestures_volume);
        brightnessLinearLayout = rootView.findViewById(R.id.player_touch_gestures_brightness_ll);
        brightnessIcon = rootView.findViewById(R.id.player_touch_gestures_brightness_icon);
        brightnessText = rootView.findViewById(R.id.player_touch_gestures_brightness);
        fastForwardLinearLayout = rootView.findViewById(R.id.player_touch_gestures_fastForward_ll);
        fastForwardText = rootView.findViewById(R.id.player_touch_gestures_fastForward);
        currentTimePosition = rootView.findViewById(R.id.player_touch_gestures_fastForward_target);
        totalTimePosition = rootView.findViewById(R.id.player_touch_gestures_fastForward_all);

        mPlayerBottomView.setIjkVideoView(mIjkVideoView);
//        replayImage.setOnClickListener(this);
//        netTieText.setOnClickListener(this);
//        freeTieText.setOnClickListener(this);
        mPlayerTopView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getZoomView().setOnClickListener(this);
        mPlayerBottomView.getLineView().setOnClickListener(this);
//        mRelativeLayout.setEnabled(true);
//        mRelativeLayout.setOnClickListener(this);

        screenWidthPixels = ScreenUtils.getScreenWidth(getContext());

        // TODO 播放器回调监听
        mIjkVideoView.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {
                mp.seekTo(0);
                mp.start();
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
                mp.start(); // 加载好开始播放
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

    // 加载源开始播放
    public void setVideoPath(String path) {
        mIjkVideoView.setVideoPath(path);
    }

    // 初始化的时候创建一次SurfaceView就好
    public void initSurfaceView(){
        mIjkVideoView.createSurfaceView();
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
    protected void hideViewAll() {
        mPlayerTopView.setVisibility(View.GONE);    // 顶部标题栏布局的控制
        mPlayerBottomView.setVisibility(View.GONE); // 底部导航栏布局的控制
        hideStatusUI();
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

    /**
     * 隐藏状态界面
     */
    protected void hideStatusUI() {
        centerPlay.setVisibility(View.GONE);     // 中间播放按钮布局的控制
        mPlayerLineView.setVisibility(View.GONE);// 分辨率布局的控制
//        replayLayout.setVisibility(View.GONE);   // 重新播放布局的控制
//        netTieLayout.setVisibility(View.GONE);   // 网络提示布局的控制
//        freeTieLayout.setVisibility(View.GONE);  // 最大试看时长提示布局的控制
        loadingLayout.setVisibility(View.GONE);  // 加载中布局的控制
    }

    /**
     * 暂停状态界面
     */
    public void pausePlayerUI() {
        mPlayerBottomView.getImageView().setImageResource(R.drawable.player_left_bottom_play);
        centerPlay.setImageResource(R.drawable.player_center_play);
    }

    /**
     * 播放状态界面
     */
    public void startPlayerUI() {
        mPlayerBottomView.getImageView().setImageResource(R.drawable.player_left_bottom_pause);
        centerPlay.setImageResource(R.drawable.player_center_pause);
    }

    /**
     * 同步进度
     */
    public long syncProgress() {
        long position = mIjkVideoView.getCurrentPosition(); // 当前时长
        long duration = mIjkVideoView.getDuration();  // 视频总时长
//        LogUtil.e(" ***总时长 = *** " + duration);
//        LogUtil.e(" ***开始播放 = *** " + position);
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

    // 初始化播放器  加载so问件
    public void setStartIjkMediaPlayer(){
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            LogUtil.e("GiraffePlayer" + e);
        }
    }

    // 设置倍速
    public void setSpeed(float speed) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setSpeed(speed);
        }
    }

    // 开始播放
    public void start() {
        if (mIjkVideoView != null) {
            mIjkVideoView.start();
        }
    }

    // 置为空闲
    public void release() {
        if (mIjkVideoView != null) {
            mIjkVideoView.reset();
            mIjkVideoView.release();
            mIjkVideoView = null;
        }
    }

    // 暂停播放
    public void pause() {
        if (mIjkVideoView != null) {
            mIjkVideoView.pause();
        }
    }

    // 停止播放
    public void stop() {
        if (mIjkVideoView != null) {
            mIjkVideoView.stop();
        }
    }

    // 重用处于Error错误状态的MediaPlayer对象，可以通过调用reset()方法，使其恢复到idle空闲状态
    public void reset() {
        if (mIjkVideoView != null) {
            mIjkVideoView.reset();
        }
    }

    // 获取总时长
    public long getDuration() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getDuration();
        } else {
            return 0;
        }
    }

    // 获取当前播放位置
    public long getCurrentPosition() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getCurrentPosition();
        } else {
            return 0;
        }
    }

    // 设置播放时长
    public void seekTo(long l) {
        if (mIjkVideoView != null) {
            mIjkVideoView.seekTo(l);
        }
    }

    // 是否在播放
    public boolean isPlaying() {
        return mIjkVideoView != null && mIjkVideoView.isPlaying();
    }
}

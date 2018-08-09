package com.kuanquan.playerlibrary.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuanquan.playerlibrary.R;
import com.kuanquan.playerlibrary.utils.HandlerWhat;
import com.kuanquan.playerlibrary.utils.LogUtil;
import com.kuanquan.playerlibrary.utils.ScreenUtils;
import com.kuanquan.playerlibrary.widget.VideoPlayerIJK;
import com.kuanquan.playerlibrary.widget.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/24.
 */
public class CommonPlayer extends FrameLayout implements View.OnClickListener {
    public VideoPlayerIJK mIjkVideoView; // 播放器的封装 (原生的Ijkplayer)
    public ImageView cover;            // 播放器的封面图片
    public LinearLayout loadingLayout;         // 加载的布局
    public TextView loadingSpeedText;         // 加载中 网络速度
    public PlayerTopView mPlayerTopView;         // 顶部栏
    public PlayerBottomView mPlayerBottomView;         // 底部栏
    public ImageView centerPlay;         // 中间的播放标记
    public RelativeLayout mRelativeLayout;         // 播放器的整个界面的布局

    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    public long newPosition = -1;
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 滑动完成，设置播放进度
                case HandlerWhat.MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        mIjkVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                // 滑动中，同步播放进度
                case HandlerWhat.MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (mIjkVideoView.isPlaying()) {
                        msg = obtainMessage(HandlerWhat.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                // 重新去播放
                case HandlerWhat.MESSAGE_RESTART_PLAY:
//                    status = PlayStateParams.STATE_ERROR;
//                    startPlay();
//                    updatePausePlay();
                    break;
                case HandlerWhat.MESSAGE_HIDE_CENTER_BOX:
//                    fastForwardLinearLayout.setVisibility(View.GONE);
//                    brightnessLinearLayout.setVisibility(View.GONE);
//                    volumeLinearLayout.setVisibility(View.GONE);
                    break;
                case HandlerWhat.MESSAGE_HIDE_COCNTROLLER:
                    hideViewAll();
                    break;
            }
        }
    };

    /**
     * 获取当前设备的宽度
     */
    public int screenWidthPixels;

    public CommonPlayer(@NonNull Context context) {
        super(context);
        initView();
    }

    public CommonPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    protected void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.common_view_player, this, true);
        mRelativeLayout = rootView.findViewById(R.id.player_view_player);
        mIjkVideoView = rootView.findViewById(R.id.player_view_player_video_view);
        cover = rootView.findViewById(R.id.player_view_player_iv_cover);
        loadingLayout = rootView.findViewById(R.id.player_view_player_loading);
        loadingSpeedText = rootView.findViewById(R.id.player_view_player_speed);
        mPlayerTopView = rootView.findViewById(R.id.player_view_player_play_top_view);
        mPlayerBottomView = rootView.findViewById(R.id.player_view_player_play_bottom_view);
        centerPlay = rootView.findViewById(R.id.player_view_player_center_icon);

        mPlayerBottomView.setIjkVideoView(mIjkVideoView);
        mPlayerTopView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getZoomView().setOnClickListener(this);
        mPlayerBottomView.getLineView().setOnClickListener(this);
        centerPlay.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);

        screenWidthPixels = ScreenUtils.getScreenWidth(getContext());
        mPlayerBottomView.setHandler(mHandler);


        Message msg = mHandler.obtainMessage(HandlerWhat.MESSAGE_HIDE_COCNTROLLER);
        mHandler.sendMessageDelayed(msg, 5000);

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
                if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    playing();
                    startPlayerUI();
                }
                // 下面是部分 what 值的含义
//                int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染  开始播放
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

    // 开始播放掉这个方法（发送消息进度条走）
    public void playing() {
        mHandler.sendEmptyMessage(HandlerWhat.MESSAGE_SHOW_PROGRESS);
    }

    /**
     * 加载源开始播放
     * @param path  播放源
     */
    public void setVideoPath(String path) {
        mIjkVideoView.setVideoPath(path);
        this.type = 0;  // 0 原来的（上一次的）播放路径（默认） 这里设置为0 是为了好暂停和播放
    }

    protected String path;  // 播放路径
    protected int type;  // 0 原来的（上一次的）播放路径（默认） 1 新的播放路径
    // TODO 设置播放路径  只有一进来页面的时候才会调用
    public void setPath(String path){
        this.path = path;
        this.type = 1;
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
        centerPlay.setVisibility(View.GONE);   // 播放器中间的播放按钮
        hideStatusUI();
    }

    /**
     * 显示标题状态栏
     */
    public void hideShowViewAll() {
        if (mPlayerTopView.getVisibility() == View.VISIBLE) {
            mPlayerTopView.setVisibility(View.GONE);    // 顶部标题栏布局的控制
            mHandler.removeMessages(HandlerWhat.MESSAGE_HIDE_COCNTROLLER);
        } else {
            mPlayerTopView.setVisibility(View.VISIBLE);    // 顶部标题栏布局的控制
            Message msg = mHandler.obtainMessage(HandlerWhat.MESSAGE_HIDE_COCNTROLLER);
            mHandler.sendMessageDelayed(msg, 5000);
        }

        if (mPlayerBottomView.getVisibility() == View.VISIBLE) {
            mPlayerBottomView.setVisibility(View.GONE);    // 底部标题栏布局的控制
            centerPlay.setVisibility(View.GONE);   // 播放器中间的播放按钮
        } else {
            mPlayerBottomView.setVisibility(View.VISIBLE);    // 底部标题栏布局的控制
            centerPlay.setVisibility(View.VISIBLE);   // 播放器中间的播放按钮
        }
    }

    /**
     * 隐藏状态界面
     */
    protected void hideStatusUI() {
        centerPlay.setVisibility(View.GONE);     // 中间播放按钮布局的控制
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
    public void setStartIjkMediaPlayer() {
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

    // 设置播放器的高度
    public void setVideoheight(View mView, int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        params.height = height;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;//设置当前控件布局的高度
        mView.setLayoutParams(params);//将设置好的布局参数应用到控件中
    }
}

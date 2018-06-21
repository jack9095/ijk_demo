package com.fly.playertool.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fly.playertool.R;
import com.fly.playertool.bean.VideoijkBean;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.NetworkUtils;
import com.fly.playertool.widget.IjkVideoView;
import com.fly.playertool.widget.PlayStateParams;
import java.util.ArrayList;
import java.util.List;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class BasePlayerView extends FrameLayout implements View.OnClickListener{
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
        playerSupport = true; // 支持设备 后期提供方法外调出去
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_view_player, this, true);
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

        replayImage.setOnClickListener(this);
        netTieText.setOnClickListener(this);
        freeTieText.setOnClickListener(this);
        mPlayerTopView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getZoomView().setOnClickListener(this);
        mPlayerBottomView.getLineView().setOnClickListener(this);
    }

/***************************************************************************************************************
***************************************************************************************************************
********************************************* 上面是UI控件 下面是播放器的常用方法***************************************************
***************************************************************************************************************
***************************************************************************************************************/
    /**
     * 是否出错停止播放，默认是出错停止播放，true出错停止播放,false为用户点击停止播放
     */
    protected boolean isErrorStop = true;
    /**
     * 当前状态
     */
    protected int status = PlayStateParams.STATE_IDLE;
    /**
     * 当前播放位置
     */
    protected int currentPosition;
    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    protected long newPosition = -1;
    /**
     * 视频旋转的角度，默认只有0,90.270分别对应向上、向左、向右三个方向
     */
    protected int rotation = 0;
    /**
     * 视频显示比例,默认保持原视频的大小
     */
    protected int currentShowType = PlayStateParams.fitparent;
    /**
     * 播放总时长
     */
    protected long durationTotal;
    /**
     * 当前声音大小
     */
    protected int volume;
    /**
     * 设备最大音量
     */
    protected int mMaxVolume;
    /**
     * 获取当前设备的宽度
     */
    protected int screenWidthPixels;
    /**
     * 记录播放器竖屏时的高度
     */
    protected int initHeight;
    /**
     * 当前亮度大小
     */
    protected float brightness;
    /**
     * 当前播放地址
     */
    protected String currentUrl;
    /**
     * 当前选择的视频流索引
     */
    protected int currentSelect;
    /**
     * 记录进行后台时的播放状态0为播放，1为暂停
     */
    protected int bgState;
    /**
     * 自动重连的时间
     */
    protected int autoConnectTime = 5000;
    /**
     * 第三方so是否支持，默认不支持，true为支持
     */
    protected boolean playerSupport;
    /**
     * 是否是直播 默认为非直播，true为直播false为点播，根据isLive()方法前缀rtmp或者后缀.m3u8判断得出的为直播，比较片面，有好的建议欢迎交流
     */
    protected boolean isLive;
    /**
     * 当前是否切换视频流，默认为否，true是切换视频流，false没有切换
     */
    protected boolean isHasSwitchStream;
    /**
     * 码流列表
     */
    protected List<VideoijkBean> listVideos = new ArrayList<>();

    public boolean isCharge; // true为收费 false为免费即不做限制
    public int maxPlaytime;  // 最大能播放时长，单位秒

    /**
     * 设置最大观看时长
     *
     * @param isCharge    true为收费 false为免费即不做限制
     * @param maxPlaytime 最大能播放时长，单位秒
     */
    public void setChargeTie(boolean isCharge, int maxPlaytime) {
        this.isCharge = isCharge;
        this.maxPlaytime = maxPlaytime * 1000;
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        status = PlayStateParams.STATE_PAUSED;
        getCurrentPosition();
        mIjkVideoView.pause();
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        mIjkVideoView.stopPlayback();
        isErrorStop = true;
    }

    /**
     * 重新播放
     */
    public void replayPlay() {
        status = PlayStateParams.STATE_ERROR;
        startPlay();
        hideStatusUI();
        updatePausePlay();
    }

    /**
     * 更新播放、暂停和停止按钮
     */
    protected void updatePausePlay() {
        if (mIjkVideoView.isPlaying()) {
            if (isLive) {
                centerPlay.setImageResource(R.drawable.player_stop_white);
            } else {
                centerPlay.setImageResource(R.drawable.player_icon_media_pause);
                mPlayerBottomView.getImageView().setImageResource(R.drawable.player_center_pause);
            }
        } else {
            centerPlay.setImageResource(R.drawable.player_arrow_white);
            mPlayerBottomView.getImageView().setImageResource(R.drawable.player_center_play);
        }
    }

    /**
     * 设置播放地址
     * 包括视频清晰度列表
     * 对应地址列表
     */
    public void setPlaySource(List<VideoijkBean> list) {
        listVideos.clear();
        if (list != null && list.size() > 0) {
            listVideos.addAll(list);
            switchStream(0);
        }
    }

    /**
     * 设置播放地址
     * 单个视频VideoijkBean
     */
    public void setPlaySource(VideoijkBean videoijkBean) {
        listVideos.clear();
        if (videoijkBean != null) {
            listVideos.add(videoijkBean);
            switchStream(0);
        }
    }

    /**
     * 设置播放地址
     * 单个视频地址时
     * 带流名称
     */
    public void setPlaySource(String stream, String url) {
        VideoijkBean mVideoijkBean = new VideoijkBean();
        mVideoijkBean.setStream(stream);
        mVideoijkBean.setUrl(url);
        setPlaySource(mVideoijkBean);
    }

    /**
     * 选择要播放的流
     */
    public void switchStream(int index) {
        if (listVideos.size() > index) {
            mPlayerBottomView.setStream(listVideos.get(index).getStream());
            currentUrl = listVideos.get(index).getUrl();
            listVideos.get(index).setSelect(true);
            isLive();
            if (mIjkVideoView.isPlaying()) {
                getCurrentPosition();
                mIjkVideoView.release(false);
            }
            isHasSwitchStream = true;
        }
    }

    /**
     * 当前播放的是否是直播
     */
    public boolean isLive() {
        if (currentUrl != null
                && (currentUrl.startsWith("rtmp://")
                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".m3u8"))
                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".flv")))) {
            isLive = true;
        } else {
            isLive = false;
        }
        return isLive;
    }

    /**
     * 设置播放地址
     * 单个视频地址时
     */
    public void setPlaySource(String url) {
        setPlaySource("标清", url);
    }

    /**
     * 自动播放
     */
    public void autoPlay(String path) {
        setPlaySource(path);
        startPlay();
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        if (isLive) {
            mIjkVideoView.setVideoPath(currentUrl);
            mIjkVideoView.seekTo(0);
        } else {
            if (isHasSwitchStream || status == PlayStateParams.STATE_ERROR) {
                //换源之后声音可播，画面卡住，主要是渲染问题，目前只是提供了软解方式，后期提供设置方式
                mIjkVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                mIjkVideoView.setVideoPath(currentUrl);
                mIjkVideoView.seekTo(currentPosition);
                isHasSwitchStream = false;
            }else{
                mIjkVideoView.setVideoPath(currentUrl);
                mIjkVideoView.seekTo(0);
            }
        }
        hideStatusUI();
        if (isGNetWork && (NetworkUtils.getNetworkType(getContext()) == 4 || NetworkUtils.getNetworkType(getContext()) == 5 || NetworkUtils.getNetworkType(getContext()) == 6)) {
            netTieLayout.setVisibility(VISIBLE);
        } else {
            if (isCharge && maxPlaytime < getCurrentPosition()) {
                freeTieLayout.setVisibility(VISIBLE);
            } else {
                if (playerSupport) {
                    loadingLayout.setVisibility(VISIBLE);
                    mIjkVideoView.start();
                } else {
                    LogUtil.e("播放器不支持此设备");
                }
            }
        }
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
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = mIjkVideoView.getCurrentPosition();
        } else {
            /**直播*/
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        durationTotal = mIjkVideoView.getDuration();
        return durationTotal;
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

    /**
     * 设置播放区域拉伸类型
     */
    public void setScaleType(int showType) {
        currentShowType = showType;
        mIjkVideoView.setAspectRatio(currentShowType);
    }

    /**==========================================Activity生命周期方法回调=============================*/
    /**
     * @Override protected void onPause() {
     * super.onPause();
     * if (player != null) {
     * player.onPause();
     * }
     * }
     */
    public void onPause() {
        bgState = (mIjkVideoView.isPlaying() ? 0 : 1);
        getCurrentPosition();
        mIjkVideoView.pause();
    }

    /**
     * @Override protected void onResume() {
     * super.onResume();
     * if (player != null) {
     * player.onResume();
     * }
     * }
     */
    public void onResume() {
        mIjkVideoView.resume();
        if (isLive) {
            mIjkVideoView.seekTo(0);
        } else {
            mIjkVideoView.seekTo(currentPosition);
        }
        if (bgState == 0) {

        } else {
//            pausePlay();
        }
    }

    /**
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * if (player != null) {
     * player.onDestroy();
     * }
     * }
     */
    public void onDestroy() {
//        orientationEventListener.disable();
//        mHandler.removeMessages(MESSAGE_RESTART_PLAY);
//        mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
        mIjkVideoView.stopPlayback();
    }

    /**
     * @Override public void onConfigurationChanged(Configuration newConfig) {
     * super.onConfigurationChanged(newConfig);
     * if (player != null) {
     * player.onConfigurationChanged(newConfig);
     * }
     * }
     */
    public void onConfigurationChanged(final Configuration newConfig) {
//        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
//        doOnConfigurationChanged(isPortrait);
    }

    /**
     * @Override public void onBackPressed() {
     * if (player != null && player.onBackPressed()) {
     * return;
     * }
     * super.onBackPressed();
     * }
     */
    public boolean onBackPressed() {
//        if (!isOnlyFullScreen && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return true;
//        }
        return false;
    }

    /**
     * ==========================================Activity生命周期方法回调=============================
     */

    /**
     * 视频播放时信息回调
     */
    public IMediaPlayer.OnInfoListener onInfoListener;

    /**
     * 设置播放信息监听回调
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    @Override
    public void onClick(View view) {
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

    public interface PlayerListener {
        void onComplete();  // 播放完成

        void onError();     // 播放错误

        void onLoading();   // 加载中

        void start();     // 开始播放

        void onPlaying();   // 播放中

        void stop();      // 停止播放

        void pause();      // 暂停播放

        void resume();      // 恢复播放

        void goBack();      // 播放器左上角返回按钮
    }
}

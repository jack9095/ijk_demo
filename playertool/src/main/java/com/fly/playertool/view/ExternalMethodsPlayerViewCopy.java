//package com.fly.playertool.view;
//
//
//import android.content.pm.ActivityInfo;
//import android.media.AudioManager;
//import android.util.DisplayMetrics;
//import android.view.Surface;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.fly.playertool.R;
//import com.fly.playertool.widget.PlayStateParams;
//
//import java.util.List;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//
///**
// * Created by fei.wang on 2018/6/20.
// * 播放控件  对外方法层
// */
//public class ExternalMethodsPlayerViewCopy extends EventPlayerViewCopy{
//    /**
//     * 显示缩略图
//     */
//    public PlayerView showThumbnail(OnShowThumbnailListener onShowThumbnailListener) {
//        this.mOnShowThumbnailListener = onShowThumbnailListener;
//        if (mOnShowThumbnailListener != null && iv_trumb != null) {
//            mOnShowThumbnailListener.onShowThumbnail(iv_trumb);
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放信息监听回调
//     */
//    public PlayerView setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
//        this.onInfoListener = onInfoListener;
//        return this;
//    }
//
//    /**
//     * 设置播放器中的返回键监听
//     */
//    public PlayerView setPlayerBackListener(OnPlayerBackListener listener) {
//        this.mPlayerBack = listener;
//        return this;
//    }
//
//    /**
//     * 设置控制面板显示隐藏监听
//     */
//    public PlayerView setOnControlPanelVisibilityChangListenter(OnControlPanelVisibilityChangeListener listener) {
//        this.onControlPanelVisibilityChangeListener = listener;
//        return this;
//    }
//
//    /**
//     * 百分比显示切换
//     */
//    public PlayerView toggleAspectRatio() {
//        if (videoView != null) {
//            videoView.toggleAspectRatio();
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放区域拉伸类型
//     */
//    public PlayerView setScaleType(int showType) {
//        currentShowType = showType;
//        videoView.setAspectRatio(currentShowType);
//        return this;
//    }
//
//    /**
//     * 旋转角度
//     */
//    public PlayerView setPlayerRotation() {
//        if (rotation == 0) {
//            rotation = 90;
//        } else if (rotation == 90) {
//            rotation = 270;
//        } else if (rotation == 270) {
//            rotation = 0;
//        }
//        setPlayerRotation(rotation);
//        return this;
//    }
//
//    /**
//     * 旋转指定角度
//     */
//    public PlayerView setPlayerRotation(int rotation) {
//        if (videoView != null) {
//            videoView.setPlayerRotation(rotation);
//            videoView.setAspectRatio(currentShowType);
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放地址
//     * 包括视频清晰度列表
//     * 对应地址列表
//     */
//    public PlayerView setPlaySource(List<VideoijkBean> list) {
//        listVideos.clear();
//        if (list != null && list.size() > 0) {
//            listVideos.addAll(list);
//            switchStream(0);
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放地址
//     * 单个视频VideoijkBean
//     */
//    public PlayerView setPlaySource(VideoijkBean videoijkBean) {
//        listVideos.clear();
//        if (videoijkBean != null) {
//            listVideos.add(videoijkBean);
//            switchStream(0);
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放地址
//     * 单个视频地址时
//     * 带流名称
//     */
//    public PlayerView setPlaySource(String stream, String url) {
//        VideoijkBean mVideoijkBean = new VideoijkBean();
//        mVideoijkBean.setStream(stream);
//        mVideoijkBean.setUrl(url);
//        setPlaySource(mVideoijkBean);
//        return this;
//    }
//
//    /**
//     * 设置播放地址
//     * 单个视频地址时
//     */
//    public PlayerView setPlaySource(String url) {
//        setPlaySource("标清", url);
//        return this;
//    }
//
//    /**
//     * 自动播放
//     */
//    public PlayerView autoPlay(String path) {
//        setPlaySource(path);
//        startPlay();
//        return this;
//    }
//
//    /**
//     * 开始播放
//     */
//    public PlayerView startPlay() {
//        if (isLive) {
//            videoView.setVideoPath(currentUrl);
//            videoView.seekTo(0);
//        } else {
//            if (isHasSwitchStream || status == PlayStateParams.STATE_ERROR) {
//                //换源之后声音可播，画面卡住，主要是渲染问题，目前只是提供了软解方式，后期提供设置方式
//                videoView.setRender(videoView.RENDER_TEXTURE_VIEW);
//                videoView.setVideoPath(currentUrl);
//                videoView.seekTo(currentPosition);
//                isHasSwitchStream = false;
//            }
//        }
//        hideStatusUI();
//        if (isGNetWork && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6)) {
//            query.id(R.id.app_video_netTie).visible();
//        } else {
//            if (isCharge && maxPlaytime < getCurrentPosition()) {
//                query.id(R.id.app_video_freeTie).visible();
//
//            } else {
//                if (playerSupport) {
//                    query.id(R.id.app_video_loading).visible();
//                    videoView.start();
//                } else {
//                    showStatus(mActivity.getResources().getString(R.string.not_support));
//                }
//            }
//        }
//        return this;
//    }
//
//    /**
//     * 设置视频名称
//     */
//    public PlayerView setTitle(String title) {
//        query.id(R.id.app_video_title).text(title);
//        return this;
//    }
//
//    /**
//     * 选择要播放的流
//     */
//    public PlayerView switchStream(int index) {
//        if (listVideos.size() > index) {
//            tv_steam.setText(listVideos.get(index).getStream());
//            currentUrl = listVideos.get(index).getUrl();
//            listVideos.get(index).setSelect(true);
//            isLive();
//            if (videoView.isPlaying()) {
//                getCurrentPosition();
//                videoView.release(false);
//            }
//            isHasSwitchStream = true;
//        }
//        return this;
//    }
//
//    /**
//     * 暂停播放
//     */
//    public PlayerView pausePlay() {
//        status = PlayStateParams.STATE_PAUSED;
//        getCurrentPosition();
//        videoView.pause();
//        return this;
//    }
//
//    /**
//     * 停止播放
//     */
//    public PlayerView stopPlay() {
//        videoView.stopPlayback();
//        isErrorStop = true;
//        if (mHandler != null) {
//            mHandler.removeMessages(MESSAGE_RESTART_PLAY);
//        }
//        return this;
//    }
//
//    /**
//     * 设置播放位置
//     */
//    public PlayerView seekTo(int playtime) {
//        videoView.seekTo(playtime);
//        return this;
//    }
//
//    /**
//     * 获取当前播放位置
//     */
//    public int getCurrentPosition() {
//        if (!isLive) {
//            currentPosition = videoView.getCurrentPosition();
//        } else {
//            /**直播*/
//            currentPosition = -1;
//        }
//        return currentPosition;
//    }
//
//    /**
//     * 获取视频播放总时长
//     */
//    public long getDuration() {
//        duration = videoView.getDuration();
//        return duration;
//    }
//
//    /**
//     * 设置2/3/4/5G和WiFi网络类型提示，
//     *
//     * @param isGNetWork true为进行2/3/4/5G网络类型提示
//     *                   false 不进行网络类型提示
//     */
//    public PlayerView setNetWorkTypeTie(boolean isGNetWork) {
//        this.isGNetWork = isGNetWork;
//        return this;
//    }
//
//    /**
//     * 设置最大观看时长
//     *
//     * @param isCharge    true为收费 false为免费即不做限制
//     * @param maxPlaytime 最大能播放时长，单位秒
//     */
//    public PlayerView setChargeTie(boolean isCharge, int maxPlaytime) {
//        this.isCharge = isCharge;
//        this.maxPlaytime = maxPlaytime * 1000;
//        return this;
//    }
//
//
//    /**
//     * 是否仅仅为全屏
//     */
//    public PlayerView setOnlyFullScreen(boolean isFull) {
//        this.isOnlyFullScreen = isFull;
//        tryFullScreen(isOnlyFullScreen);
//        if (isOnlyFullScreen) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//        }
//        return this;
//    }
//
//    /**
//     * 设置是否禁止双击
//     */
//    public PlayerView setForbidDoulbeUp(boolean flag) {
//        this.isForbidDoulbeUp = flag;
//        return this;
//    }
//
//    /**
//     * 设置是否禁止隐藏bar
//     */
//    public PlayerView setForbidHideControlPanl(boolean flag) {
//        this.isForbidHideControlPanl = flag;
//        return this;
//    }
//
//    /**
//     * 当前播放的是否是直播
//     */
//    public boolean isLive() {
//        if (currentUrl != null
//                && (currentUrl.startsWith("rtmp://")
//                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".m3u8"))
//                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".flv")))) {
//            isLive = true;
//        } else {
//            isLive = false;
//        }
//        return isLive;
//    }
//
//    /**
//     * 是否禁止触摸
//     */
//    public PlayerView forbidTouch(boolean forbidTouch) {
//        this.isForbidTouch = forbidTouch;
//        return this;
//    }
//
//    /**
//     * 隐藏所有状态界面
//     */
//    public PlayerView hideAllUI() {
//        if (query != null) {
//            hideAll();
//        }
//        return this;
//    }
//
//    /**
//     * 获取顶部控制barview
//     */
//    public View getTopBarView() {
//        return ll_topbar;
//    }
//
//    /**
//     * 获取底部控制barview
//     */
//    public View getBottonBarView() {
//        return ll_bottombar;
//    }
//
//    /**
//     * 获取旋转view
//     */
//    public ImageView getRationView() {
//        return iv_rotation;
//    }
//
//    /**
//     * 获取返回view
//     */
//    public ImageView getBackView() {
//        return iv_back;
//    }
//
//    /**
//     * 获取菜单view
//     */
//    public ImageView getMenuView() {
//        return iv_menu;
//    }
//
//    /**
//     * 获取全屏按钮view
//     */
//    public ImageView getFullScreenView() {
//        return iv_fullscreen;
//    }
//
//    /**
//     * 获取底部bar的播放view
//     */
//    public ImageView getBarPlayerView() {
//        return iv_bar_player;
//    }
//
//    /**
//     * 获取中间的播放view
//     */
//    public ImageView getPlayerView() {
//        return iv_player;
//    }
//
//    /**
//     * 隐藏返回键，true隐藏，false为显示
//     */
//    public PlayerView hideBack(boolean isHide) {
//        iv_back.setVisibility(isHide ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 隐藏菜单键，true隐藏，false为显示
//     */
//    public PlayerView hideMenu(boolean isHide) {
//        iv_menu.setVisibility(isHide ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 隐藏分辨率按钮，true隐藏，false为显示
//     */
//    public PlayerView hideSteam(boolean isHide) {
//        tv_steam.setVisibility(isHide ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 隐藏旋转按钮，true隐藏，false为显示
//     */
//    public PlayerView hideRotation(boolean isHide) {
//        iv_rotation.setVisibility(isHide ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 隐藏全屏按钮，true隐藏，false为显示
//     */
//    public PlayerView hideFullscreen(boolean isHide) {
//        iv_fullscreen.setVisibility(isHide ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 隐藏中间播放按钮,ture为隐藏，false为不做隐藏处理，但不是显示
//     */
//    public PlayerView hideCenterPlayer(boolean isHide) {
//        isHideCenterPlayer = isHide;
//        iv_player.setVisibility(isHideCenterPlayer ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 是否隐藏topbar，true为隐藏，false为不隐藏，但不一定是显示
//     */
//    public PlayerView hideHideTopBar(boolean isHide) {
//        isHideTopBar = isHide;
//        ll_topbar.setVisibility(isHideTopBar ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 是否隐藏bottonbar，true为隐藏，false为不隐藏，但不一定是显示
//     */
//    public PlayerView hideBottonBar(boolean isHide) {
//        isHideBottonBar = isHide;
//        ll_bottombar.setVisibility(isHideBottonBar ? View.GONE : View.VISIBLE);
//        return this;
//    }
//
//    /**
//     * 是否隐藏上下bar，true为隐藏，false为不隐藏，但不一定是显示
//     */
//    public PlayerView hideControlPanl(boolean isHide) {
//        hideBottonBar(isHide);
//        hideHideTopBar(isHide);
//        return this;
//    }
//
//    /**
//     * 设置自动重连的模式或者重连时间，isAuto true 出错重连，false出错不重连，connectTime重连的时间
//     */
//    public PlayerView setAutoReConnect(boolean isAuto, int connectTime) {
//        this.isAutoReConnect = isAuto;
//        this.autoConnectTime = connectTime;
//        return this;
//    }
//
//    /**
//     * 显示或隐藏操作面板
//     */
//    public PlayerView operatorPanl() {
//        isShowControlPanl = !isShowControlPanl;
//        query.id(R.id.simple_player_settings_container).gone();
//        query.id(R.id.simple_player_select_stream_container).gone();
//        if (isShowControlPanl) {
//            ll_topbar.setVisibility(isHideTopBar ? View.GONE : View.VISIBLE);
//            ll_bottombar.setVisibility(isHideBottonBar ? View.GONE : View.VISIBLE);
//            if (isLive) {
//                query.id(R.id.app_video_process_panl).invisible();
//            } else {
//                query.id(R.id.app_video_process_panl).visible();
//            }
//            if (isOnlyFullScreen || isForbidDoulbeUp) {
//                iv_fullscreen.setVisibility(View.GONE);
//            } else {
//                iv_fullscreen.setVisibility(View.VISIBLE);
//            }
//            if (onControlPanelVisibilityChangeListener != null) {
//                onControlPanelVisibilityChangeListener.change(true);
//            }
//            /**显示面板的时候再根据状态显示播放按钮*/
//            if (status == PlayStateParams.STATE_PLAYING
//                    || status == PlayStateParams.STATE_PREPARED
//                    || status == PlayStateParams.STATE_PREPARING
//                    || status == PlayStateParams.STATE_PAUSED) {
//                if (isHideCenterPlayer) {
//                    iv_player.setVisibility(View.GONE);
//                } else {
//                    iv_player.setVisibility(isLive ? View.GONE : View.VISIBLE);
//                }
//            } else {
//                iv_player.setVisibility(View.GONE);
//            }
//            updatePausePlay();
//            mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
//            mAutoPlayRunnable.start();
//        } else {
//            if (isHideTopBar) {
//                ll_topbar.setVisibility(View.GONE);
//            } else {
//                ll_topbar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);
//
//            }
//            if (isHideBottonBar) {
//                ll_bottombar.setVisibility(View.GONE);
//            } else {
//                ll_bottombar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);
//
//            }
//            if (!isLive && status == PlayStateParams.STATE_PAUSED && !videoView.isPlaying()) {
//                if (isHideCenterPlayer) {
//                    iv_player.setVisibility(View.GONE);
//                } else {
//                    /**暂停时一直显示按钮*/
//                    iv_player.setVisibility(View.VISIBLE);
//                }
//            } else {
//                iv_player.setVisibility(View.GONE);
//            }
//            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
//            if (onControlPanelVisibilityChangeListener != null) {
//                onControlPanelVisibilityChangeListener.change(false);
//            }
//            mAutoPlayRunnable.stop();
//        }
//        return this;
//    }
//
//    /**
//     * 全屏切换
//     */
//    public PlayerView toggleFullScreen() {
//        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        updateFullScreenButton();
//        return this;
//    }
//
//    /**
//     * 显示菜单设置
//     */
//    public PlayerView showMenu() {
//        volumeController.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mMaxVolume);
//        brightnessController.setProgress((int) (mActivity.getWindow().getAttributes().screenBrightness * 100));
//        settingsContainer.setVisibility(View.VISIBLE);
//        if (!isForbidHideControlPanl) {
//            ll_topbar.setVisibility(View.GONE);
//            ll_bottombar.setVisibility(View.GONE);
//        }
//        return this;
//    }
//
//    /**
//     * 进度条和时长显示的方向切换
//     */
//    public PlayerView toggleProcessDurationOrientation() {
//        setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT);
//        return this;
//    }
//
//    /**
//     * 设置显示加载网速或者隐藏
//     */
//    public PlayerView setShowSpeed(boolean isShow) {
//        tv_speed.setVisibility(isShow ? View.VISIBLE : View.GONE);
//        return this;
//    }
//
//    /**
//     * 设置进度条和时长显示的方向，默认为上下显示，true为上下显示false为左右显示
//     */
////    public PlayerView setProcessDurationOrientation(int portrait) {
////        if (portrait == PlayStateParams.PROCESS_CENTER) {
////            query.id(R.id.app_video_currentTime_full).gone();
////            query.id(R.id.app_video_endTime_full).gone();
////            query.id(R.id.app_video_center).gone();
////            query.id(R.id.app_video_lift).visible();
////        } else if (portrait == PlayStateParams.PROCESS_LANDSCAPE) {  // 左右显示
////            query.id(R.id.app_video_currentTime_full).visible();
////            query.id(R.id.app_video_endTime_full).visible();
////            query.id(R.id.app_video_center).gone();
////            query.id(R.id.app_video_lift).gone();
////        } else {
////            query.id(R.id.app_video_currentTime_full).gone();
////            query.id(R.id.app_video_endTime_full).gone();
////            query.id(R.id.app_video_center).visible();
////            query.id(R.id.app_video_lift).gone();
////        }
////        return this;
////    }
//
//    /**
//     * 设置进度条和时长显示的方向，默认为上下显示，true为上下显示false为左右显示
//     */
//    public PlayerView setProcessDurationOrientation(int portrait) {
//        // 左右显示
//        query.id(R.id.app_video_currentTime_full).visible();
//        query.id(R.id.app_video_endTime_full).visible();
//        query.id(R.id.app_video_center).gone();
//        query.id(R.id.app_video_lift).gone();
//
//        return this;
//    }
//
//
//    /**
//     * 获取界面方向
//     */
//    public int getScreenOrientation() {
//        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
//        DisplayMetrics dm = new DisplayMetrics();
//        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        int orientation;
//        // if the device's natural orientation is portrait:
//        if ((rotation == Surface.ROTATION_0
//                || rotation == Surface.ROTATION_180) && height > width ||
//                (rotation == Surface.ROTATION_90
//                        || rotation == Surface.ROTATION_270) && width > height) {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//                case Surface.ROTATION_90:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_180:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                    break;
//                default:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//            }
//        }
//        // if the device's natural orientation is landscape or if the device
//        // is square:
//        else {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_90:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//                case Surface.ROTATION_180:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                default:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//            }
//        }
//
//        return orientation;
//    }
//}

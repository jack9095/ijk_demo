//package com.fly.playertool.view;
//
//
//import android.content.pm.ActivityInfo;
//import android.media.AudioManager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.fly.playertool.R;
//import com.fly.playertool.widget.PlayStateParams;
//
///**
// * Created by fei.wang on 2018/6/20.
// * 播放控件  各种事件层
// */
//public class InternalMethodPlayerViewCopy extends EventPlayerViewCopy{
//    /**
//     * 状态改变同步UI
//     */
//    private void statusChange(int newStatus) {
//        if (newStatus == PlayStateParams.STATE_COMPLETED) {
//            status = PlayStateParams.STATE_COMPLETED;
//            currentPosition = 0;
//            hideAll();
//            showStatus("播放结束");
//        } else if (newStatus == PlayStateParams.STATE_PREPARING
//                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
//            status = PlayStateParams.STATE_PREPARING;
//            /**视频缓冲*/
//            hideStatusUI();
//            query.id(R.id.app_video_loading).visible();
//        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
//                || newStatus == PlayStateParams.STATE_PLAYING
//                || newStatus == PlayStateParams.STATE_PREPARED
//                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
//                || newStatus == PlayStateParams.STATE_PAUSED) {
//            if (status == PlayStateParams.STATE_PAUSED) {
//                status = PlayStateParams.STATE_PAUSED;
//            } else {
//                status = PlayStateParams.STATE_PLAYING;
//            }
//            /**视频缓冲结束后隐藏缩列图*/
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    hideStatusUI();
//                    /**显示控制bar*/
//                    isShowControlPanl = false;
//                    if (!isForbidTouch) {
//                        operatorPanl();
//                    }
//                    /**延迟0.5秒隐藏视频封面隐藏*/
//                    query.id(R.id.ll_bg).gone();
//                }
//            }, 500);
//        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_INTERRUPT) {
//            /**直播停止推流*/
//            status = PlayStateParams.STATE_ERROR;
//            if (!(isGNetWork &&
//                    (NetworkUtils.getNetworkType(mContext) == 4
//                            || NetworkUtils.getNetworkType(mContext) == 5
//                            || NetworkUtils.getNetworkType(mContext) == 6))) {
//                if (isCharge && maxPlaytime < getCurrentPosition()) {
//                    query.id(R.id.app_video_freeTie).visible();
//                } else {
//                    hideAll();
//                    if (isLive) {
//                        showStatus("获取不到直播源");
//                    } else {
//                        showStatus(mActivity.getResources().getString(R.string.small_problem));
//                    }
//                    /**5秒尝试重连*/
//                    if (!isErrorStop && isAutoReConnect) {
//                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, autoConnectTime);
//                    }
//
//                }
//            } else {
//                query.id(R.id.app_video_netTie).visible();
//            }
//
//        } else if (newStatus == PlayStateParams.STATE_ERROR
//                || newStatus == PlayStateParams.MEDIA_INFO_UNKNOWN
//                || newStatus == PlayStateParams.MEDIA_ERROR_IO
//                || newStatus == PlayStateParams.MEDIA_ERROR_MALFORMED
//                || newStatus == PlayStateParams.MEDIA_ERROR_UNSUPPORTED
//                || newStatus == PlayStateParams.MEDIA_ERROR_TIMED_OUT
//                || newStatus == PlayStateParams.MEDIA_ERROR_SERVER_DIED) {
//            status = PlayStateParams.STATE_ERROR;
//            if (!(isGNetWork && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6))) {
//                if (isCharge && maxPlaytime < getCurrentPosition()) {
//                    query.id(R.id.app_video_freeTie).visible();
//                } else {
//                    hideStatusUI();
//                    if (isLive) {
//                        showStatus(mActivity.getResources().getString(R.string.small_problem));
//                    } else {
//                        showStatus(mActivity.getResources().getString(R.string.small_problem));
//                    }
//                    /**5秒尝试重连*/
//                    if (!isErrorStop && isAutoReConnect) {
//                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, autoConnectTime);
//                    }
//                }
//            } else {
//                query.id(R.id.app_video_netTie).visible();
//            }
//        }
//    }
//
//    /**
//     * 显示视频播放状态提示
//     */
//    private void showStatus(String statusText) {
//        query.id(R.id.app_video_replay).visible();
//        query.id(R.id.app_video_status_text).text(statusText);
//    }
//
//    /**
//     * 界面方向改变是刷新界面
//     */
//    private void doOnConfigurationChanged(final boolean portrait) {
//        if (videoView != null && !isOnlyFullScreen) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tryFullScreen(!portrait);
//                    if (portrait) {
//                        query.id(R.id.app_video_box).height(initHeight, false);
//                    } else {
//                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
//                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
//                        query.id(R.id.app_video_box).height(Math.min(heightPixels, widthPixels), false);
//                    }
//                    updateFullScreenButton();
//                }
//            });
//            orientationEventListener.enable();
//        }
//    }
//
//
//    /**
//     * 设置界面方向
//     */
//    private void setFullScreen(boolean fullScreen) {
//        if (mActivity != null) {
//
//            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
//            if (fullScreen) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                mActivity.getWindow().setAttributes(attrs);
//                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            } else {
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                mActivity.getWindow().setAttributes(attrs);
//                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            }
//            toggleProcessDurationOrientation();
//        }
//
//    }
//
//    /**
//     * 设置界面方向带隐藏actionbar
//     */
//    private void tryFullScreen(boolean fullScreen) {
//        if (mActivity instanceof AppCompatActivity) {
//            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
//            if (supportActionBar != null) {
//                if (fullScreen) {
//                    supportActionBar.hide();
//                } else {
//                    supportActionBar.show();
//                }
//            }
//        }
//        setFullScreen(fullScreen);
//    }
//
//
//    /**
//     * 隐藏状态界面
//     */
//    private void hideStatusUI() {
//        iv_player.setVisibility(View.GONE);
//        query.id(R.id.simple_player_settings_container).gone();  // 声音亮度布局的控制
//        query.id(R.id.simple_player_select_stream_container).gone(); // 分辨率布局的控制
//        query.id(R.id.app_video_replay).gone();   // 重新播放布局的控制
//        query.id(R.id.app_video_netTie).gone();   // 网络提示布局的控制
//        query.id(R.id.app_video_freeTie).gone();  // 最大试看时长提示布局的控制
//        query.id(R.id.app_video_loading).gone();  // 加载中布局的控制
//        if (onControlPanelVisibilityChangeListener != null) {
//            onControlPanelVisibilityChangeListener.change(false);
//        }
//    }
//
//    /**
//     * 隐藏所有界面
//     */
//    private void hideAll() {
//        if (!isForbidHideControlPanl) {
//            ll_topbar.setVisibility(View.GONE);    // 顶部标题栏布局的控制
//            ll_bottombar.setVisibility(View.GONE); // 底部导航栏布局的控制
//        }
//        hideStatusUI();
//    }
//
//    /**
//     * 显示分辨率列表
//     */
//    private void showStreamSelectView() {
//        this.streamSelectView.setVisibility(View.VISIBLE);
//        if (!isForbidHideControlPanl) {
//            ll_topbar.setVisibility(View.GONE);
//            ll_bottombar.setVisibility(View.GONE);
//        }
//        this.streamSelectListView.setItemsCanFocus(true);
//    }
//
//    /**
//     * 隐藏分辨率列表
//     */
//    private void hideStreamSelectView() {
//        this.streamSelectView.setVisibility(View.GONE);
//    }
//
//
//    /**
//     * 手势结束
//     */
//    private void endGesture() {
//        volume = -1;
//        brightness = -1f;
//        if (newPosition >= 0) {
//            mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
//            mHandler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
//        } else {
//            /**什么都不做(do nothing)*/
//        }
//        mHandler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
//        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
//        if (mAutoPlayRunnable != null) {
//            mAutoPlayRunnable.start();
//        }
//
//    }
//
//    /**
//     * 同步进度
//     */
//    private long syncProgress() {
//        if (isDragging) {
//            return 0;
//        }
//        long position = videoView.getCurrentPosition();
//        long duration = videoView.getDuration();
//        if (seekBar != null) {
//            if (duration > 0) {
//                long pos = 1000L * position / duration;
//                seekBar.setProgress((int) pos);
//            }
//            int percent = videoView.getBufferPercentage();
//            seekBar.setSecondaryProgress(percent * 10);
//        }
//
//        if (isCharge && maxPlaytime + 1000 < getCurrentPosition()) {
//            query.id(R.id.app_video_freeTie).visible();
//            pausePlay();
//        } else {
//            query.id(R.id.app_video_currentTime).text(generateTime(position));
//            query.id(R.id.app_video_currentTime_full).text(generateTime(position));
//            query.id(R.id.app_video_currentTime_left).text(generateTime(position));
//            query.id(R.id.app_video_endTime).text(generateTime(duration));
//            query.id(R.id.app_video_endTime_full).text(generateTime(duration));
//            query.id(R.id.app_video_endTime_left).text(generateTime(duration));
//        }
//        return position;
//    }
//
//    /**
//     * 时长格式化显示
//     */
//    private String generateTime(long time) {
//        int totalSeconds = (int) (time / 1000);
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours = totalSeconds / 3600;
//        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
//    }
//
//    /**
//     * 下载速度格式化显示
//     */
//    private String getFormatSize(int size) {
//        long fileSize = (long) size;
//        String showSize = "";
//        if (fileSize >= 0 && fileSize < 1024) {
//            showSize = fileSize + "Kb/s";
//        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
//            showSize = Long.toString(fileSize / 1024) + "KB/s";
//        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
//            showSize = Long.toString(fileSize / (1024 * 1024)) + "MB/s";
//        }
//        return showSize;
//    }
//
//
//    /**
//     * 更新播放、暂停和停止按钮
//     */
//    private void updatePausePlay() {
//        if (videoView.isPlaying()) {
//            if (isLive) {
//                iv_bar_player.setImageResource(R.drawable.simple_player_stop_white_24dp);
//            } else {
//                iv_bar_player.setImageResource(R.drawable.simple_player_icon_media_pause);
//                iv_player.setImageResource(R.drawable.simple_player_center_pause);
//            }
//        } else {
//            iv_bar_player.setImageResource(R.drawable.simple_player_arrow_white_24dp);
//            iv_player.setImageResource(R.drawable.simple_player_center_play);
//        }
//    }
//
//    /**
//     * 更新全屏和半屏按钮
//     */
//    private void updateFullScreenButton() {
//        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink);
//        } else {
//            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch);
//        }
//    }
//
//    /**
//     * 滑动改变声音大小
//     *
//     * @param percent
//     */
//    private void onVolumeSlide(float percent) {
//        if (volume == -1) {
//            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            if (volume < 0)
//                volume = 0;
//        }
//        int index = (int) (percent * mMaxVolume) + volume;
//        if (index > mMaxVolume)
//            index = mMaxVolume;
//        else if (index < 0)
//            index = 0;
//
//        // 变更声音
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
//
//        // 变更进度条
//        int i = (int) (index * 1.0 / mMaxVolume * 100);
//        String s = i + "%";
//        if (i == 0) {
//            s = "off";
//        }
//        // 显示
//        query.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.simple_player_volume_off_white_36dp : R.drawable.simple_player_volume_up_white_36dp);
//        query.id(R.id.app_video_brightness_box).gone();
//        query.id(R.id.app_video_volume_box).visible();
//        query.id(R.id.app_video_volume_box).visible();
//        query.id(R.id.app_video_volume).text(s).visible();
//    }
//
//    /**
//     * 快进或者快退滑动改变进度
//     *
//     * @param percent
//     */
//    private void onProgressSlide(float percent) {
//        int position = videoView.getCurrentPosition();
//        long duration = videoView.getDuration();
//        long deltaMax = Math.min(100 * 1000, duration - position);
//        long delta = (long) (deltaMax * percent);
//        newPosition = delta + position;
//        if (newPosition > duration) {
//            newPosition = duration;
//        } else if (newPosition <= 0) {
//            newPosition = 0;
//            delta = -position;
//        }
//        int showDelta = (int) delta / 1000;
//        if (showDelta != 0) {
//            query.id(R.id.app_video_fastForward_box).visible();
//            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            query.id(R.id.app_video_fastForward).text(text + "s");
//            query.id(R.id.app_video_fastForward_target).text(generateTime(newPosition) + "/");
//            query.id(R.id.app_video_fastForward_all).text(generateTime(duration));
//        }
//    }
//
//    /**
//     * 亮度滑动改变亮度
//     *
//     * @param percent
//     */
//    private void onBrightnessSlide(float percent) {
//        if (brightness < 0) {
//            brightness = mActivity.getWindow().getAttributes().screenBrightness;
//            if (brightness <= 0.00f) {
//                brightness = 0.50f;
//            } else if (brightness < 0.01f) {
//                brightness = 0.01f;
//            }
//        }
//        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
//        query.id(R.id.app_video_brightness_box).visible();
//        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
//        lpa.screenBrightness = brightness + percent;
//        if (lpa.screenBrightness > 1.0f) {
//            lpa.screenBrightness = 1.0f;
//        } else if (lpa.screenBrightness < 0.01f) {
//            lpa.screenBrightness = 0.01f;
//        }
//        query.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
//        mActivity.getWindow().setAttributes(lpa);
//    }
//}

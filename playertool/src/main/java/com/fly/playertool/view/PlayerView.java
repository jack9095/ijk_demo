package com.fly.playertool.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import com.fly.playertool.R;
import com.fly.playertool.utils.CommonUtil;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.NetworkUtils;
import com.fly.playertool.widget.PlayStateParams;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class PlayerView extends BasePlayerView {

    public PlayerView(@NonNull Context context) {
        super(context);
        onEventListener();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onEventListener();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onEventListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onEventListener();
    }

    public void setStartIjkMediaPlayer(){
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            LogUtil.e("GiraffePlayer" + e);
        }
    }

    /**
     * 各种事件汇总
     */
    public void onEventListener(){
        mIjkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
                    LogUtil.e("extra = " + extra);
                    if (loadingSpeedText != null) {
                        loadingSpeedText.setText(CommonUtil.getFormatSize(extra)); // 显示加载速度
                    }
                }
                statusChange(what);
                if (onInfoListener != null) {
                    onInfoListener.onInfo(mp, what, extra);
                }
                if (isCharge && maxPlaytime < getCurrentPosition()) { // 观看到了最大试看时长
                    freeTieLayout.setVisibility(VISIBLE); // 显示最大试看时长
                    pausePlay(); // 暂停
                }
                return true;
            }
        });
    }

    /**
     * 状态改变同步UI
     */
    private void statusChange(int newStatus) {
        if (newStatus == PlayStateParams.STATE_COMPLETED) {
            status = PlayStateParams.STATE_COMPLETED;
            currentPosition = 0;
            LogUtil.e("播放结束");
        } else if (newStatus == PlayStateParams.STATE_PREPARING
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
            status = PlayStateParams.STATE_PREPARING;
            // 视频缓冲
            hideStatusUI();
            loadingLayout.setVisibility(View.VISIBLE); // 显示加载布局
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
                || newStatus == PlayStateParams.STATE_PLAYING
                || newStatus == PlayStateParams.STATE_PREPARED
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
                || newStatus == PlayStateParams.STATE_PAUSED) {
            if (status == PlayStateParams.STATE_PAUSED) {
                status = PlayStateParams.STATE_PAUSED;
            } else {
                status = PlayStateParams.STATE_PLAYING;
            }
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
            status = PlayStateParams.STATE_ERROR;
            if (!(isGNetWork &&
                    (NetworkUtils.getNetworkType(getContext()) == 4
                            || NetworkUtils.getNetworkType(getContext()) == 5
                            || NetworkUtils.getNetworkType(getContext()) == 6))) {
                if (isCharge && maxPlaytime < getCurrentPosition()) {  // 观看到了最大试看时长
                    freeTieLayout.setVisibility(VISIBLE); // 显示最大试看时长
                    pausePlay(); // 暂停
                } else {
                    hideAll();
                    if (isLive) {
                        LogUtil.e("获取不到直播源");
                    } else {
                        LogUtil.e("出现了点小问题,稍后重试");
                    }
                    // 5秒尝试重连
                    if (!isErrorStop) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                replayPlay();
                            }
                        }, autoConnectTime);
                    }
                }
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
            status = PlayStateParams.STATE_ERROR;
            if (!(isGNetWork && (NetworkUtils.getNetworkType(getContext()) == 4 || NetworkUtils.getNetworkType(getContext()) == 5 || NetworkUtils.getNetworkType(getContext()) == 6))) {
                if (isCharge && maxPlaytime < getCurrentPosition()) {
                    freeTieLayout.setVisibility(VISIBLE); // 显示最大试看时长
                    pausePlay(); // 暂停
                } else {
                    hideStatusUI();
                    if (isLive) {
                        LogUtil.e("获取不到直播源");
                    } else {
                        LogUtil.e("出现了点小问题,稍后重试");
                    }
                    // 5秒尝试重连
                    if (!isErrorStop) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                replayPlay();
                            }
                        }, autoConnectTime);
                    }
                }
            } else {
                netTieLayout.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.play_top_bar_back) {  // 返回
            mPlayerListener.goBack();
        } else if (v.getId() == R.id.player_bottom_bar_stream) {  // 选择分辨率

        } else if (v.getId() == R.id.player_bottom_bar_zoom) {   // 点击横竖屏

        } else if (v.getId() == R.id.player_bottom_bar_video_play || v.getId() == R.id.player_view_player_center_icon) {
            // 视频播放和暂停
            if (mIjkVideoView.isPlaying()) {
                if (isLive) {
                    mIjkVideoView.stopPlayback();
                } else {
                    pausePlay();
                }
            } else {
                startPlay();
                if (mIjkVideoView.isPlaying()) {
                    // ijkplayer内部的监听没有回调，只能手动修改状态
                    status = PlayStateParams.STATE_PREPARING;
                    hideStatusUI();
                }
            }
            updatePausePlay();
        } else if (v.getId() == R.id.player_view_player_netTie_icon) {
            // 使用移动网络提示继续播放
            isGNetWork = false;
            hideStatusUI();
            startPlay();
            updatePausePlay();
        } else if (v.getId() == R.id.player_view_player_replay_icon) {
            replayPlay(); // 重新播放
        } else if (v.getId() == R.id.player_view_player_video_freeTie_icon) {
            // 购买会员
        }
    }
}

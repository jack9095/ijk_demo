package com.fly.playertool.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import com.fly.playertool.R;
import com.fly.playertool.utils.CommonUtil;
import com.fly.playertool.utils.HandlerWhat;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.NetworkUtils;
import com.fly.playertool.utils.ScreenRotateUtil;
import com.fly.playertool.widget.PlayStateParams;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class PlayerView extends BasePlayerView implements View.OnClickListener{

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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                /**滑动完成，设置播放进度*/
                case HandlerWhat.MESSAGE_SEEK_NEW_POSITION:
//                    if (!isLive && newPosition >= 0) {
//                        videoView.seekTo((int) newPosition);
//                        newPosition = -1;
//                    }
                    break;
                /**滑动中，同步播放进度*/
                case HandlerWhat.MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (mIjkVideoView.isPlaying()) {
                        msg = obtainMessage(HandlerWhat.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                /**重新去播放*/
                case HandlerWhat.MESSAGE_RESTART_PLAY:
//                    status = PlayStateParams.STATE_ERROR;
//                    startPlay();
//                    updatePausePlay();
                    break;
            }
        }
    };

    /**
     * 同步进度
     */
    private long syncProgress() {
        long position = mIjkVideoView.getCurrentPosition(); // 当前时长
        long duration = mIjkVideoView.getDuration();  // 视频总时长
        LogUtil.e(" ***总时长 = *** " + duration);
        LogUtil.e(" ***开始播放 = *** " + position);
        mPlayerBottomView.setTotalTime(duration);
        mPlayerBottomView.setCurrentTime(position);
        mPlayerBottomView.setSeekBarTo(position);
        return position;
    }

    // 开始播放
    public void playing(){
        mHandler.sendEmptyMessage(HandlerWhat.MESSAGE_SHOW_PROGRESS);
    }

    /**
     * 各种事件汇总
     */
    public void onEventListener(){
        replayImage.setOnClickListener(this);
        netTieText.setOnClickListener(this);
        freeTieText.setOnClickListener(this);
        mPlayerTopView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getImageView().setOnClickListener(this);
        mPlayerBottomView.getZoomView().setOnClickListener(this);
        mPlayerBottomView.getLineView().setOnClickListener(this);
        mPlayerBottomView.setHandler(mHandler);
//        mIjkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
//                LogUtil.e("PlayView =  what = " + what + "   extra = " + extra);
//                if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {  // 播放
//                    LogUtil.e(" ***开始播放*** ");
//                    playing();
//                }else if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
//                    LogUtil.e("extra = " + extra);
//                    if (loadingSpeedText != null) {
//                        loadingSpeedText.setText(CommonUtil.getFormatSize(extra)); // 显示加载速度
//                    }
//                }
////                statusChange(what);
//                return true;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_top_bar_back) {  // 返回
            mPlayerListener.goBack();
        } else if (v.getId() == R.id.player_bottom_bar_stream) {  // 选择分辨率

        } else if (v.getId() == R.id.player_bottom_bar_zoom) {   // 点击横竖屏
            LogUtil.e("点击切换横竖屏");
            if (ScreenRotateUtil.isLandscape(getContext())) {
                mPlayerListener.screen(2);
                mPlayerBottomView.getZoomView().setImageResource(R.drawable.player_fullscreen_zoom);
            }else {
                mPlayerBottomView.getZoomView().setImageResource(R.drawable.player_zoom);
                mPlayerListener.screen(1);
            }
        } else if (v.getId() == R.id.player_bottom_bar_video_play || v.getId() == R.id.player_view_player_center_icon) {

            if (mIjkVideoView.isPlaying()) {
                pausePlayerUI();
                mIjkVideoView.pause();
            }else{
                startPlayerUI();
                mIjkVideoView.start();
            }

        } else if (v.getId() == R.id.player_view_player_netTie_icon) {
            // 使用移动网络提示继续播放
            isGNetWork = false;
            hideStatusUI();

        } else if (v.getId() == R.id.player_view_player_replay_icon) {
           // 重新播放

        } else if (v.getId() == R.id.player_view_player_video_freeTie_icon) {
            // 购买会员
        }
    }
}

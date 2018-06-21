package com.fly.playertool.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.fly.playertool.R;
import com.fly.playertool.utils.HandlerWhat;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.ScreenRotateUtil;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class PlayerView extends BasePlayerView{

    /**
     * 禁止触摸，默认可以触摸，true为禁止false为可触摸
     */
    private boolean isForbidTouch;
    /**
     * 是否禁止双击，默认不禁止，true为禁止，false为不禁止
     */
    private boolean isForbidDoulbeUp;
    /**
     * 当前声音大小
     */
    private int volume;
    /**
     * 设备最大音量
     */
    private int mMaxVolume;
    /**
     * 当前亮度大小
     */
    private float brightness;
    /**
     * 音频管理器
     */
    private AudioManager audioManager;

    private Activity mActivity;
    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    private long newPosition = -1;

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

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    // 开始播放
    public void playing(){
        mHandler.sendEmptyMessage(HandlerWhat.MESSAGE_SHOW_PROGRESS);
    }

    /**
     * 各种事件汇总
     */
    @SuppressLint("ClickableViewAccessibility")
    public void onEventListener(){
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mPlayerBottomView.setHandler(mHandler);

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new PlayerGestureListener());
        mRelativeLayout.setClickable(true);
        mRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
//                        if (mAutoPlayRunnable != null) {
//                            mAutoPlayRunnable.stop();
//                        }
                        break;
                }
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });
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

/********************************************************************************************************
******************************************** 下面是手势 *************************************************************
/********************************************************************************************************/

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
         */
        private boolean isVolume;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 视频视窗双击事件
            if (!isForbidTouch && !isForbidDoulbeUp) {

            }
            return true;
        }

        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isVolume = mOldX > screenWidthPixels * 0.5f;
                    isDownTouch = false;
                }

                if (isLandscape) {
                    // 进度设置
                    onProgressSlide(-deltaX / mIjkVideoView.getWidth());
                } else {
                    float percent = deltaY / mIjkVideoView.getHeight();
                    if (isVolume) {
                        // 声音设置
                        onVolumeSlide(percent);
                    } else {
                        // 亮度设置
                        onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 视频视窗单击事件
            if (!isForbidTouch) {
                // 显示和隐藏操作面板
            }
            return true;
        }
    }

    /**
     * 手势滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
//        query.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.simple_player_volume_off_white_36dp : R.drawable.simple_player_volume_up_white_36dp);
//        query.id(R.id.app_video_brightness_box).gone();
//        query.id(R.id.app_video_volume_box).visible();
//        query.id(R.id.app_video_volume_box).visible();
//        query.id(R.id.app_video_volume).text(s).visible();
    }

    /**
     * 手势 快进或者快退滑动改变进度
     *
     * @param percent
     */
    private void onProgressSlide(float percent) {
        int position = mIjkVideoView.getCurrentPosition();
        long duration = mIjkVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);
        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
//            query.id(R.id.app_video_fastForward_box).visible();
//            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            query.id(R.id.app_video_fastForward).text(text + "s");
//            query.id(R.id.app_video_fastForward_target).text(generateTime(newPosition) + "/");
//            query.id(R.id.app_video_fastForward_all).text(generateTime(duration));
        }
    }

    /**
     * 手势 亮度滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
//        query.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
//        query.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
        mActivity.getWindow().setAttributes(lpa);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            mHandler.removeMessages(HandlerWhat.MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(HandlerWhat.MESSAGE_SEEK_NEW_POSITION);
        } else {
            // 什么都不做(do nothing)
        }
//        mHandler.removeMessages(HandlerWhat.MESSAGE_HIDE_CENTER_BOX);
//        mHandler.sendEmptyMessageDelayed(HandlerWhat.MESSAGE_HIDE_CENTER_BOX, 500);
//        if (mAutoPlayRunnable != null) {
//            mAutoPlayRunnable.start();
//        }

    }
}

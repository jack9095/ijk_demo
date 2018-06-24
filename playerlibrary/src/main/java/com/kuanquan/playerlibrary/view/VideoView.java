package com.kuanquan.playerlibrary.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import com.kuanquan.playerlibrary.R;
import com.kuanquan.playerlibrary.utils.HandlerWhat;
import com.kuanquan.playerlibrary.utils.LogUtil;
import com.kuanquan.playerlibrary.utils.ScreenRotateUtil;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件
 */
public class VideoView extends CommonPlayer{

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

    public VideoView(@NonNull Context context) {
        super(context);
        onEventListener();
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onEventListener();
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onEventListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onEventListener();
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    /**
     * 各种事件汇总
     */
    @SuppressLint("ClickableViewAccessibility")
    public void onEventListener(){
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // 开始手势
//        final GestureDetector gestureDetector = new GestureDetector(getContext(), new PlayerGestureListener());
//        mRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
////                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
////                    case MotionEvent.ACTION_DOWN:
//////                        if (mAutoPlayRunnable != null) {
//////                            mAutoPlayRunnable.stop();
//////                        }
////                        break;
////                }
//                if (gestureDetector.onTouchEvent(motionEvent))
//                    return true;
//                // 处理手势结束
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_UP:
//                        endGesture();
//                        break;
//                }
//                return false;
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

//        } else if (v.getId() == R.id.player_view_player_netTie_icon) {
//            // 使用移动网络提示继续播放
//            isGNetWork = false;
//            hideStatusUI();
//
//        } else if (v.getId() == R.id.player_view_player_replay_icon) {
//           // 重新播放
//
//        } else if (v.getId() == R.id.player_view_player_video_freeTie_icon) {
//            // 购买会员
        }else if (v.getId() == R.id.player_view_player) {  // 最外层布局点击事件
//            mRelativeLayout.setClickable(true);
            hideShowViewAll();
            LogUtil.e("***** 最外层布局点击事件 ***** = ",indexnnn++);
        }
    }

/********************************************************************************************************
******************************************** 下面是手势 *************************************************************
/********************************************************************************************************/


//int index;
int indexnnn;
//    /**
//     * 播放器的手势监听
//     */
//    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        /**
//         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
//         */
//        private boolean isDownTouch;
//        /**
//         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
//         */
//        private boolean isVolume;
//        /**
//         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
//         */
//        private boolean isLandscape;
//
//        /**
//         * 双击
//         */
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            // 视频视窗双击事件
//            if (!isForbidTouch && !isForbidDoulbeUp) {
//
//            }
//            return true;
//        }
//
//        /**
//         * 按下
//         */
//        @Override
//        public boolean onDown(MotionEvent e) {
//            isDownTouch = true;
//
//            LogUtil.e("***** 按下 *****", index++);
//            return super.onDown(e);
//        }
//
//        /**
//         * 滑动
//         */
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            if (!isForbidTouch) {
//                float mOldX = e1.getX(), mOldY = e1.getY();
//                float deltaY = mOldY - e2.getY();
//                float deltaX = mOldX - e2.getX();
//                if (isDownTouch) {
//                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
//                    isVolume = mOldX > screenWidthPixels * 0.5f;
//                    isDownTouch = false;
//                }
//
//                if (isLandscape) {
//                    // 进度设置
//                    onProgressSlide(-deltaX / mIjkVideoView.getWidth());
//                } else {
//                    float percent = deltaY / mIjkVideoView.getHeight();
//                    if (isVolume) {
//                        // 声音设置
//                        onVolumeSlide(percent);
//                    } else {
//                        // 亮度设置
//                        onBrightnessSlide(percent);
//                    }
//                }
//            }
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }
//
//        /**
//         * 单击
//         */
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            // 视频视窗单击事件
//            if (!isForbidTouch) {
//                // 显示和隐藏操作面板
//                hideShowViewAll();
//                LogUtil.e("***** 单击事件 ***** = ",indexnnn++);
//            }
//            return true;
//        }
//    }
//
//    /**
//     * 手势滑动改变声音大小
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
//        volumeIcon.setImageResource(i == 0 ? R.drawable.player_volume_off_white : R.drawable.player_volume_up_white);
//        brightnessLinearLayout.setVisibility(View.GONE);
//        fastForwardLinearLayout.setVisibility(View.GONE);
//        volumeLinearLayout.setVisibility(View.VISIBLE);
//        volumeText.setVisibility(View.VISIBLE);
//        volumeText.setText(s);
//    }
//
//    /**
//     * 手势 快进或者快退滑动改变进度
//     *
//     * @param percent
//     */
//    private void onProgressSlide(float percent) {
//        int position = (int) mIjkVideoView.getCurrentPosition();
//        long duration = mIjkVideoView.getDuration();
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
//            fastForwardLinearLayout.setVisibility(View.VISIBLE);
//            brightnessLinearLayout.setVisibility(View.GONE);
//            volumeLinearLayout.setVisibility(View.GONE);
//            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            fastForwardText.setText(text + "s");
//            currentTimePosition.setText(CommonUtil.generateTime(newPosition) + "/");
//            totalTimePosition.setText(CommonUtil.generateTime(duration));
//        }
//    }
//
//    /**
//     * 手势 亮度滑动改变亮度
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
//        brightnessLinearLayout.setVisibility(View.VISIBLE);
//        fastForwardLinearLayout.setVisibility(View.GONE);
//        volumeLinearLayout.setVisibility(View.GONE);
//        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
//        lpa.screenBrightness = brightness + percent;
//        if (lpa.screenBrightness > 1.0f) {
//            lpa.screenBrightness = 1.0f;
//        } else if (lpa.screenBrightness < 0.01f) {
//            lpa.screenBrightness = 0.01f;
//        }
//        brightnessText.setText(((int) (lpa.screenBrightness * 100)) + "%");
//        mActivity.getWindow().setAttributes(lpa);
//    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            mHandler.removeMessages(HandlerWhat.MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(HandlerWhat.MESSAGE_SEEK_NEW_POSITION);
        }
        mHandler.removeMessages(HandlerWhat.MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(HandlerWhat.MESSAGE_HIDE_CENTER_BOX, 500);
//        if (mAutoPlayRunnable != null) {
//            mAutoPlayRunnable.start();
//        }
    }
}

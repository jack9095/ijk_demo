//package com.fly.playertool.view;
//
//
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//
///**
// * Created by fei.wang on 2018/6/20.
// * 播放控件  各种内部类层
// */
//public class MiddlePlayerViewCopy extends ExternalMethodsPlayerViewCopy{
//
//    /**
//     * 收起控制面板轮询，默认5秒无操作，收起控制面板，
//     */
//    private class AutoPlayRunnable implements Runnable {
//        private int AUTO_PLAY_INTERVAL = 5000;
//        private boolean mShouldAutoPlay;
//
//        /**
//         * 五秒无操作，收起控制面板
//         */
//        public AutoPlayRunnable() {
//            mShouldAutoPlay = false;
//        }
//
//        public void start() {
//            if (!mShouldAutoPlay) {
//                mShouldAutoPlay = true;
//                mHandler.removeCallbacks(this);
//                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
//            }
//        }
//
//        public void stop() {
//            if (mShouldAutoPlay) {
//                mHandler.removeCallbacks(this);
//                mShouldAutoPlay = false;
//            }
//        }
//
//        @Override
//        public void run() {
//            if (mShouldAutoPlay) {
//                mHandler.removeCallbacks(this);
//                if (!isForbidTouch && !isShowControlPanl) {
//                    operatorPanl();
//                }
//            }
//        }
//    }
//
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
//            /**视频视窗双击事件*/
//            if (!isForbidTouch && !isOnlyFullScreen && !isForbidDoulbeUp) {
//                toggleFullScreen();
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
//            return super.onDown(e);
//        }
//
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
//                    if (!isLive) {
//                        /**进度设置*/
//                        onProgressSlide(-deltaX / videoView.getWidth());
//                    }
//                } else {
//                    float percent = deltaY / videoView.getHeight();
//                    if (isVolume) {
//                        /**声音设置*/
//                        onVolumeSlide(percent);
//                    } else {
//                        /**亮度设置*/
//                        onBrightnessSlide(percent);
//                    }
//
//
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
//            /**视频视窗单击事件*/
//            if (!isForbidTouch) {
//                operatorPanl();
//            }
//            return true;
//        }
//    }
//}

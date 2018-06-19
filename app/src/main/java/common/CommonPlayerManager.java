package common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import utils.ScreenUtils;
import widget.IRenderView;
import widget.IjkVideoView;

public class CommonPlayerManager {
    /**
     * 可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     */
    public static final String SCALETYPE_FITPARENT = "fitParent";
    /**
     * 可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     */
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    /**
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     */
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    /**
     * 不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY = "fitXY";
    /**
     * 不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     */
    public static final String SCALETYPE_16_9 = "16:9";
    /**
     * 不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     */
    public static final String SCALETYPE_4_3 = "4:3";

    /**
     * 状态常量
     */
    public final int STATUS_ERROR = -1;     // 错误
    public final int STATUS_IDLE = 0;       // 空闲
    public final int STATUS_LOADING = 1;    // 加载中
    public final int STATUS_PLAYING = 2;    // 播放中
    public final int STATUS_PAUSE = 3;      // 暂停
    public final int STATUS_COMPLETED = 4;  // 播放完

    /*********************************** 上面为常量 **********************************************************/
    /*******************************************************************************************************/
    /*******************************************************************************************************/
    /*******************************************************************************************************/
    /*******************************************************************************************************/

    public long defaultRetryTime = 5000;
    public boolean fullScreenOnly;   // 是否为全屏 （横屏）
    public boolean portrait;     // 垂直
    public AudioManager audioManager;  // 音频管理
    public GestureDetector gestureDetector;  // 手势检测
    public final int mMaxVolume;     // 最大音量
    public int screenWidthPixels;    // 屏幕实际宽度
    public Context context;    // 上下文
    public int volume = -1;    // 音量
    public long newPosition = -1;  // 当前播放的点（时长  毫秒）
    public float brightness = -1; // 当前屏幕的亮度
    public boolean isLive = false;//是否为直播
    public boolean playerSupport; // 是否支持设备
    public long pauseTime;         // 暂停时间
    public String url;      // 播放的流
    public IjkVideoView videoView;

    public CommonPlayerManager(Context context, IjkVideoView videoView) {
        this.context = context;
        this.videoView = videoView;
        playerSupport = true;
        screenWidthPixels = ScreenUtils.getScreenWidth(QuApplication.getAppContext());
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gestureDetector = new GestureDetector(context, new PlayerGestureListener());

        //        if (fullScreenOnly) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        portrait=getScreenOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        if (!playerSupport) {
            LogUtil.e("播放器不支持此设备");
        }
    }

    /**
     * 手势
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            videoView.toggleAspectRatio();
            Toast.makeText(context, "默认Toast样式", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }
            } else {
                float percent = deltaY / videoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        Toast.makeText(context, "控制声音", Toast.LENGTH_SHORT).show();
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        LogUtil.d("onVolumeSlide:" + s);
    }

    // 滑动view进度的处理
    private void onProgressSlide(float percent) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
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
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            LogUtil.d("onProgressSlide:" + text);
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        Toast.makeText(context, "控制亮度", Toast.LENGTH_SHORT).show();
//        if (brightness < 0) {
//            brightness = context.getWindow().getAttributes().screenBrightness;
//            if (brightness <= 0.00f){
//                brightness = 0.50f;
//            }else if (brightness < 0.01f){
//                brightness = 0.01f;
//            }
//        }
//        DebugLog.d("brightness:"+brightness+",percent:"+ percent);
//        WindowManager.LayoutParams lpa = context.getWindow().getAttributes();
//        lpa.screenBrightness = brightness + percent;
//        if (lpa.screenBrightness > 1.0f){
//            lpa.screenBrightness = 1.0f;
//        }else if (lpa.screenBrightness < 0.01f){
//            lpa.screenBrightness = 0.01f;
//        }
//        activity.getWindow().setAttributes(lpa);
    }

    /**
     * 生成时间
     *
     * @param time 毫秒
     * @return
     */
    public String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 屏幕旋转
     * @return
     */
//    private int getScreenOrientation() {
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        DisplayMetrics dm = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        int orientation;
//        // if the device's natural orientation is portrait:
//        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width ||
//                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//                case Surface.ROTATION_90:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_180:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
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
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                default:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//            }
//        }
//        return orientation;
//    }

    /**
     * <pre>
     *     fitParent:可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     *     fillParent:可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     *     wrapContent:将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     *     fitXY:不剪裁,非等比例拉伸画面填满整个View
     *     16:9:不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     *     4:3:不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     * </pre>
     *
     * @param scaleType
     */
    public void setScaleType(String scaleType) {
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        } else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        } else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        } else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
    }

    public PlayerStateListener playerStateListener;    // 播放状态监听

    public void setPlayerStateListener(PlayerStateListener playerStateListener) {
        this.playerStateListener = playerStateListener;
    }

    public interface PlayerStateListener {
        void onComplete();

        void onError();

        void onLoading();

        void onPlay();
    }
    /************************************** 没用的监听 **********************************************/


    /************************************************************************************************/
    /************************************************************************************************/
    /************************************************************************************************/

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnCompleteListener {

        void onComplete();
    }

    public interface OnInfoListener {

        void onInfo(int what, int extra);
    }

    public void onError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void onComplete(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void onInfo(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    public OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(int what, int extra) {
        }
    };

    public OnCompleteListener onCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete() {
        }
    };

    public OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };

    public boolean onBackPressed() {
//        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return true;
//        }
        return false;
    }

    public void playInFullScreen(boolean fullScreen) {
        if (fullScreen) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    /**
     * is player support this device
     *
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }

    /**
     * try to play when error(only for live video)
     *
     * @param defaultRetryTime millisecond,0 will stop retry,default is 5000 millisecond
     */
    public void setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
    }

    /**
     * 设置横竖屏
     * //     * @param fullScreenOnly
     */
//    public void setFullScreenOnly(boolean fullScreenOnly) {
//        this.fullScreenOnly = fullScreenOnly;
//        tryFullScreen(fullScreenOnly);
//        if (fullScreenOnly) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//        }
//    }
//
//    private void tryFullScreen(boolean fullScreen) {
//        if (activity instanceof AppCompatActivity) {
//            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
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
//    private void setFullScreen(boolean fullScreen) {
//        if (activity != null) {
//            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
//            if (fullScreen) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            } else {
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            }
//        }
//    }


// 没用到的
    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity = activity;
        }

        public Query id(int id) {
            view = activity.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(View.OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }

        private void size(boolean width, int n, boolean dip) {
            if (view != null) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (n > 0 && dip) {
                    n = dip2pixel(activity, n);
                }
                if (width) {
                    lp.width = n;
                } else {
                    lp.height = n;
                }
                view.setLayoutParams(lp);
            }
        }

        public void height(int height, boolean dip) {
            size(false, height, dip);
        }

        public int dip2pixel(Context context, float n) {
            int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
            return value;
        }

        public float pixel2dip(Context context, float n) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = n / (metrics.densityDpi / 160f);
            return dp;
        }
    }
}

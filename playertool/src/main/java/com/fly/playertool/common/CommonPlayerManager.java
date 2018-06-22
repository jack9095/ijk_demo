package com.fly.playertool.common;

import android.content.Context;
import android.media.AudioManager;
import com.fly.playertool.utils.ScreenUtils;
import com.fly.playertool.widget.IRenderView;
import com.fly.playertool.widget.IjkVideoView;

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

    public AudioManager audioManager;  // 音频管理
    public final int mMaxVolume;     // 最大音量
    public int screenWidthPixels;    // 屏幕实际宽度
    public Context context;    // 上下文
    public long newPosition = -1;  // 当前播放的点（时长  毫秒）
    public boolean playerSupport; // 是否支持设备
    public long pauseTime;         // 暂停时间
    public String url;      // 播放的流
    public IjkVideoView videoView;

    public CommonPlayerManager(Context context, IjkVideoView videoView) {
        this.context = context;
        this.videoView = videoView;
        playerSupport = true;
        screenWidthPixels = ScreenUtils.getScreenWidth(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

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
}

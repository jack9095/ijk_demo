package com.fly.playertool.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.fly.playertool.R;
import com.fly.playertool.utils.CommonUtil;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放底部栏控件
 */
public class PlayerBottomView extends FrameLayout {

    private ImageView leftBottomPlayer;
    private TextView currentTime;
    private SeekBar seekBar;
    private TextView endTime;
    private TextView stream;
    private ImageView zoom;

    private long totalTimeF;   // 视屏总时长
    private long currentTimeF; // 当前播放时长
    private boolean isDragging; // 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动

    public PlayerBottomView(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayerBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.player_controlbar, this, true);
        leftBottomPlayer = rootView.findViewById(R.id.player_bottom_bar_video_play);
        currentTime = rootView.findViewById(R.id.player_bottom_bar_currentTime);
        seekBar = rootView.findViewById(R.id.player_bottom_bar_seekBar);
        endTime = rootView.findViewById(R.id.player_bottom_bar_endTime);
        stream = rootView.findViewById(R.id.player_bottom_bar_stream);
        zoom = rootView.findViewById(R.id.player_bottom_bar_zoom);

        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
    }

    public ImageView getImageView(){
        return leftBottomPlayer;
    }

    public View getZoomView(){
        return zoom;
    }

    public View getLineView(){
        return stream;
    }

    public void setTotalTime(long totalTime){
        this.totalTimeF = totalTime;
        String time = CommonUtil.generateTime(totalTime);
        if (!TextUtils.isEmpty(time)) {
            endTime.setText(time);
        }else {
            endTime.setText("");
        }
    }

    public void setStream(String streams){
        if (!TextUtils.isEmpty(streams)) {
            stream.setText(streams);
        }else {
            stream.setText("");
        }
    }

    public void setCurrentTime(long currenttime){
        this.currentTimeF = currenttime;
        String time = CommonUtil.generateTime(currenttime);
        if (!TextUtils.isEmpty(time)) {
            currentTime.setText(time);
        }else {
            currentTime.setText("");
        }
    }

    /**
     * 进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        /**数值的改变*/
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                // 不是用户拖动的，自动播放滑动的情况
            } else {
                int currentPosition = (int) ((totalTimeF * progress * 1.0) / 1000);
                setCurrentTime(currentPosition);
            }

        }

        /**开始拖动*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
        }

        /**停止拖动*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
//            videoView.seekTo((int) ((totalTimeF * seekBar.getProgress() * 1.0) / 1000)); // 给播放器设置
            isDragging = false;
            syncProgress(totalTimeF,currentTimeF,0);
//            if (!isDragging) {
//                updatePausePlay();
//            }
        }
    };

    /**
     * 同步进度
     * @param totalTime   视频总时长
     * @param currentTime 当前播放的时长
     * @param bufferTime 缓存的时长
     */
    public void syncProgress(long totalTime,long currentTime,int bufferTime) {
        if (isDragging) {
            return;
        }

        if (seekBar != null) {
            if (totalTime > 0) {
                long pos = 1000L * currentTime / totalTime;
                seekBar.setProgress((int) pos);
            }
//            seekBar.setSecondaryProgress(bufferTime * 10);
        }

//        if (isCharge && maxPlaytime + 1000 < getCurrentPosition()) { // 最大试看时长
//            query.id(R.id.app_video_freeTie).visible();  // 最大试看时长提示语
//            pausePlay(); // 暂停
//        } else {
            setCurrentTime(currentTime);
            setTotalTime(totalTime);
//        }
    }
}

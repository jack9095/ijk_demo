package com.fly.playertool.common;

import android.content.Context;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.widget.IjkVideoView;

public class PlayerManager extends CommonPlayerManager{

    private int currentPosition;  // 当前播放点
    private int status = STATUS_IDLE; // 状态

    // 初始化
    public PlayerManager(IjkVideoView mIjkVideoView, Context context) {
        super(context,mIjkVideoView);
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            LogUtil.e("GiraffePlayer" + e);
        }

        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(STATUS_COMPLETED);
                onCompleteListener.onComplete();
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(STATUS_ERROR);
                onErrorListener.onError(what,extra);
                return true;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                LogUtil.e("what = " + what + "   extra = " + extra);
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:  // 开始缓存
                        statusChange(STATUS_LOADING);
//                        if (loadingSpeedText != null) {
//                            loadingSpeedText.setText(CommonUtil.getFormatSize(extra)); // 显示加载速度
//                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:   // 结束缓存
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        // 显示下载速度
//                      Toast.show("download rate:" + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:  // 播放
                        statusChange(STATUS_PLAYING);
                        break;
                }
                onInfoListener.onInfo(what,extra);
                return false;
            }
        });
    }

    private void statusChange(int newStatus) {
        status = newStatus;
        switch (newStatus){
            case STATUS_COMPLETED:
                LogUtil.e("statusChange STATUS_COMPLETED... 播放完成");
                if (playerStateListener != null){
                    playerStateListener.onComplete();
                }
                break;
            case STATUS_ERROR:
                LogUtil.e("PlayerManager错误");
                if (playerStateListener != null){
                    playerStateListener.onError();
                }
                break;
            case STATUS_LOADING:
                //            $.id(R.id.app_video_loading).visible();
                if (playerStateListener != null){
                    playerStateListener.onLoading();
                }
                LogUtil.e("PlayerManager加载");
                break;
            case STATUS_PLAYING:
                LogUtil.e("PlayerManager播放");
                if (playerStateListener != null){
                    playerStateListener.onPlay();
                }
                break;
            case STATUS_PAUSE:
                LogUtil.e("PlayerManager暂停");
                break;
        }
    }

    public void onPause() {
        pauseTime= System.currentTimeMillis();
        if (status==STATUS_PLAYING) {
            videoView.pause();
//            if (!isLive) {
//                currentPosition = videoView.getCurrentPosition();
//            }
        }
    }

    public void onResume() {
        pauseTime=0;
        if (status==STATUS_PLAYING) {
//            if (isLive) {
//                videoView.seekTo(0);
//            } else {
//                if (currentPosition>0) {
//                    videoView.seekTo(currentPosition);
//                }
//            }
            videoView.start();
        }
    }

    // 设置播放  参数为播放的流
    public void play(String url) {
        this.url = url;
        if (playerSupport) {
            videoView.setVideoPath(url); // 设置视频路径
            videoView.start();
        }
    }

    // 开始播放
    public void start() {
        videoView.start();
    }

    // 暂停播放
    public void pause() {
        videoView.pause();
    }

    // 是否正在播放
    public boolean isPlaying() {
        return videoView!=null?videoView.isPlaying():false;
    }

    // 停止播放
    public void stop(){
        videoView.stopPlayback();
    }

    // 获取当前播放点
    public int getCurrentPosition(){
        return videoView.getCurrentPosition();
    }

    // 获取视频时长
    public int getDuration(){
        return videoView.getDuration();
    }

    // 是否是直播
    public PlayerManager live(boolean isLive) {
//        this.isLive = isLive;
        return this;
    }

    // //改变视频缩放状态
    public void toggleAspectRatio(){
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
    }
}
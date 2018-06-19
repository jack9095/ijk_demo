package common;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import widget.IjkVideoView;

public class PlayerManager extends CommonPlayerManager{

    private int currentPosition;  // 当前播放点
    private int status = STATUS_IDLE; // 状态

    // 初始化
    public PlayerManager(IjkVideoView mIjkVideoView) {
        super(QuApplication.getAppContext(),mIjkVideoView);
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            DebugLog.e("GiraffePlayer" + e);
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
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(STATUS_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示下载速度
//                      Toast.show("download rate:" + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
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
        if (!isLive && newStatus==STATUS_COMPLETED) {
            DebugLog.e("statusChange STATUS_COMPLETED...");
            if (playerStateListener != null){
                playerStateListener.onComplete();
            }
        }else if (newStatus == STATUS_ERROR) {
            DebugLog.e("statusChange STATUS_ERROR...");
            if (playerStateListener != null){
                playerStateListener.onError();
            }
        } else if(newStatus==STATUS_LOADING){
//            $.id(R.id.app_video_loading).visible();
            if (playerStateListener != null){
                playerStateListener.onLoading();
            }
            DebugLog.e("statusChange STATUS_LOADING...");
        } else if (newStatus == STATUS_PLAYING) {
            DebugLog.e("statusChange STATUS_PLAYING...");
            if (playerStateListener != null){
                playerStateListener.onPlay();
            }
        }
    }

    public void onPause() {
        pauseTime= System.currentTimeMillis();
        if (status==STATUS_PLAYING) {
            videoView.pause();
            if (!isLive) {
                currentPosition = videoView.getCurrentPosition();
            }
        }
    }

    public void onResume() {
        pauseTime=0;
        if (status==STATUS_PLAYING) {
            if (isLive) {
                videoView.seekTo(0);
            } else {
                if (currentPosition>0) {
                    videoView.seekTo(currentPosition);
                }
            }
            videoView.start();
        }
    }

    // 设置播放  参数为播放的流
    public void play(String url) {
        this.url = url;
        if (playerSupport) {
            videoView.setVideoPath(url);
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
        this.isLive = isLive;
        return this;
    }

    // 触觉谱系
    public void toggleAspectRatio(){
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
    }
}
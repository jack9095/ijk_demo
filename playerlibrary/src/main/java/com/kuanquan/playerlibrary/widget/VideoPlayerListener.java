package com.kuanquan.playerlibrary.widget;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 提供回调的接口
 * Created by fei.wang on 2018/6/23.
 */

public abstract class VideoPlayerListener implements IMediaPlayer.OnBufferingUpdateListener
        , IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener
        , IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener
        , IMediaPlayer.OnSeekCompleteListener {
}

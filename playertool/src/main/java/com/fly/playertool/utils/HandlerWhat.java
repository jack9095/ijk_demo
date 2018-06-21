package com.fly.playertool.utils;

/**
 * Created by Administrator on 2018/6/21.
 */

public interface HandlerWhat {
    /**
     * 同步进度
     */
    int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 设置新位置
     */
    int MESSAGE_SEEK_NEW_POSITION = 3;
    /**
     * 重新播放
     */
    int MESSAGE_RESTART_PLAY = 5;
}

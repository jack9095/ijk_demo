package com.kuanquan.customplayer.utils;

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

    /**
     * 隐藏手势出现的布局  （声音、亮度、快进快退）
     */
    int MESSAGE_HIDE_CENTER_BOX = 6;
}

package com.fly.playertool.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fly.playertool.R;
import com.fly.playertool.bean.VideoijkBean;
import com.fly.playertool.utils.LogUtil;
import com.fly.playertool.utils.NetworkUtils;
import com.fly.playertool.widget.IjkVideoView;
import com.fly.playertool.widget.PlayStateParams;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fei.wang on 2018/6/20.
 * 播放控件  变量常量层
 */
public class BasePlayerViewCopy {
    /**
     * 打印日志的TAG
     */
    protected static  String TAG = BasePlayerViewCopy.class.getSimpleName();
    /**
     * 全局上下文
     */
    protected  Context mContext;
    /**
     * 依附的容器Activity
     */
    protected  Activity mActivity;
    /**
     * Activity界面的中布局的查询器
     */
    protected  LayoutQuery query;
    /**
     * 原生的Ijkplayer
     */
    protected  IjkVideoView videoView;
    /**
     * 播放器整个界面
     */
    protected  View rl_box;
    /**
     * 播放器顶部控制bar
     */
    protected  View ll_topbar;
    /**
     * 播放器底部控制bar
     */
    protected  View ll_bottombar;
    /**
     * 播放器封面，播放前的封面或者缩列图
     */
    protected  ImageView iv_trumb;
    /**
     * 视频方向旋转按钮
     */
    protected  ImageView iv_rotation;
    /**
     * 视频返回按钮
     */
    protected  ImageView iv_back;
    /**
     * 视频菜单按钮
     */
    protected  ImageView iv_menu;
    /**
     * 视频bottonbar的播放按钮
     */
    protected  ImageView iv_bar_player;
    /**
     * 视频中间的播放按钮
     */
    protected  ImageView iv_player;
    /**
     * 视频全屏按钮
     */
    protected  ImageView iv_fullscreen;
    /**
     * 菜单面板
     */
    protected  View settingsContainer;
    /**
     * 声音面板
     */
    protected  View volumeControllerContainer;
    /**
     * 亮度面板
     */
    protected  View brightnessControllerContainer;
    /**
     * 声音进度
     */
    protected  SeekBar volumeController;
    /**
     * 亮度进度
     */
    protected  SeekBar brightnessController;
    /**
     * 视频分辨率按钮
     */
    protected  TextView tv_steam;
    /**
     * 视频加载速度
     */
    protected  TextView tv_speed;
    /**
     * 视频播放进度条
     */
    protected  SeekBar seekBar;
    /**
     * 不同分辨率列表的外层布局
     */
    protected  LinearLayout streamSelectView;
    /**
     * 码流列表
     */
    protected List<VideoijkBean> listVideos = new ArrayList<VideoijkBean>();

    /**
     * 当前状态
     */
    protected int status = PlayStateParams.STATE_IDLE;
    /**
     * 当前播放位置
     */
    protected int currentPosition;
    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    protected long newPosition = -1;
    /**
     * 视频旋转的角度，默认只有0,90.270分别对应向上、向左、向右三个方向
     */
    protected int rotation = 0;
    /**
     * 视频显示比例,默认保持原视频的大小
     */
    protected int currentShowType = PlayStateParams.fitparent;
    /**
     * 播放总时长
     */
    protected long duration;
    /**
     * 当前声音大小
     */
    protected int volume;
    /**
     * 设备最大音量
     */
    protected  int mMaxVolume;
    /**
     * 获取当前设备的宽度
     */
    protected  int screenWidthPixels;
    /**
     * 记录播放器竖屏时的高度
     */
    protected  int initHeight;
    /**
     * 当前亮度大小
     */
    protected float brightness;
    /**
     * 当前播放地址
     */
    protected String currentUrl;
    /**
     * 当前选择的视频流索引
     */
    protected int currentSelect;
    /**
     * 记录进行后台时的播放状态0为播放，1为暂停
     */
    protected int bgState;
    /**
     * 自动重连的时间
     */
    protected int autoConnectTime = 5000;
    /**
     * 第三方so是否支持，默认不支持，true为支持
     */
    protected boolean playerSupport;
    /**
     * 是否是直播 默认为非直播，true为直播false为点播，根据isLive()方法前缀rtmp或者后缀.m3u8判断得出的为直播，比较片面，有好的建议欢迎交流
     */
    protected boolean isLive;
    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    protected boolean isShowControlPanl;
    /**
     * 禁止触摸，默认可以触摸，true为禁止false为可触摸
     */
    protected boolean isForbidTouch;
    /**
     * 禁止收起控制面板，默认可以收起，true为禁止false为可触摸
     */
    protected boolean isForbidHideControlPanl;
    /**
     * 当前是否切换视频流，默认为否，true是切换视频流，false没有切换
     */
    protected boolean isHasSwitchStream;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    protected boolean isDragging;
    /**
     * 播放的时候是否需要网络提示，默认显示网络提示，true为显示网络提示，false不显示网络提示
     */
    protected boolean isGNetWork = true;

    protected boolean isCharge;
    protected int maxPlaytime;
    /**
     * 是否只有全屏，默认非全屏，true为全屏，false为非全屏
     */
    protected boolean isOnlyFullScreen;
    /**
     * 是否禁止双击，默认不禁止，true为禁止，false为不禁止
     */
    protected boolean isForbidDoulbeUp;
    /**
     * 是否出错停止播放，默认是出错停止播放，true出错停止播放,false为用户点击停止播放
     */
    protected boolean isErrorStop = true;
    /**
     * 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
     */
    protected boolean isPortrait = true;
    /**
     * 是否隐藏中间播放按钮，默认不隐藏，true为隐藏，false为不隐藏
     */
    protected boolean isHideCenterPlayer;
    /**
     * 是否自动重连，默认5秒重连，true为重连，false为不重连
     */
    protected boolean isAutoReConnect = true;
    /**
     * 是否隐藏topbar，true为隐藏，false为不隐藏
     */
    protected boolean isHideTopBar;
    /**
     * 是否隐藏bottonbar，true为隐藏，false为不隐藏
     */
    protected boolean isHideBottonBar;
    /**
     * 音频管理器
     */
    protected AudioManager audioManager;

/*******************************************************************************************************/
/************************************  下面是 Handler 的几个 what  *************************************/
/*******************************************************************************************************/
    /**
     * 同步进度
     */
    protected final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 设置新位置
     */
    protected final int MESSAGE_SEEK_NEW_POSITION = 3;
    /**
     * 隐藏提示的box
     */
    protected final int MESSAGE_HIDE_CENTER_BOX = 4;
    /**
     * 重新播放
     */
    protected final int MESSAGE_RESTART_PLAY = 5;
}

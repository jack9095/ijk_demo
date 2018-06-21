//package com.fly.playertool.view;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.media.AudioManager;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.provider.Settings;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.OrientationEventListener;
//import android.view.Surface;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.fly.playertool.R;
//import com.fly.playertool.widget.PlayStateParams;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
///**
// * 播放器世纪控制View
// */
//public class PlayerViewCopy extends MiddlePlayerViewCopy {
//
//    /**
//     * 消息处理
//     */
//    @SuppressWarnings("HandlerLeak")
//    private Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                /**滑动完成，隐藏滑动提示的box*/
//                case MESSAGE_HIDE_CENTER_BOX:
//                    query.id(R.id.app_video_volume_box).gone();
//                    query.id(R.id.app_video_brightness_box).gone();
//                    query.id(R.id.app_video_fastForward_box).gone();
//                    break;
//                /**滑动完成，设置播放进度*/
//                case MESSAGE_SEEK_NEW_POSITION:
//                    if (!isLive && newPosition >= 0) {
//                        videoView.seekTo((int) newPosition);
//                        newPosition = -1;
//                    }
//                    break;
//                /**滑动中，同步播放进度*/
//                case MESSAGE_SHOW_PROGRESS:
//                    long pos = syncProgress();
//                    if (!isDragging && isShowControlPanl) {
//                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
//                        sendMessageDelayed(msg, 1000 - (pos % 1000));
//                        updatePausePlay();
//                    }
//                    break;
//                /**重新去播放*/
//                case MESSAGE_RESTART_PLAY:
//                    status = PlayStateParams.STATE_ERROR;
//                    startPlay();
//                    updatePausePlay();
//                    break;
//            }
//        }
//    };
//
//    /**========================================视频的监听方法==============================================*/
//
//    /**
//     * 控制面板收起或者显示的轮询监听
//     */
//    private AutoPlayRunnable mAutoPlayRunnable = new AutoPlayRunnable();
//    /**
//     * Activity界面方向监听
//     */
//    private final OrientationEventListener orientationEventListener;
//    /**
//     * 控制面板显示或隐藏监听
//     */
//    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener;
//    /**
//     * 视频封面显示监听
//     */
//    private OnShowThumbnailListener mOnShowThumbnailListener;
//    /**
//     * 视频的返回键监听
//     */
//    private OnPlayerBackListener mPlayerBack;
//    /**
//     * 视频播放时信息回调
//     */
//    private IMediaPlayer.OnInfoListener onInfoListener;
//
//    /**
//     * 点击事件监听
//     */
//    private final View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v.getId() == R.id.app_video_menu) {
//                /**菜单*/
//                showMenu();
//            } else if (v.getId() == R.id.app_video_stream) {
//                /**选择分辨率*/
//                showStreamSelectView();
//            } else if (v.getId() == R.id.ijk_iv_rotation) {
//                /**旋转视频方向*/
//                setPlayerRotation();
//            } else if (v.getId() == R.id.app_video_fullscreen) {
//                /**视频全屏切换*/
//                toggleFullScreen();
//            } else if (v.getId() == R.id.app_video_play || v.getId() == R.id.play_icon) {
//                /**视频播放和暂停*/
//                if (videoView.isPlaying()) {
//                    if (isLive) {
//                        videoView.stopPlayback();
//                    } else {
//                        pausePlay();
//                    }
//                } else {
//                    startPlay();
//                    if (videoView.isPlaying()) {
//                        /**ijkplayer内部的监听没有回调，只能手动修改状态*/
//                        status = PlayStateParams.STATE_PREPARING;
//                        hideStatusUI();
//                    }
//                }
//                updatePausePlay();
//            } else if (v.getId() == R.id.app_video_finish) {
//                /**返回*/
//                if (!isOnlyFullScreen && !isPortrait) {
//                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                } else {
//                    if (mPlayerBack != null) {
//                        mPlayerBack.onPlayerBack();
//                    } else {
//                        mActivity.finish();
//                    }
//                }
//            } else if (v.getId() == R.id.app_video_netTie_icon) {
//                /**使用移动网络提示继续播放*/
//                isGNetWork = false;
//                hideStatusUI();
//                startPlay();
//                updatePausePlay();
//            } else if (v.getId() == R.id.app_video_replay_icon) {
//                /**重新播放*/
//                status = PlayStateParams.STATE_ERROR;
//                hideStatusUI();
//                startPlay();
//                updatePausePlay();
//            }
//        }
//    };
//
//    /**
//     * 进度条滑动监听
//     */
//    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
//
//        /**数值的改变*/
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (!fromUser) {
//                /**不是用户拖动的，自动播放滑动的情况*/
//                return;
//            } else {
//                long duration = getDuration();
//                int position = (int) ((duration * progress * 1.0) / 1000);
//                String time = generateTime(position);
//                query.id(R.id.app_video_currentTime).text(time);
//                query.id(R.id.app_video_currentTime_full).text(time);
//                query.id(R.id.app_video_currentTime_left).text(time);
//            }
//
//        }
//
//        /**开始拖动*/
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            isDragging = true;
//            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
//        }
//
//        /**停止拖动*/
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            long duration = getDuration();
//            videoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
//            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
//            isDragging = false;
//            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
//        }
//    };
//
//    /**
//     * 亮度进度条滑动监听
//     */
//    private final SeekBar.OnSeekBarChangeListener onBrightnessControllerChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        /**数值的改变*/
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            setBrightness(progress);
//        }
//
//        /**开始拖动*/
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        /**停止拖动*/
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            brightness = -1;
//        }
//    };
//
//    public void setBrightness(int value) {
//        WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
//        if (brightness < 0) {
//            brightness = mActivity.getWindow().getAttributes().screenBrightness;
//            if (brightness <= 0.00f) {
//                brightness = 0.50f;
//            } else if (brightness < 0.01f) {
//                brightness = 0.01f;
//            }
//        }
//        if (value < 1) {
//            value = 1;
//        }
//        if (value > 100) {
//            value = 100;
//        }
//        layout.screenBrightness = 1.0F * (float) value / 100.0F;
//        if (layout.screenBrightness > 1.0f) {
//            layout.screenBrightness = 1.0f;
//        } else if (layout.screenBrightness < 0.01f) {
//            layout.screenBrightness = 0.01f;
//        }
//        this.mActivity.getWindow().setAttributes(layout);
//    }
//
//
//    /**
//     * 声音进度条滑动监听
//     */
//    private final SeekBar.OnSeekBarChangeListener onVolumeControllerChangeListener = new SeekBar.OnSeekBarChangeListener() {
//
//        /**数值的改变*/
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            int index = (int) (mMaxVolume * progress * 0.01);
//            if (index > mMaxVolume)
//                index = mMaxVolume;
//            else if (index < 0)
//                index = 0;
//            // 变更声音
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
//        }
//
//        /**开始拖动*/
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        /**停止拖动*/
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            volume = -1;
//        }
//    };
//
//
//    /**
//     * ========================================视频的监听方法==============================================
//     */
//
//    /**
//     * 保留旧的调用方法
//     */
//    public PlayerViewCopy(Activity activity) {
//        this(activity, null);
//    }
//
//    /**
//     * 新的调用方法，适用非Activity中使用PlayerView，例如fragment、holder中使用
//     */
//    public PlayerViewCopy(Activity activity, View rootView) {
//        this.mActivity = activity;
//        this.mContext = activity;
//        try {
//            IjkMediaPlayer.loadLibrariesOnce(null);
//            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//            playerSupport = true;
//        } catch (Throwable e) {
//            Log.e(TAG, "loadLibraries error", e);
//        }
//        screenWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
//        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        if (rootView == null) {
//            query = new LayoutQuery(mActivity);
//            rl_box = mActivity.findViewById(R.id.app_video_box);
//            videoView = (IjkVideoView) mActivity.findViewById(R.id.video_view);
//            settingsContainer = mActivity.findViewById(R.id.simple_player_settings_container);
//            settingsContainer.setVisibility(View.GONE);
//            volumeControllerContainer = mActivity.findViewById(R.id.simple_player_volume_controller_container);
//            /**声音进度*/
//            volumeController = (SeekBar) mActivity.findViewById(R.id.simple_player_volume_controller);
//            volumeController.setMax(100);
//            volumeController.setOnSeekBarChangeListener(this.onVolumeControllerChangeListener);
//            /**亮度进度*/
//            brightnessControllerContainer = mActivity.findViewById(R.id.simple_player_brightness_controller_container);
//            brightnessController = (SeekBar) mActivity.findViewById(R.id.simple_player_brightness_controller);
//            brightnessController.setMax(100);
//        } else {
//            query = new LayoutQuery(mActivity, rootView);
//            rl_box = rootView.findViewById(R.id.app_video_box);
//            videoView = (IjkVideoView) rootView.findViewById(R.id.video_view);
//            settingsContainer = rootView.findViewById(R.id.simple_player_settings_container);
//            settingsContainer.setVisibility(View.GONE);
//            volumeControllerContainer = rootView.findViewById(R.id.simple_player_volume_controller_container);
//            /**声音进度*/
//            volumeController = (SeekBar) rootView.findViewById(R.id.simple_player_volume_controller);
//            volumeController.setMax(100);
//            volumeController.setOnSeekBarChangeListener(this.onVolumeControllerChangeListener);
//            /**亮度进度*/
//            brightnessControllerContainer = rootView.findViewById(R.id.simple_player_brightness_controller_container);
//            brightnessController = (SeekBar) rootView.findViewById(R.id.simple_player_brightness_controller);
//            brightnessController.setMax(100);
//        }
//
//        try {
//            int e = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//            float progress = 1.0F * (float) e / 255.0F;
//            WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
//            layout.screenBrightness = progress;
//            mActivity.getWindow().setAttributes(layout);
//        } catch (Settings.SettingNotFoundException var7) {
//            var7.printStackTrace();
//        }
//        brightnessController.setOnSeekBarChangeListener(this.onBrightnessControllerChangeListener);
//        if (rootView == null) {
//            streamSelectView = (LinearLayout) mActivity.findViewById(R.id.simple_player_select_stream_container);
//            streamSelectListView = (ListView) mActivity.findViewById(R.id.simple_player_select_streams_list);
//            ll_topbar = mActivity.findViewById(R.id.app_video_top_box);
//            ll_bottombar = mActivity.findViewById(R.id.ll_bottom_bar);
//            iv_trumb = (ImageView) mActivity.findViewById(R.id.iv_trumb);
//            iv_back = (ImageView) mActivity.findViewById(R.id.app_video_finish);
//            iv_menu = (ImageView) mActivity.findViewById(R.id.app_video_menu);
//            iv_bar_player = (ImageView) mActivity.findViewById(R.id.app_video_play);
//            iv_player = (ImageView) mActivity.findViewById(R.id.play_icon);
//            iv_rotation = (ImageView) mActivity.findViewById(R.id.ijk_iv_rotation);
//            iv_fullscreen = (ImageView) mActivity.findViewById(R.id.app_video_fullscreen);
//            tv_steam = (TextView) mActivity.findViewById(R.id.app_video_stream);
//            tv_speed = (TextView) mActivity.findViewById(R.id.app_video_speed);
//            seekBar = (SeekBar) mActivity.findViewById(R.id.app_video_seekBar);
//        } else {
//            streamSelectView = (LinearLayout) rootView.findViewById(R.id.simple_player_select_stream_container);
//            streamSelectListView = (ListView) rootView.findViewById(R.id.simple_player_select_streams_list);
//            ll_topbar = rootView.findViewById(R.id.app_video_top_box);
//            ll_bottombar = rootView.findViewById(R.id.ll_bottom_bar);
//            iv_trumb = (ImageView) rootView.findViewById(R.id.iv_trumb);
//            iv_back = (ImageView) rootView.findViewById(R.id.app_video_finish);
//            iv_menu = (ImageView) rootView.findViewById(R.id.app_video_menu);
//            iv_bar_player = (ImageView) rootView.findViewById(R.id.app_video_play);
//            iv_player = (ImageView) rootView.findViewById(R.id.play_icon);
//            iv_rotation = (ImageView) rootView.findViewById(R.id.ijk_iv_rotation);
//            iv_fullscreen = (ImageView) rootView.findViewById(R.id.app_video_fullscreen);
//            tv_steam = (TextView) rootView.findViewById(R.id.app_video_stream);
//            tv_speed = (TextView) rootView.findViewById(R.id.app_video_speed);
//            seekBar = (SeekBar) rootView.findViewById(R.id.app_video_seekBar);
//        }
//
//        seekBar.setMax(1000);
//        seekBar.setOnSeekBarChangeListener(mSeekListener);
//        iv_bar_player.setOnClickListener(onClickListener);
//        iv_player.setOnClickListener(onClickListener);
//        iv_fullscreen.setOnClickListener(onClickListener);
//        iv_rotation.setOnClickListener(onClickListener);
//        tv_steam.setOnClickListener(onClickListener);
//        iv_back.setOnClickListener(onClickListener);
//        iv_menu.setOnClickListener(onClickListener);
//        query.id(R.id.app_video_netTie_icon).clicked(onClickListener);
//        query.id(R.id.app_video_replay_icon).clicked(onClickListener);
//        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
//                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
//                    LogUtil.e("extra = " + extra);
//                    if (tv_speed != null) {
//                        tv_speed.setText(getFormatSize(extra));
//                    }
//                }
//                statusChange(what);
//                if (onInfoListener != null) {
//                    onInfoListener.onInfo(mp, what, extra);
//                }
//                if (isCharge && maxPlaytime < getCurrentPosition()) {
//                    query.id(R.id.app_video_freeTie).visible();
//                    pausePlay();
//                }
//                return true;
//            }
//        });
//        this.streamSelectAdapter = new StreamSelectAdapter(mContext, listVideos);
//        this.streamSelectListView.setAdapter(this.streamSelectAdapter);
//        this.streamSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                hideStreamSelectView();
//                if (currentSelect == position) {
//                    return;
//                }
//                currentSelect = position;
//                switchStream(position);
//                for (int i = 0; i < listVideos.size(); i++) {
//                    if (i == position) {
//                        listVideos.get(i).setSelect(true);
//                    } else {
//                        listVideos.get(i).setSelect(false);
//                    }
//                }
//                streamSelectAdapter.notifyDataSetChanged();
//                startPlay();
//            }
//        });
//
//        final GestureDetector gestureDetector = new GestureDetector(mContext, new PlayerGestureListener());
//        rl_box.setClickable(true);
//        rl_box.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (mAutoPlayRunnable != null) {
//                            mAutoPlayRunnable.stop();
//                        }
//                        break;
//                }
//                if (gestureDetector.onTouchEvent(motionEvent))
//                    return true;
//                // 处理手势结束
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_UP:
//                        endGesture();
//                        break;
//                }
//                return false;
//            }
//        });
//
//
//        orientationEventListener = new OrientationEventListener(mActivity) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
//                    //竖屏
//                    if (isPortrait) {
//                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
//                    if (!isPortrait) {
//                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                }
//            }
//        };
//        if (isOnlyFullScreen) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        isPortrait = (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        initHeight = rl_box.getLayoutParams().height;
//        hideAll();
//        if (!playerSupport) {
//            showStatus(mActivity.getResources().getString(R.string.not_support));
//        } else {
//            query.id(R.id.ll_bg).visible();
//        }
//    }
//
//    /**==========================================Activity生命周期方法回调=============================*/
//    /**
//     * @Override protected void onPause() {
//     * super.onPause();
//     * if (player != null) {
//     * player.onPause();
//     * }
//     * }
//     */
//    public PlayerView onPause() {
//        bgState = (videoView.isPlaying() ? 0 : 1);
//        getCurrentPosition();
//        videoView.onPause();
//        return this;
//    }
//
//    /**
//     * @Override protected void onResume() {
//     * super.onResume();
//     * if (player != null) {
//     * player.onResume();
//     * }
//     * }
//     */
//    public PlayerView onResume() {
//        videoView.onResume();
//        if (isLive) {
//            videoView.seekTo(0);
//        } else {
//            videoView.seekTo(currentPosition);
//        }
//        if (bgState == 0) {
//
//        } else {
//            pausePlay();
//        }
//        return this;
//    }
//
//    /**
//     * @Override protected void onDestroy() {
//     * super.onDestroy();
//     * if (player != null) {
//     * player.onDestroy();
//     * }
//     * }
//     */
//    public PlayerView onDestroy() {
//        orientationEventListener.disable();
//        mHandler.removeMessages(MESSAGE_RESTART_PLAY);
//        mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
//        videoView.stopPlayback();
//        return this;
//    }
//
//    /**
//     * @Override public void onConfigurationChanged(Configuration newConfig) {
//     * super.onConfigurationChanged(newConfig);
//     * if (player != null) {
//     * player.onConfigurationChanged(newConfig);
//     * }
//     * }
//     */
//    public PlayerView onConfigurationChanged(final Configuration newConfig) {
//        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
//        doOnConfigurationChanged(isPortrait);
//        return this;
//    }
//
//    /**
//     * @Override public void onBackPressed() {
//     * if (player != null && player.onBackPressed()) {
//     * return;
//     * }
//     * super.onBackPressed();
//     * }
//     */
//    public boolean onBackPressed() {
//        if (!isOnlyFullScreen && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * ==========================================Activity生命周期方法回调=============================
//     */
//
//}

package com.kuanquan.customplayer.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by fei.wang on 2018/6/21.
 *  屏幕常亮工具类
 */
public class ScreenWakeLockUtil {

    private Context mContext;
    private PowerManager.WakeLock wakeLock;

    public ScreenWakeLockUtil(Context context) {
        mContext = context;
    }

    public void onCreate(){
        /**常亮*/
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
    }

    public void finish(){
        // 恢复设备亮度状态
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    public void onResume(){
        // 激活设备常亮状态
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

}

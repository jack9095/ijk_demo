package com.kuanquan.ijk_test.tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/6/19.
 */

public class FileUtil {

    /**
     * 播放本地视频
     */
    public static String getLocalVideoPath(String name) {
        String sdCard = Environment.getExternalStorageDirectory().getPath();
        String uri = sdCard + File.separator + name;
        return uri;
    }
}

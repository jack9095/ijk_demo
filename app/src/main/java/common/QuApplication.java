package common;

import android.app.Application;

/**
 * Created by Administrator on 2018/6/4.
 *
 */
public class QuApplication extends Application {

    private static QuApplication mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LogUtil.Builder builder = new LogUtil.Builder(mContext)
                .isLog(true) //是否开启打印
                .isLogBorder(true) //是否开启边框
                .setLogType(LogUtil.TYPE.E) //设置默认打印级别
                .setTag("fly"); //设置默认打印Tag
        LogUtil.init(builder);
    }

    public static QuApplication getAppContext(){
        return mContext;
    }
}

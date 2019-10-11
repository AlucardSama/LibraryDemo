package com.zhengja.android.librarydemo;

import android.app.Application;
import android.os.Environment;
import android.util.Printer;

import com.zhengja.android.comonlib.ApplicationCrashHandler;
import com.zhengja.android.comonlib.Tasty;

import java.io.File;

/**
 * @author zhengja@landicorp.com
 * @description TODO
 * @date 2019/10/11
 * @edit TODO
 */
public class App extends Application {

    public static final String DIR_CRASH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LibDemo" + File.separator + "crash";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        //TastyToast
        Tasty.init(this);
        //crashHandler
        initCrashHandler();

    }

    /**
     * 奔溃日志捕获
     */
    private void initCrashHandler() {
        ApplicationCrashHandler
                .getInstance()
                .init(getApplicationContext())
                .setCrashDir(DIR_CRASH)
                .setRestartActivity(MainActivity.class);

    }


}

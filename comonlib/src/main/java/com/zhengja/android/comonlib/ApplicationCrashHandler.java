package com.zhengja.android.comonlib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用异常崩溃处理器</br>
 * 1)重置应用异常处理器;
 * 2)捕获应用异常堆栈信息并存储;
 * 3)重启应用
 *
 * @author baoxl
 */
public class ApplicationCrashHandler implements Thread.UncaughtExceptionHandler {

    private static String TAG = new Throwable().getStackTrace()[0].getClassName();
    private static ApplicationCrashHandler mInstance = new ApplicationCrashHandler();

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private String crashDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Crash";
    private Class restartActivity;

    public static ApplicationCrashHandler getInstance() {
        return mInstance;
    }

    public ApplicationCrashHandler init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        return this;
    }

    public ApplicationCrashHandler setCrashDir(String crashDir) {
        this.crashDir = crashDir;
        return this;
    }

    public ApplicationCrashHandler setRestartActivity(Class clz) {
        this.restartActivity = clz;
        return this;
    }

    private ApplicationCrashHandler() {
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean handle = handleException(thread, ex);
        if (!handle && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //退出或者是重启
            restartOnCrash();
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }

        Map<String, String> pkgInfo = collectApplicationPacketInfo(mContext, thread);
        String crashInfo = makeCrashInfo(pkgInfo, ex);
        Log.e("Crash","Crash nfo:"+crashInfo);
        Log.e("Crash","Crash dir:"+crashDir);
        String fileName = makeFileName();
        saveCrashInfo(fileName, crashInfo);
        //崩溃后重启
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将重启", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
        return true;
    }

    /**
     * 存放异常日志的目录
     *
     * @return
     */
    private String getCrashDir() {
        return crashDir;
    }

    /**
     * 生成异常日志文件名</br>
     * 生成规则：crash-yyyyMMddHHmmss.log
     *
     * @return 异常日志文件名
     */
    private static String makeFileName() {
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = formatter.format(new Date());
        return "crash-" + time + ".log";
    }

    /**
     * 存储异常日志数据到外部存储的文件中
     *
     * @param fileName  异常日志文件名
     * @param crashInfo 异常日志数据
     * @return
     */
    private boolean saveCrashInfo(String fileName, String crashInfo) {

        Log.e("Crash","Crash info:"+crashInfo);
        Log.e("Crash","Crash dir:"+crashDir);
        Log.e("Crash","Crash fileName:"+fileName);

        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        String dirName = getCrashDir();
        File crashDir = new File(dirName);
        if (!crashDir.exists()) {
            crashDir.mkdirs();
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(dirName + File.separator + fileName);
            bos = new BufferedOutputStream(fos);
            bos.write(crashInfo.getBytes());
            bos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bos);
            closeQuietly(fos);
        }
        return false;
    }

    /**
     * 收集应用程序包和平台相关的信息
     *
     * @param context 应用上下文对象
     * @param thread  抛出异常的线程
     * @return 包和平台相关信息
     */
    private Map<String, String> collectApplicationPacketInfo(Context context, Thread thread) {
        Map<String, String> pkgInfo = new HashMap<String, String>();
        pkgInfo.put("Thread-ID", Long.toString(thread.getId()));
        pkgInfo.put("Thread-Name", thread.getName());
        pkgInfo.put("Thread-Priority", Integer.toString(thread.getPriority()));

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                pkgInfo.put("versionName", versionName);
                pkgInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                pkgInfo.put(field.getName(), field.get(null).toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return pkgInfo;
    }

    /**
     * 构造异常日志信息
     *
     * @param pkgInfo 应用包及平台相关信息
     * @param ex      异常栈
     * @return 异常日志信息
     */
    private String makeCrashInfo(Map<String, String> pkgInfo, Throwable ex) {
        StringBuffer info = new StringBuffer();
        for (Map.Entry<String, String> entry : pkgInfo.entrySet()) {
            info.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append("\n");
        }
        info.append("--------------------------------------------------------------------------------\n");
        Writer stack = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stack);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        info.append(stack.toString());

        return info.toString();
    }

    /**
     * 关闭输出流对象
     *
     * @param os 输出流对象
     */
    private static void closeQuietly(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异常退出
     */
    private void restartOnCrash() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (restartActivity != null) {
            Intent intent = new Intent(mContext, restartActivity);
            PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        System.gc();
    }

}

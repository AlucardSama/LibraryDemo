package com.zhengja.android.comonlib;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

/**
 * @author zhengja@landicorp.com
 * @description TastyToast 工具类
 * @date 2018/6/26
 * @edit TODO
 */
public class Tasty {


    private static Context context;

    /**
     * 初始化
     *
     * @param ctx
     */
    public static void init(Context ctx) {
        context = ctx;
    }


    /**
     * SUCCESS
     *
     * @param msg
     */
    public static void s(String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
    }

    /**
     * ERROR
     *
     * @param msg
     */
    public static void e(String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    /**
     * INFO
     *
     * @param msg
     */
    public static void i(String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.INFO);
    }

    /**
     * WARNING
     *
     * @param msg
     */
    public static void w(String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
    }

    /**
     * CONFUSING
     *
     * @param msg
     */
    public static void c(String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
    }


}

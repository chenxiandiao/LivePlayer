package com.cxd.android.liveplayer.utils;

import android.util.Log;

@SuppressWarnings("unused")
public class KasLog {

    private static boolean mbLog = false;

    public static void setLogEnabled(boolean enalbe) {
        mbLog = enalbe;
    }

    public static boolean isLogEnalbed() {
        return mbLog;
    }

    public static void v(String tag, String msg) {
        if (!mbLog || tag == null || msg == null)
            return;

        Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (!mbLog || tag == null || msg == null)
            return;

        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (!mbLog || tag == null || msg == null)
            return;

        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!mbLog || tag == null || msg == null)
            return;
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!mbLog || tag == null || msg == null)
            return;

        Log.e(tag, msg);
    }

//    public static void obj(String tag, Object obj) {
//        if (!mbLog || tag == null) {
//            return;
//        }
//        Log.d(tag, FormatUtils.formatObject(obj));
//    }
//
//    public static void format(String tag, String template, Object... args) {
//        if (!mbLog || tag == null) {
//            return;
//        }
//        Log.d(tag, FormatUtils.formatString(template, args));
//    }
}
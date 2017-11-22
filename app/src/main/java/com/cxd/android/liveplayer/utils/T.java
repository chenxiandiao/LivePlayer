package com.cxd.android.liveplayer.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

@SuppressWarnings("unused")
public class T {
    
    private T() {
    }
    
    public static void showShort(Context context, CharSequence message) {
        FastToast.showToast(context, message);
    }
    
    public static void showShort(Context context, @StringRes int message) {
        FastToast.showToast(context, message);
    }
    
    public static void release() {
        FastToast.release();
    }
    
    /**
     * 用于多条吐司快速显示
     */
    private static class FastToast {
        private static CharSequence oldMsg;
        private static Toast toast = null;
        private static long oldTime = 0;
        
        static void showToast(Context context, CharSequence s) {
            if (context == null || s == null || s.length() == 0) {
                return;
            }
            if (toast == null) {
                toast = Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);
                toast.show();
                oldMsg = s;
                oldTime = System.currentTimeMillis();
            } else {
                long current = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (current - oldTime > 2000) {
                        toast.show();
                        oldTime = current;
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                    oldTime = current;
                }
            }
        }
        
        static void showToast(Context context, @StringRes int resId) {
            if (context == null) {
                return;
            }
            showToast(context, context.getString(resId));
        }
        
        static void release() {
            oldMsg = null;
            toast = null;
        }
    }
    
}
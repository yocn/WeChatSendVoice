package com.yocn.af.util;

import android.os.SystemClock;
import android.util.Log;

/**
 * @Author yocn
 * @Date 2019/8/2 11:05 AM
 * @ClassName LogUtil
 */
public class LogUtil {
    private static final String TAG = LogUtil.class.getSimpleName();

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    private static long lastTS = 0;

    public static void logWithInterval(String msg) {
        if (SystemClock.elapsedRealtime() - lastTS > 1000) {
            lastTS = SystemClock.elapsedRealtime();
            Log.d(TAG, msg);
        }
    }

    public static void v(String msg) {
        Log.d(TAG, msg);
    }

    public static void v(String... msg) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : msg) {
            stringBuilder.append(s);
        }
        Log.d(TAG, stringBuilder.toString());
    }
}

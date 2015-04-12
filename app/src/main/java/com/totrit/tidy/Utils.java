package com.totrit.tidy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by totrit on 2015/1/31.
 */
public class Utils {
    private final static boolean LOG_ENABLED = true;

    public static void d(String tag, String msg) {
        if (LOG_ENABLED) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_ENABLED) {
            android.util.Log.e(tag, msg);
        }
    }
}

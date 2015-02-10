package com.totrit.tidy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by totrit on 2015/1/31.
 */
public class Utils {
    private final static String COMMON_SP = "common";
    private final static boolean LOG_ENABLED = true;
    private static Toast sToast;
    private static SharedPreferences sCommonSharedPreferences;

    public static void showTip(String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(ApplicationImpl.getGlobalContext(), "", Toast.LENGTH_SHORT);
        }
        sToast.setText(msg);
        sToast.show();
    }

    public static SharedPreferences getCommonSP() {
        if (sCommonSharedPreferences == null) {
            sCommonSharedPreferences = ApplicationImpl.getGlobalContext().getSharedPreferences(COMMON_SP, Context.MODE_PRIVATE);
        }
        return sCommonSharedPreferences;
    }

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

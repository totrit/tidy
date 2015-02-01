package com.totrit.tidy;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by totrit on 2015/1/31.
 */
public class ApplicationImpl extends Application {
    private static Context sGlobalContext;

    public static Context getGlobalContext() {
        return sGlobalContext;
    }

    @Override
    public void onCreate() {
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        super.onCreate();
        sGlobalContext = this;
    }
}

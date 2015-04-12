package com.totrit.tidy.core;

import com.totrit.tidy.Utils;
import com.totrit.tidy.ui.MainActivity;

/**
 * Created by maruilin on 15/4/12.
 */
public class Communicator {
    private final static String LOG_TAG = "Communicator";
    private MainActivity mMainActivity;
    private static Communicator sInstance = null;

    public static Communicator getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (Communicator.class) {
            if (sInstance == null) {
                sInstance = new Communicator();
            }
        }
        return sInstance;
    }

    public void registerMainActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void unregisterMainActivity() {
        mMainActivity = null;
    }

    public void notifyMainListItemClicked(Entity clickedEntity) {
        try {
            Utils.d(LOG_TAG, "notifyMainListItemClicked:" + clickedEntity);
            mMainActivity.onListItemClicked(clickedEntity);
        } catch (NullPointerException ignore) {
        }
    }

    public void postRunnableToUi(Runnable r) {
        try {
            mMainActivity.runOnUiThread(r);
        } catch (NullPointerException ignore) {
        }
    }
}

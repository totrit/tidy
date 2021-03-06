package com.totrit.tidy.core;

import android.content.Context;

import com.totrit.tidy.Utils;
import com.totrit.tidy.ui.MainActivity;

/**
 * Created by maruilin on 15/4/12.
 */
public class Communicator {
    private final static String LOG_TAG = "Communicator";
    private MainActivity mMainActivity;
    private static Communicator sInstance = null;
    private Context mGlobalContext;

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
        mGlobalContext = mainActivity.getApplicationContext();
    }

    public Context getContext() {
        return mGlobalContext;
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

    public void notifyListViewNeedRefresh(final long id) {
        postRunnableToUi(new Runnable() {
            @Override
            public void run() {
                mMainActivity.refreshCurrentFrag(id);
            }
        });
    }

    public void notifyTitleNeedRefresh(final long id) {
        postRunnableToUi(new Runnable() {
            @Override
            public void run() {
                mMainActivity.updateTitle(id);
            }
        });

    }

    public void postRunnableToUi(Runnable r) {
        try {
            mMainActivity.runOnUiThread(r);
        } catch (NullPointerException ignore) {
        }
    }
}

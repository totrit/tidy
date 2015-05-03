package com.totrit.tidy.core;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by maruilin on 15/5/3.
 */
public class WorkingThread extends HandlerThread {
    private static WorkingThread sInstance = null;
    private volatile boolean inited = false;
    private Handler mHandler = null;

    public WorkingThread(String name) {
        super(name);
    }

    public static WorkingThread getInstance() {
        if (sInstance != null && sInstance.inited) {
            return sInstance;
        }
        synchronized (WorkingThread.class) {
            if (sInstance != null && sInstance.inited) {
                return sInstance;
            } else if (sInstance == null) {
                sInstance = new WorkingThread("worker");
                sInstance.start();
                sInstance.mHandler = new Handler(sInstance.getLooper());
                sInstance.inited = true;
            }
        }
        return sInstance;
    }

    public void post(Runnable r) {
        sInstance.mHandler.post(r);
    }
}

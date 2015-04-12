package com.totrit.tidy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by maruilin on 15/4/6.
 */
public class EntityManager {
    ExecutorService mExecutor = null;
    private static EntityManager sInstance = null;

    public static EntityManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            synchronized (EntityManager.class) {
                if (sInstance == null) {
                    sInstance = new EntityManager();
                }
            }
            return sInstance;
        }
    }

    private EntityManager() {
        mExecutor = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }


    public List<Entity> queryCandidates(String description) {
        //TODO
        return null;
    }

    public void place(Entity object, Entity container) {
        //TODO
    }

    Future mLastScheduledTask = null;
    public void asyncFetchContained(final int containerId, final IContainedObjectsFetchCallback uiCallback) {
        // cancel the previous task
        if (mLastScheduledTask != null) {
            mLastScheduledTask.cancel(true);
        }
        mLastScheduledTask = mExecutor.submit(new Callable<Void>() {
            public Void call() {
                final List<Entity> fetched = internalGetContained(containerId);
                Communicator.getInstance().postRunnableToUi(new Runnable() {
                    @Override
                    public void run() {
                        uiCallback.containedFetched(fetched);
                    }
                });
                return null;
            }
        });
    }

    public static interface IContainedObjectsFetchCallback {
        public void containedFetched(List<Entity> children);
    }

    private List<Entity> internalGetContained(int containerId) {
        // TODO
        ArrayList<Entity> tmpDataset = new ArrayList<Entity>(3);
        for (int i = containerId * 100 + 0; i < containerId * 100 + 10; i ++) {
            tmpDataset.add(new Entity(i, "测试" + i, null));
        }
        return tmpDataset;
//        return null;
    }



}

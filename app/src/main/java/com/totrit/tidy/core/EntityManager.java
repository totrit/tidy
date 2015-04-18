package com.totrit.tidy.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;

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
    private final static String LOG_TAG = "EntityManager";
    public final static long ROOT_ENTITY_ID = 0;
    ExecutorService mExecutor = null;
    static EntityManager sInstance = null;
    private long mEntityIdIncretor = -1; //TODO atomic?
    private boolean INITIALIZED = false;
    private List<Entity> mEntitiesToSave = new ArrayList<>();
    private HardworkHandler mWorkerHandler;

    static {
        EntityManager.getInstance();
    }

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
        HandlerThread workThread = new HandlerThread("db-worker");
        workThread.start();
        mWorkerHandler = new HardworkHandler(workThread.getLooper());
        mExecutor = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, new LinkedBlockingQueue());
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                EntityManager.this.initIfNecessary();
            }
        });
    }

    public void place(Entity object, Entity container) {
        if (!INITIALIZED) {
            return; //TODO
        }
        object.setContainer(container.getEntityId());
    }

    Future mLastScheduledTask = null;
    public void asyncFetchContained(final long containerId, final IDataFetchCallback uiCallback) {
        // cancel the previous task
        if (mLastScheduledTask != null) {
            mLastScheduledTask.cancel(true);
        }
        mLastScheduledTask = mExecutor.submit(new Callable<Void>() {
            public Void call() {
                if (!INITIALIZED) {
                    Utils.sleep(50);
                    mLastScheduledTask = mExecutor.submit(this);
                    return null;
                }
                final List<Entity> fetched = internalGetContained(containerId);
                Communicator.getInstance().postRunnableToUi(new Runnable() {
                    @Override
                    public void run() {
                        uiCallback.dataFetched(fetched);
                    }
                });
                return null;
            }
        });
    }

    public void asyncSearch(final String descPart, final IDataFetchCallback uiCallback) {
        // cancel the previous task
        if (mLastScheduledTask != null) {
            mLastScheduledTask.cancel(true);
        }
        mLastScheduledTask = mExecutor.submit(new Callable<Void>() {
            public Void call() {
                if (!INITIALIZED) {
                    Utils.sleep(50);
                    mLastScheduledTask = mExecutor.submit(this);
                    return null;
                }
                final List<Entity> fetched = internalSearch(descPart);
                Utils.d(LOG_TAG, "query end, fetched list's size: " + (fetched != null? fetched.size(): 0));
                Communicator.getInstance().postRunnableToUi(new Runnable() {
                    @Override
                    public void run() {
                        uiCallback.dataFetched(fetched);
                    }
                });
                return null;
            }
        });
    }

    public void asyncQueryItemInfo(final long id, final IItemInfoQueryCallback uiCallback) {
        // cancel the previous task
        if (mLastScheduledTask != null) {
            mLastScheduledTask.cancel(true);
        }
        mLastScheduledTask = mExecutor.submit(new Callable<Void>() {
            public Void call() {
                if (!INITIALIZED) {
                    Utils.sleep(50);
                    mLastScheduledTask = mExecutor.submit(this);
                    return null;
                }
                final List<Entity> fetched = Entity.find(Entity.class, "ENTITYID = ?", Long.toString(id));
                Utils.d(LOG_TAG, "item info query end, fetched list's size: " + (fetched != null? fetched.size(): 0));
                if (fetched != null && fetched.size() == 1) {
                    Communicator.getInstance().postRunnableToUi(new Runnable() {
                        @Override
                        public void run() {
                            uiCallback.dataFetched(fetched.get(0));
                        }
                    });
                }
                return null;
            }
        });
    }

    public static interface IDataFetchCallback {
        public void dataFetched(List<Entity> children);
    }

    public static interface IItemInfoQueryCallback {
        public void dataFetched(Entity entity);
    }

    public void initIfNecessary() {
        List<Entity> entities = Entity.find(Entity.class, null, null, null, "entityid DESC", "1");
        if (entities == null || entities.size() == 0) {
            Utils.d(LOG_TAG, "initializing root...");
            mEntityIdIncretor = ROOT_ENTITY_ID;
            Entity rootEntity = new Entity("ROOT", null);
            rootEntity.setContainer(-1);
            rootEntity.save();
        } else {
            mEntityIdIncretor = entities.get(0).getEntityId() + 1;
        }
        INITIALIZED = true;
        Utils.d(LOG_TAG, "init done.");
    }

    long getAnId() {
        return mEntityIdIncretor ++;
    }

    private List<Entity> internalGetContained(long containerId) {
        return Entity.find(Entity.class, "container = ?", Long.toString(containerId));
    }

    private List<Entity> internalSearch(String descPart) {
        Utils.d(LOG_TAG, "searching " + descPart);
        //FIXME Use FTS3
//        return Entity.find(Entity.class, "description MATCH '?'", descPart);
        List<Entity> ret = Entity.find(Entity.class, "description LIKE '%" + descPart + "%'", null);
        Utils.d(LOG_TAG, "search done.");
        if (ret != null) {
            for (Entity entity: ret) {
                if (TextUtils.isEmpty(descPart)) {
                    entity.highlightedDescription = Html.fromHtml(entity.getDescription());
                } else {
                    String replaced = entity.getDescription().replaceAll(descPart, "<font color=\"" + Communicator.getInstance().getContext().getResources().getColor(R.color.text_highlight_color) + "\">" + descPart + "</font>");
                    entity.highlightedDescription = Html.fromHtml(replaced);
                }
                //TODO
                entity.thumb = Communicator.getInstance().getContext().getResources().getDrawable(R.drawable.ic_launcher);
                Utils.d(LOG_TAG, "entity after revised: " + entity);
            }
        }
        return ret;
        //TODO high light the items
    }

    void asyncSave(Entity entity) {
        synchronized (mEntitiesToSave) {
            mEntitiesToSave.add(entity);
        }
        mWorkerHandler.sendEmptyMessage(HardworkHandler.MSG_SAVE_ENTITY);
    }

    private class HardworkHandler extends Handler {
        final static int MSG_SAVE_ENTITY = 0;

        public HardworkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_ENTITY: {
                    while(true) {
                        Entity entity = null;
                        synchronized (EntityManager.this.mEntitiesToSave) {
                            if (mEntitiesToSave.size() != 0) {
                                entity = mEntitiesToSave.remove(0);
                            }
                        }
                        if (entity != null) {
                            entity.save();
                        } else {
                            break;
                        }
                    }
                    break;
                }
            }
        }

    }

}

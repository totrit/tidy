package com.totrit.tidy.core;

import android.os.Environment;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.totrit.tidy.ApplicationImpl;
import com.totrit.tidy.Utils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by totrit on 2015/2/1.
 */
class SubjectSimpleFactory extends SubjectAbstractFactory {
    private final static String LOG_TAG = "SubjectSimpleFactory";
    private final static String SAVE_FILE_NAME = "subjects.data";
    private Map<ISubject, ISubject> mAllSubjects;
    private PrivateHandler mHandler;

    public SubjectSimpleFactory() {
        HandlerThread newThread = new HandlerThread("data-saver");
        newThread.start();
        mHandler = new PrivateHandler(newThread.getLooper());
        mHandler.sendEmptyMessage(PrivateHandler.MSG_LOAD);
    }

    @Override
    public Pair<ISubject, ISubject> createObject(String statement) {
        final String SPLITTER = "放到";
        int splitdSpot = statement.indexOf(SPLITTER);
        if (splitdSpot != -1) {
            final String subject = statement.substring(0, splitdSpot);
            final String place = statement.substring(splitdSpot + SPLITTER.length());
            if (!TextUtils.isEmpty(subject) && !TextUtils.isEmpty(place)) {
                return new Pair<ISubject, ISubject>(new SimpleSubject(subject), new SimpleSubject(place));
            }
        }
        return null;
    }

    @Override
    public Collection<ISubject> similarSubjects(ISubject comparing, double subjectSimilarityThreshold) {
        if (mAllSubjects == null) {
            return null;
        }
        ISubject contained = mAllSubjects.get(comparing);
        Collection<ISubject> ret = null;
        if (contained != null) {
            ret = new ArrayList<ISubject>(1);
            ret.add(contained);
        }
        return ret;
    }

    @Override
    public void putSubject(ISubject subject, ISubject container) {
        subject.placedInto(container);
        if (mAllSubjects == null) {
            mAllSubjects = new HashMap<ISubject, ISubject>(10);
        }
        mAllSubjects.put(subject, subject);
        mAllSubjects.put(container, container);
    }

    private class PrivateHandler extends android.os.Handler {
        public final static int MSG_LOAD = 0;
        public final static int MSG_SAVE = 1;
        private ByteArrayOutputStream mIOBuffer = new ByteArrayOutputStream(32000); // 32KB

        public PrivateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD: {
                    loadData();
                    break;
                }
                case MSG_SAVE: {
                    saveData();
                    this.sendEmptyMessageDelayed(MSG_SAVE, 20000);
                    break;
                }
            }
        }

        private void saveData() {
            try {
                FileOutputStream fos = new FileOutputStream(ApplicationImpl.getGlobalContext().getFilesDir() + "/" + SAVE_FILE_NAME);
                mIOBuffer.reset();
                ObjectOutputStream oos = new ObjectOutputStream(mIOBuffer);
                oos.writeObject(mAllSubjects);
                mIOBuffer.writeTo(fos);
                oos.close();
                fos.close();
                Utils.d(LOG_TAG, "data saved, size=" + (mAllSubjects != null? mAllSubjects.size(): 0));
            } catch (Exception e) {
            }
        }

        private void loadData() {
            try {
                FileInputStream fos = new FileInputStream(ApplicationImpl.getGlobalContext().getFilesDir() + "/" + SAVE_FILE_NAME);
                ObjectInputStream oos = new ObjectInputStream(fos);
                mAllSubjects = (Map<ISubject, ISubject>) oos.readObject();
                oos.close();
                fos.close();
                Utils.d(LOG_TAG, "data loaded, size=" + (mAllSubjects != null? mAllSubjects.size(): 0));
            } catch (Exception e) {
            }
        }
    }
}

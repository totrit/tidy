package com.totrit.tidy.core;

import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.totrit.tidy.Utils;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by totrit on 2015/2/1.
 */
class SubjectSimpleFactory extends SubjectAbstractFactory {
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
        ISubject contained = mAllSubjects.get(comparing);
        Collection<ISubject> ret = null;
        if (contained != null) {
            ret = new ArrayList<ISubject>(1);
            ret.add(contained);
        }
        return ret;
    }

    @Override
    public void saveSubject(ISubject subject) {
        mAllSubjects.put(subject, subject);
    }

    private class PrivateHandler extends android.os.Handler {
        public final static int MSG_LOAD = 0;
        public final static int MSG_SAVE = 1;

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

        }

        private void loadData() {

        }
    }
}

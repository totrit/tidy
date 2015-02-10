package com.totrit.tidy.core;

import android.os.Parcel;

import com.iflytek.cloud.Setting;
import com.totrit.tidy.Utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by totrit on 2015/2/1.
 */
class SimpleSubject extends ISubject implements Serializable{
    private final static String LOG_TAG = "SimpleSubject";
    private String mName;
    private Set<String> mAttributes;
    private ISubject mFather;
    private Set<SimpleSubject> mInnerSubjects;

    public SimpleSubject(String statement) {
        mName = statement;
    }

    @Override
    public boolean placedInto(ISubject another) {
        if (!(another instanceof  SimpleSubject)) {
            return false;
        }
        SimpleSubject father = (SimpleSubject)another;
        if (father.mInnerSubjects == null) {
            father.mInnerSubjects = new HashSet<SimpleSubject>(10);
        }
        boolean ret = father.mInnerSubjects.add(this);
        if (mFather != null) {
            Utils.d(LOG_TAG, "placedInto, current-subject=" + this + ", previous-father=" + mFather + ", new-father=" + father);
            ((SimpleSubject)mFather).mInnerSubjects.remove(this);
        }
        mFather = father;
        Utils.d(LOG_TAG, "placedInto, son=" + this + ", father=" + another);
        return ret;
    }

    @Override
    public int childrenNum() {
        return mInnerSubjects != null? mInnerSubjects.size(): 0;
    }

    @Override
    public double similarity(ISubject anotherSubject) {
        //TODO
        if (mName == null) {
            return 0;
        }
        if (anotherSubject instanceof SimpleSubject) {
            return this.mName.equals(((SimpleSubject) anotherSubject).mName)? 1.0f: 0;
        } else {
            return 0;
        }
    }

    @Override
    public String readableDescription() {
        return mName;
    }

    @Override
    public boolean equals(Object another) {
        return similarity((ISubject) another) > 0.99f;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}

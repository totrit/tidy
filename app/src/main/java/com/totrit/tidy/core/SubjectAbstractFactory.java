package com.totrit.tidy.core;

import android.util.Pair;

import java.util.Collection;

/**
 * Created by totrit on 2015/2/1.
 */
abstract class SubjectAbstractFactory {
    public abstract Pair<ISubject, ISubject> createObject(String statement);
    public abstract Collection<ISubject> similarSubjects(ISubject comparing,
                                                         double subjectSimilarityThreshold);
    public abstract void saveSubject(ISubject subject);
}

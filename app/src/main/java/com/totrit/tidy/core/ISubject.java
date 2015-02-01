package com.totrit.tidy.core;

import java.io.Serializable;

/**
 * Created by totrit on 2015/2/1.
 */
public abstract class ISubject implements Serializable{
    public abstract boolean placedInto(ISubject another);
    public abstract int childrenNum();
    public abstract double similarity(ISubject anotherSubject);
    public abstract String readableDescription();
}

package com.totrit.tidy.core;

/**
 * Created by maruilin on 15/4/6.
 */
public class Entity {

    private String mDescription;
    private String mPicPath;

    public Entity(String description, String picPath) {
        mDescription = description;
        mPicPath = picPath;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPicPath() {
        return mPicPath;
    }
}

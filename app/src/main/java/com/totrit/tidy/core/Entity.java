package com.totrit.tidy.core;

import android.content.SharedPreferences;
import android.os.Build;

import com.totrit.tidy.Utils;

import java.util.List;

/**
 * Created by maruilin on 15/4/6.
 */
public class Entity {
    private int mId;
    private String mDescription;
    private String mPicPath;
    private static int sIdIncretor = -1;

    public Entity(String description, String picPath) {
        mId = generateNewId();
        mDescription = description;
        mPicPath = picPath;
    }

    public Entity(int id, String description, String picPath) {
        mId = id;
        mDescription = description;
        mPicPath = picPath;
    }

    @Override
    public String toString() {
        return "Entity{id=" + mId + ", desc=" + mDescription + ", picPath=" + mPicPath;
    }

    public int getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPicPath() {
        return mPicPath;
    }

    private int generateNewId() {
        if (sIdIncretor == -1) {
            sIdIncretor = Utils.getCommonSP().getInt("entity_id_increased_to", 0);
        }
        int ret = sIdIncretor;
        sIdIncretor ++;
        SharedPreferences.Editor editor = Utils.getCommonSP().edit().putInt("entity_id_increased_to", sIdIncretor);
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
        return ret;
    }
}

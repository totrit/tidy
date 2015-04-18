package com.totrit.tidy.core;

import android.graphics.drawable.Drawable;
import android.text.Spanned;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.totrit.tidy.Utils;

/**
 * Created by maruilin on 15/4/6.
 */
public class Entity extends SugarRecord<Entity> {
    private final static String LOG_TAG = "Entity";
    private long entity_id;
    private String description;
    private String pic_path;
    private long container;

    @Ignore
    Drawable thumb;
    @Ignore
    Spanned highlightedDescription;

    public Entity() {
    }

    public Entity(String description, String picPath) {
        this.entity_id = EntityManager.sInstance.getAnId();
        this.description = description;
        this.pic_path = picPath;
        Utils.d(LOG_TAG, "Entity.init, " + this);
    }

    public void setContainer(long containerId) {
        container = containerId;
        this.asyncSave();
    }

    public long getEntityId() {
        return entity_id;
    }

    public Drawable getThumb() {
        return thumb;
    }

    public long getContainerId() {
        return container;
    }

    public Spanned getHighlightedDescription() {
        return highlightedDescription;
    }

    @Deprecated
    public Long getId() {
        return null;
    }

    @Override
    public String toString() {
        return "Entity{id=" + entity_id + ", desc=" + description + ", picPath=" + pic_path;
    }

    private void asyncSave() {
        EntityManager.sInstance.asyncSave(this);
    }

    public String getDescription() {
        return description;
    }

    public String getPicPath() {
        return pic_path;
    }
}

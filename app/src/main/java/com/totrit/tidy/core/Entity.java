package com.totrit.tidy.core;

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
    private String image_name;
    private long container;
    public long time;

    @Ignore
    Spanned highlightedDescription;

    public Entity() {
    }

    public Entity(String description, String image) {
        this.entity_id = EntityManager.sInstance.getAnId();
        this.description = description;
        this.image_name = image;
        this.time = System.currentTimeMillis();
        Utils.d(LOG_TAG, "Entity.init, " + this);
    }

    public Entity(long id, String description, String picPath) {
        if (id == -1) {
            this.entity_id = EntityManager.sInstance.getAnId();
        } else {
            this.entity_id = id;
        }
        this.description = description;
        this.image_name = picPath;
        this.time = System.currentTimeMillis();
        Utils.d(LOG_TAG, "Entity.init, " + this);
    }

    public boolean updateProperties(Entity another) {
        boolean different = false;
        if (!this.description.equals(another.description)) {
            this.description = another.description;
            different = true;
        }
        if (this.image_name == null && another.image_name != null ||
                this.image_name != null && !this.image_name.equals(another.image_name)) {
            this.image_name = another.image_name;
            different = true;
        }
        if (different) {
            time = System.currentTimeMillis();
        }
        return different;
    }

    public void setContainer(long containerId) {
        container = containerId;
    }

    public long getEntityId() {
        return entity_id;
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
        return "Entity{id=" + entity_id + ", desc=" + description + ", imageName=" + image_name;
    }

    public void asyncSave() {
        EntityManager.sInstance.asyncSave(this);
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return image_name;
    }
}

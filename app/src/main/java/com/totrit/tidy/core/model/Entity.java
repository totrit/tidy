package com.totrit.tidy.core.model;

import com.orm.SugarRecord;

/**
 * Created by maruilin on 15/4/6.
 */
public class Entity extends SugarRecord<Entity> {
    public final static long ROOT_ENTITY_ID = 1;
    String description;
    String pic_path;
    long container;

    public Entity() {

    }

    public Entity(String description, String picPath) {
        this.description = description;
        pic_path = picPath;
    }

    public void setContainer(long containerId) {
        container = containerId;
    }

    @Override
    public String toString() {
        return "Entity{id=" + getId() + ", desc=" + description + ", picPath=" + pic_path;
    }

    public String getDescription() {
        return description;
    }

    public String getPicPath() {
        return pic_path;
    }
}

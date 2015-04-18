package com.totrit.tidy.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;
import com.totrit.tidy.ui.MainActivity;

import java.util.List;

/**
 * Created by maruilin on 15/4/18.
 */
public class BasicEntitiesDisplayTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public BasicEntitiesDisplayTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
        for (int i = 1; i < 6; i ++) {
            Entity entity1 = new Entity("" + i, "/sdcard/test.png");
            entity1.setContainer(0);
            for (int j = 1; j < 6; j ++) {
                Entity entity2 = new Entity(entity1.getDescription() + "->" + j, "/sdcard/test.png");
                entity2.setContainer(entity1.getEntityId());
            }
        }

        Utils.sleep(2000);
    }

    @MediumTest
    public void testEntities() {
        return;
    }
}

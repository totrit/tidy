package com.totrit.tidy.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.totrit.tidy.Utils;
import com.totrit.tidy.ui.AddItemDialog;

/**
 * Created by maruilin on 15/4/20.
 */

public class AddItemDialogTest extends ActivityInstrumentationTestCase2<AddItemDialog> {

    public AddItemDialogTest() {
        super(AddItemDialog.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @MediumTest
    public void testNone() {
        Utils.sleep(5000);
    }
}
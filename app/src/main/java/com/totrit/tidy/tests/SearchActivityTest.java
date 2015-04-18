package com.totrit.tidy.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.totrit.tidy.Utils;
import com.totrit.tidy.ui.SearchActivity;

/**
 * Created by maruilin on 15/4/18.
 */
public class SearchActivityTest extends ActivityInstrumentationTestCase2<SearchActivity> {

    public SearchActivityTest() {
        super(SearchActivity.class);
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

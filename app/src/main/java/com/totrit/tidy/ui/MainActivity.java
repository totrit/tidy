package com.totrit.tidy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.totrit.tidy.Constants;
import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Communicator;
import com.totrit.tidy.core.Entity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends android.support.v7.app.ActionBarActivity {
    private final static String LOG_TAG = "MainActivity";
    private List<MainView> mListViews = new ArrayList<>();

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int mCurrentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_pager);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        newFragment(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < mCurrentPageIndex) {
                    Utils.d(LOG_TAG, "page changed to " + position);
                    deleteUnnecessaryPages();
                }
                mCurrentPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Communicator.getInstance().registerMainActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Communicator.getInstance().unregisterMainActivity();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private void deleteUnnecessaryPages() {
        Utils.d(LOG_TAG, "deleting unnecessary page " + (mPager.getCurrentItem() + 1));
        mListViews.remove(mPager.getCurrentItem() + 1);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_new_entity) {
            Intent newDialog = new Intent();
            newDialog.setClass(this, NewItemDialog.class);
            this.startActivityForResult(newDialog, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 0){
            if (resultCode == 1) {
                String description = data.getStringExtra(Constants.BUNDLE_KEY_DESCRIPTION);
                String picPath = data.getStringExtra(Constants.BUNDLE_KEY_PIC_PATH);
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }
    }

    public void onListItemClicked(Entity entity) {
        newFragment(entity.getId());
    }

    private void newFragment(int id) {
        MainView newFrag = MainView.createInstance(id);
        if (mPager.getCurrentItem() < mListViews.size() - 1) {
            mListViews.set(mPager.getCurrentItem() + 1, newFrag);
        } else {
            mListViews.add(newFrag);
        }
        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
    }

}

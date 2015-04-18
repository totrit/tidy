package com.totrit.tidy.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Communicator;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends android.support.v7.app.ActionBarActivity {
    private final static String LOG_TAG = "MainActivity";
    private List<MainView> mListViews = new ArrayList<>();

    private int mCurrentDepth = -1;
    private SearchActivity.SearchResult mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Communicator.getInstance().registerMainActivity(this);
        newFragment(0, -1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSearchResult != null && mSearchResult.selectedId != -1 && mSearchResult.containerId != -1) {
            newFragment(mSearchResult.containerId, mSearchResult.selectedId);
        }
        mSearchResult = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Communicator.getInstance().unregisterMainActivity();
    }

    @Override
    public void onBackPressed() {
        if (goBack()) {
            finish();
        }
    }

    /*
    return true if end of stack
     */
    private boolean goBack() {
        Utils.d(LOG_TAG, "deleting unnecessary page " + mCurrentDepth);
        if (mListViews.size() <= 1) {
            mListViews.clear();
            return true;
        }
        getSupportFragmentManager().popBackStack();
        mListViews.remove(mCurrentDepth);
        mCurrentDepth --;
        return false;
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
        } else if (id == R.id.action_search) {
            SearchActivity.startActivity(this, false, mSearchActivityCallback);
        }else if (id == R.id.action_new) {
            //TODO
        }

        return super.onOptionsItemSelected(item);
    }

    private SearchActivity.ISearchCallback mSearchActivityCallback = new SearchActivity.ISearchCallback() {
        @Override
        public void onEnd(SearchActivity.SearchResult result) {
            Utils.d(LOG_TAG, "result from SearchActivity: " + result);
            mSearchResult = result;
        }
    };

    private EntityManager.IItemInfoQueryCallback mItemInfoQueryCallback = new EntityManager.IItemInfoQueryCallback() {
        @Override
        public void dataFetched(Entity entity) {
            getSupportActionBar().setTitle(entity.getDescription());
        }
    };

    public void onListItemClicked(Entity entity) {
        newFragment(entity.getEntityId(), -1);
    }

    private void newFragment(long id, long highlight) {
        MainView newFragment = MainView.createInstance(id, highlight);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_push_in, R.anim.fragment_push_out, R.anim.fragment_pop_in, R.anim.fragment_pop_out);
        ft.replace(R.id.frag_area, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
        mListViews.add(newFragment);
        mCurrentDepth ++;
        EntityManager.getInstance().asyncQueryItemInfo(id, mItemInfoQueryCallback);
    }

}

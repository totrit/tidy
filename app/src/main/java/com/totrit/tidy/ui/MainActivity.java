package com.totrit.tidy.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Communicator.getInstance().registerMainActivity(this);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        newFragment(0, -1);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        title = (TextView) findViewById(R.id.toolbarTitle);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.d(LOG_TAG, "title clicked");
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(Utils.dp2px(getResources().getDimension(R.dimen.elev_action_bar), getResources()));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mListViews.get(mCurrentDepth).refresh();
        if (mSearchResult != null && mSearchResult.selectedEntity != null) {
            newFragment(mSearchResult.selectedEntity.getContainerId(), mSearchResult.selectedEntity.getEntityId());
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

    public void refreshCurrentFrag(long id) {
        if (mListViews.get(mCurrentDepth).id == id) {
            mListViews.get(mCurrentDepth).refresh();
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
        setTitleText(mListViews.get(mCurrentDepth).mTitle);
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
        if (id == R.id.action_search) {
            SearchActivity.startActivity(this, false, mSearchActivityCallback);
        }else if (id == R.id.action_new) {
            Intent newIntent = new Intent();
            newIntent.setClass(this, AddItemDialog.class);
            this.startActivity(newIntent);
        } else if (id == android.R.id.home || id == R.id.up || id == R.id.homeAsUp) {
            onBackPressed();
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
            mListViews.get(mCurrentDepth).mTitle = entity.getDescription();
            setTitleText(entity.getDescription());
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

    public void updateTitle(long id) {
        EntityManager.getInstance().asyncQueryItemInfo(id, mItemInfoQueryCallback);
    }

    private void setTitleText(final String titleTxt) {
        title.setText(titleTxt);
    }

    public void onBackClicked(View v) {
        onBackPressed();
    }

}

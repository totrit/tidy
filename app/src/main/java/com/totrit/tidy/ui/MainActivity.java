package com.totrit.tidy.ui;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Communicator;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


public class MainActivity extends android.support.v7.app.ActionBarActivity {
    private final static String LOG_TAG = "MainActivity";
    private List<MainView> mListViews = new ArrayList<>();

    private int mCurrentDepth = -1;
    private SearchActivity.SearchResult mSearchResult;
    private Spinner title;
    private DropDownContainerChainProcessor titleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Communicator.getInstance().registerMainActivity(this);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        title = (Spinner) findViewById(R.id.toolbarTitle);
        titleController = new DropDownContainerChainProcessor(this, R.layout.container_chain_item, R.id.container_name, new ArrayList<String>());
        titleController.setDropDownViewResource(R.layout.container_chain_item);
        title.setOnItemSelectedListener(titleController);
        title.setAdapter(titleController);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(Utils.dp2px(getResources().getDimension(R.dimen.elev_action_bar), getResources()));
        newFragment(0, -1);
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
        updateTitle(mListViews.get(mCurrentDepth).id);
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
        updateTitle(id);
    }

    public void updateTitle(long id) {
        titleController.updateChain(id);
    }

    public void onBackClicked(View v) {
        onBackPressed();
    }

    private class DropDownContainerChainProcessor extends ArrayAdapter implements AdapterView.OnItemSelectedListener {
        private List<Long> chain = new ArrayList<Long>(10);
        private List<String> chainName = new ArrayList<>(10);

        public DropDownContainerChainProcessor(Context context, int resource, int textViewId, List objects) {
            super(context, resource, textViewId, objects);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if (position != 0) {
                newFragment(chain.get(position), -1);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

        private void updateChain(long id) {
            chain.clear();
            chainName.clear();
            EntityManager.getInstance().getContainChain(id, new Subscriber<Entity>() {
                @Override
                public void onCompleted() {
                    clear();
                    for (String name: chainName) {
                        DropDownContainerChainProcessor.this.add(name);
                    }
                    title.setSelection(0);
                    DropDownContainerChainProcessor.this.notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Entity entity) {
                    chainName.add(entity.getDescription());
                    chain.add(entity.getEntityId());
                }
            });
        }

    }

}

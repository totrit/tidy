package com.totrit.tidy.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.totrit.tidy.Constants;
import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Communicator;
import com.totrit.tidy.core.model.Entity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends android.support.v7.app.ActionBarActivity {
    private final static String LOG_TAG = "MainActivity";
    private List<MainView> mListViews = new ArrayList<>();

    private int mCurrentDepth = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Communicator.getInstance().registerMainActivity(this);
        newFragment(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
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

    public void onListItemClicked(Entity entity) {
        newFragment(entity.getId());
    }

    private void newFragment(long id) {
        MainView newFragment = MainView.createInstance(id);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_push_in, R.anim.fragment_push_out, R.anim.fragment_pop_in, R.anim.fragment_pop_out);
        ft.replace(R.id.frag_area, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
        mListViews.add(newFragment);
        mCurrentDepth ++;
    }

}

package com.totrit.tidy.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;

import java.util.List;

/**
 * Created by maruilin on 15/4/6.
 */
public class MainView extends android.support.v4.app.Fragment {
    private final static String LOG_TAG = "MainView";
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgress;
    private MainListAdapter mAdapter;

    static MainView createInstance(long entityId, long highlight) {
        MainView newFrag = new MainView();

        Bundle args = new Bundle();
        args.putLong("id", entityId);
        args.putLong("highlight", highlight);
        newFrag.setArguments(args);

        return newFrag;
    }

    public MainView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (ViewGroup)inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.main_objects_lv);
        mProgress = (ProgressBar)mRootView.findViewById(R.id.progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setHasFixedSize(true);

        long id = this.getArguments().getLong("id", -1);
        final long highlight = this.getArguments().getLong("highlight", -1);
        Utils.d(LOG_TAG, "creating new Fragment for entity " + id);
        showProgress(true);
        EntityManager.getInstance().asyncFetchContained(id, new EntityManager.IDataFetchCallback() {
            @Override
            public void dataFetched(List<Entity> children) {
                mAdapter = new MainListAdapter();
                mAdapter.setHightlight(highlight);
                mAdapter.setData(children);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                showProgress(false);
            }
        });
        return mRootView;
    }

    private void showProgress(boolean toShow) {
        if (toShow) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }


}

package com.totrit.tidy.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maruilin on 15/4/6.
 */
public class MainView extends android.support.v4.app.Fragment {
    private final static String LOG_TAG = "MainView";
    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;

    static MainView createInstance(int entityId) {
        MainView newFrag = new MainView();

        Bundle args = new Bundle();
        args.putInt("id", entityId);
        newFrag.setArguments(args);

        return newFrag;
    }

    public MainView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.main_objects_lv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setHasFixedSize(true);

        int id = this.getArguments().getInt("id", -1);
        Utils.d(LOG_TAG, "creating new Fragment for entity " + id);
        EntityManager.getInstance().asyncFetchContained(id, new EntityManager.IContainedObjectsFetchCallback() {
            @Override
            public void containedFetched(List<Entity> children) {
                mAdapter = new MainListAdapter();

                mAdapter.setData(children);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }


}

package com.totrit.tidy.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totrit.tidy.R;
import com.totrit.tidy.core.Entity;

import java.util.ArrayList;

/**
 * Created by maruilin on 15/4/6.
 */
public class MainView extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;
    public MainView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.main_objects_lv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setHasFixedSize(true);

        //TODO set tmp data
        mAdapter = new MainListAdapter();
        ArrayList<Entity> tmpDataset = new ArrayList<Entity>(3);
        for (int i = 0; i < 100; i ++) {
            tmpDataset.add(new Entity("测试" + i, null));
        }
        mAdapter.setData(tmpDataset);

        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }


}

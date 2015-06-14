package com.totrit.tidy.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Communicator;
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
    private MainListAdapter mAdapter = new MainListAdapter();
    public String mTitle = null;
    public long id = -1;

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
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        id = this.getArguments().getLong("id", -1);
        final long highlight = this.getArguments().getLong("highlight", -1);
        Utils.d(LOG_TAG, "creating new Fragment for entity " + id);
        showProgress(true);
        EntityManager.getInstance().asyncFetchContained(id, new EntityManager.IDataFetchCallback() {
            @Override
            public void dataFetched(List<Entity> children) {
                mAdapter.setHightlight(highlight);
                MainView.this.getArguments().putLong("highlight", -1);
                mAdapter.setData(children);
                mAdapter.notifyDataSetChanged();
                showProgress(false);
            }
        });
    }

    public void refresh() {
        loadData();
    }

    private void showProgress(boolean toShow) {
        if (toShow) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    private class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {
        private List<Entity> mDataSet;
        private long mHightlightId;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                v.findViewById(R.id.list_item_image).setOnClickListener(this);
                v.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v instanceof ImageView
                        && mDataSet.get(this.getPosition()).getImageName() != null) {
                    Utils.d(LOG_TAG, "viewing image " + mDataSet.get(this.getPosition()).getImageName());
                    Utils.viewImage(mDataSet.get(this.getPosition()).getImageName(), MainView.this.getActivity());
                } else {
                    Communicator.getInstance().notifyMainListItemClicked(mDataSet.get(this.getPosition()));
                }
            }

            @Override
            public boolean onLongClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.list_popup);
                popupMenu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_del: {
                                        Entity delEnt = mDataSet.remove(ViewHolder.this.getPosition());
                                        EntityManager.getInstance().asyncDel(delEnt);
                                        MainListAdapter.this.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                return true;
                            }
                        }
                );
                popupMenu.show();
                return true;
            }
        }

        public void setData(List<Entity> dataset) {
            mDataSet = dataset;
        }

        public void setHightlight(long id) {
            mHightlightId = id;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MainListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            ViewGroup wholeItem = (ViewGroup)(holder.itemView);
            TextView title = (TextView) wholeItem.findViewById(R.id.list_item_title);
            ((TextView)wholeItem.findViewById(R.id.list_item_time)).setText(Utils.milliesToDateStr(mDataSet.get(position).time));
            ImageView thumb = (ImageView) wholeItem.findViewById(R.id.list_item_image);
            title.setText(mDataSet.get(position).getDescription());
            if (mDataSet.get(position).getEntityId() == mHightlightId) {
                wholeItem.setPressed(true);
                setHightlight(-1);
            }
            Utils.asyncLoadImage(mDataSet.get(position).getImageName(), thumb);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if (mDataSet == null) {
                return 0;
            }
            return mDataSet.size();
        }

    }

}

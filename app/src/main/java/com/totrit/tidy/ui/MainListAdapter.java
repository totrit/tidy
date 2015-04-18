package com.totrit.tidy.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.totrit.tidy.R;
import com.totrit.tidy.core.Communicator;
import com.totrit.tidy.core.Entity;

import java.util.List;

/**
 * Created by maruilin on 15/4/6.
 */
class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {
    private List<Entity> mDataSet;
    private long mHightlightId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Communicator.getInstance().notifyMainListItemClicked(mDataSet.get(this.getPosition()));
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
        ImageView thumb = (ImageView) wholeItem.findViewById(R.id.list_item_image);
        title.setText(mDataSet.get(position).getDescription());
        if (mDataSet.get(position).getEntityId() == mHightlightId) {
            wholeItem.setPressed(true);
            setHightlight(-1);
        }
        //TODO
        thumb.setImageResource(R.drawable.ic_launcher);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
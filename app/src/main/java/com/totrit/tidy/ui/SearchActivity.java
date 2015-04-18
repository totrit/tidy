package com.totrit.tidy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.totrit.tidy.R;
import com.totrit.tidy.Utils;
import com.totrit.tidy.core.Entity;
import com.totrit.tidy.core.EntityManager;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends Activity {
    private final static String LOG_TAG = "SearchActivity";
    private RecyclerView mRecyclerView;
    private SearchListAdapter mAdapter;
    private EditText mEditText;
    private Button mButton;
    private boolean misForOnlySuggestion;
    private static ISearchCallback sSearchCallback;

    public static void startActivity(Activity context, boolean forSuggestion, ISearchCallback callback) {
        sSearchCallback = callback;
        Intent newIntent = new Intent();
        newIntent.putExtra("forSuggestion", forSuggestion);
        newIntent.setClass(context, SearchActivity.class);
        context.startActivity(newIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecyclerView = (RecyclerView)this.findViewById(R.id.searched_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mEditText = (EditText)this.findViewById(R.id.search_edit_text);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String textAfterChanged = charSequence.toString();
                Utils.d(LOG_TAG, "onTextChanged, text=" + textAfterChanged);
                EntityManager.getInstance().asyncSearch(textAfterChanged, new EntityManager.IDataFetchCallback() {
                    @Override
                    public void dataFetched(List<Entity> candidates) {
                        Utils.d(LOG_TAG, "displaying list: " + (candidates != null ? Arrays.toString(candidates.toArray()) : null));
                        if (mAdapter == null) {
                            mAdapter = new SearchListAdapter();
                        }

                        mAdapter.setData(candidates);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mButton = (Button)findViewById(R.id.search_done_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (misForOnlySuggestion) {
                    SearchResult ret = new SearchResult();
                    ret.typedText = mEditText.getText().toString().trim();
                    sSearchCallback.onEnd(ret);
                }
                SearchActivity.this.finish();
            }
        });
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        misForOnlySuggestion = intent.getBooleanExtra("forSuggestion", false);
        if (misForOnlySuggestion) {
            mButton.setText(R.string.done);
        } else {
            mButton.setText(R.string.close);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
        private List<Entity> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v instanceof ImageView) {
                    // TODO view the image
                } else {
                    SearchResult ret = new SearchResult();
                    ret.selectedId = mDataSet.get(this.getPosition()).getEntityId();
                    ret.containerId = mDataSet.get(this.getPosition()).getContainerId();
                    sSearchCallback.onEnd(ret);
                }
                SearchActivity.this.finish();
            }
        }

        void setData(List<Entity> dataset) {
            mDataSet = dataset;
        }

        @Override
        public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ViewGroup wholeItem = (ViewGroup)(holder.itemView);
            TextView title = (TextView) wholeItem.findViewById(R.id.list_item_title);
            ImageView thumb = (ImageView) wholeItem.findViewById(R.id.list_item_image);
            title.setText(mDataSet.get(position).getHighlightedDescription());
            thumb.setImageDrawable(mDataSet.get(position).getThumb());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

    public static interface ISearchCallback {
        public void onEnd(SearchResult result);
    }

    public static class SearchResult {
        long selectedId = -1;
        long containerId = -1;
        String typedText;

        @Override
        public String toString() {
            return "{selected: " + selectedId + ", contianer: " + containerId + ", typed: " + typedText + "}";
        }
    }
}

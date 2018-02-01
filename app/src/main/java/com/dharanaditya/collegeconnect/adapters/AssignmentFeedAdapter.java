/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dharanaditya on 30/01/18.
 */

public class AssignmentFeedAdapter extends FirestoreRecyclerAdapter<AssignmentFeed, AssignmentFeedAdapter.AssignmentViewHolder> {

    private static final String TAG = AssignmentFeedAdapter.class.getSimpleName();
    private OnListItemClickListener mOnListItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AssignmentFeedAdapter(@NonNull FirestoreRecyclerOptions<AssignmentFeed> options, OnListItemClickListener onListItemClickListener) {
        super(options);
        this.mOnListItemClickListener = onListItemClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position, @NonNull AssignmentFeed model) {
        holder.bind(model.getTimestamp(), model.getAuthor(), model.getTitle(), model.getMessage());
    }

    @Override
    public AssignmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new AssignmentViewHolder(v);
    }

    // FIXME: 30/01/18 Change Item Layout
    class AssignmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.US);
        @BindView(R.id.tv_item_date)
        TextView mDateTextView;
        @BindView(R.id.tv_item_author)
        TextView mAuthorTextView;
        @BindView(R.id.tv_item_message)
        TextView mMessageTextView;
        @BindView(R.id.tv_item_title)
        TextView mTitleTextView;

        AssignmentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(Date date, String author, String title, String message) {
            if (date != null) {
                mDateTextView.setText(FORMAT.format(date));
            }
            mAuthorTextView.setText(author);
            mMessageTextView.setText(message);
            mTitleTextView.setText(title);
        }

        @Override
        public void onClick(View view) {
            mOnListItemClickListener.onItemClick(getItem(getAdapterPosition()));
        }
    }
}

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
import com.dharanaditya.collegeconnect.models.NotificationFeed;
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

public class NotificationFeedAdapter extends FirestoreRecyclerAdapter<NotificationFeed, NotificationFeedAdapter.NotificationViewHolder> {

    private static final String TAG = NotificationFeedAdapter.class.getSimpleName();
    private OnListItemClickListener mOnListItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NotificationFeedAdapter(@NonNull FirestoreRecyclerOptions<NotificationFeed> options, OnListItemClickListener onListItemClickListener) {
        super(options);
        this.mOnListItemClickListener = onListItemClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationFeed model) {
//        Log.d(TAG, "onBindViewHolder: " + model.toString());
        holder.itemView.setTag(model.getDocumentId());
        holder.bind(model.getTimestamp(), model.getAuthor(), model.getTitle(), model.getMessage());
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new NotificationViewHolder(v);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.US);
        @BindView(R.id.tv_item_date)
        TextView mDateTextView;
        @BindView(R.id.tv_item_author)
        TextView mAuthorTextView;
        @BindView(R.id.tv_item_message)
        TextView mMessageTextView;
        @BindView(R.id.tv_item_title)
        TextView mTitleTextView;

        NotificationViewHolder(View itemView) {
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
            if (mOnListItemClickListener != null)
                mOnListItemClickListener.onItemClick(getItem(getAdapterPosition()));
        }
    }
}

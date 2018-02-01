/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.adapters.AssignmentFeedAdapter;
import com.dharanaditya.collegeconnect.adapters.ExamFeedAdapter;
import com.dharanaditya.collegeconnect.adapters.NotificationFeedAdapter;
import com.dharanaditya.collegeconnect.adapters.OnListItemClickListener;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedListFragment extends Fragment implements FirebaseAuth.AuthStateListener, OnListItemClickListener {
    private static final String TAG = FeedListFragment.class.getSimpleName();
    private static final String ARG_LIST_TYPE = "listType";
    private static final String ARG_QUERY_REF = "queryRef";
    @BindView(R.id.rcv_feed_list)
    RecyclerView mFeedRecyclerView;
    private String mListType;
    private String mQueryRef;
    private OnFeedListItemClickListener mListener;
    private FirestoreRecyclerAdapter mAdapter;

    private FirestoreRecyclerOptions options;
    private Query mQuery;

    public FeedListFragment() {
        // Required empty public constructor
    }

    public static FeedListFragment newInstance(String listType, String queryRef) {
        FeedListFragment fragment = new FeedListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_TYPE, listType);
        args.putString(ARG_QUERY_REF, queryRef);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedListItemClickListener) {
            mListener = (OnFeedListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFeedListItemClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListType = getArguments().getString(ARG_LIST_TYPE);
            mQueryRef = getArguments().getString(ARG_QUERY_REF);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        setupUi(mListType);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.stopListening();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (mAdapter != null) {
            if (firebaseAuth.getCurrentUser() != null) {
                mAdapter.startListening();
            } else {
                mAdapter.stopListening();
            }
        }
    }

    @Override
    public void onItemClick(Object obj) {
        mListener.onItemClick(obj);
    }

    private void setupUi(String listType) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        switch (listType) {
            case "Notification":
                buildNotificationFeedAdapter(firestore);
                break;
            case "Assignment":
                buildAssignmentFeedAdapter(firestore);

                break;
            case "Examination":
                buildExamFeedAdapter(firestore);
                break;
        }
        mFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedRecyclerView.setAdapter(mAdapter);
        mFeedRecyclerView.setHasFixedSize(false);
    }

    private void buildNotificationFeedAdapter(FirebaseFirestore firestore) {
        mQuery = firestore.collection("feed").document(mQueryRef).collection("general");
        options = new FirestoreRecyclerOptions.Builder<NotificationFeed>()
                .setQuery(mQuery, new SnapshotParser<NotificationFeed>() {
                    @NonNull
                    @Override
                    public NotificationFeed parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        NotificationFeed notificationFeed = snapshot.toObject(NotificationFeed.class);
                        notificationFeed.setDocumentId(snapshot.getId());
                        return notificationFeed;
                    }
                })
//                .setLifecycleOwner(this)
                .build();

        mAdapter = new NotificationFeedAdapter(options, this);
    }

    private void buildAssignmentFeedAdapter(FirebaseFirestore firestore) {
        // FIXME: 30/01/18 Get Year and Section Dynamically from user info
        mQuery = firestore.collection("feed").document(mQueryRef)
                .collection("year-" + 1)
                .whereEqualTo("branch", "ECE")
                .whereEqualTo("section", "A");
        options = new FirestoreRecyclerOptions.Builder<AssignmentFeed>()
                .setQuery(mQuery, new SnapshotParser<AssignmentFeed>() {
                    @NonNull
                    @Override
                    public AssignmentFeed parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        AssignmentFeed assignmentFeed = snapshot.toObject(AssignmentFeed.class);
                        assignmentFeed.setDocumentId(snapshot.getId());
                        return assignmentFeed;
                    }
                })
                .build();
        mAdapter = new AssignmentFeedAdapter(options, this);
    }

    private void buildExamFeedAdapter(FirebaseFirestore firestore) {
        // FIXME: 30/01/18 Get Year Dynamically from user info
        mQuery = firestore
                .collection("feed")
                .document(mQueryRef)
                .collection("year-" + 1);
        options = new FirestoreRecyclerOptions.Builder<ExamFeed>()
                .setQuery(mQuery, new SnapshotParser<ExamFeed>() {
                    @NonNull
                    @Override
                    public ExamFeed parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        ExamFeed examFeed = snapshot.toObject(ExamFeed.class);
                        examFeed.setDocumentId(snapshot.getId());
                        return examFeed;
                    }
                })
                .build();
        mAdapter = new ExamFeedAdapter(options, this);
    }

    public interface OnFeedListItemClickListener {
        void onItemClick(Object obj);
    }
}

/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTIFICATION = "com.dharanaditya.collegeconnect.EXTRA_NOTIFICATION";
    public static final String EXTRA_ASSIGNMENT = "com.dharanaditya.collegeconnect.EXTRA_ASSIGNMENT";
    public static final String EXTRA_EXAM = "com.dharanaditya.collegeconnect.EXTRA_EXAM";
    private static final String TAG = DetailActivity.class.getSimpleName();
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    @BindView(R.id.tv_detail_date)
    TextView mDateTextView;
    @BindView(R.id.tv_detail_discipline_info)
    TextView mInfoTextView;
    @BindView(R.id.tv_detail_title)
    TextView mTitleTextView;
    @BindView(R.id.tv_detail_message)
    TextView mMessageTextView;
    @BindView(R.id.tv_detail_author)
    TextView mAuthorTextView;
    @BindView(R.id.tv_detail_due_date)
    TextView mDueDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(EXTRA_NOTIFICATION)) {
                NotificationFeed notificationFeed = Parcels.unwrap(data.getParcelableExtra(EXTRA_NOTIFICATION));
                Log.d(TAG, "Received Parcel : " + notificationFeed.toString());
                updateUI(notificationFeed);
            } else if (data.hasExtra(EXTRA_ASSIGNMENT)) {
                AssignmentFeed assignmentFeed = Parcels.unwrap(data.getParcelableExtra(EXTRA_ASSIGNMENT));
                Log.d(TAG, "Received Parcel : " + assignmentFeed.toString());
                updateUI(assignmentFeed);
            } else if (data.hasExtra(EXTRA_EXAM)) {
                ExamFeed examFeed = Parcels.unwrap(data.getParcelableExtra(EXTRA_EXAM));
                Log.d(TAG, "Received Parcel : " + examFeed.toString());
                updateUI(examFeed);
            } else {
                throw new UnsupportedOperationException("Unsupported Operation");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void updateUI(ExamFeed examFeed) {

    }

    private void updateUI(AssignmentFeed assignmentFeed) {
        findViewById(R.id.placeholder_info).setVisibility(View.VISIBLE);
        if (assignmentFeed.getTimestamp() != null)
            mDateTextView.setText(FORMAT.format(assignmentFeed.getTimestamp()));
        mInfoTextView.setText(buildInfo(assignmentFeed.getYear(), assignmentFeed.getBranch(), assignmentFeed.getSection()));
        mTitleTextView.setText(assignmentFeed.getTitle());
        mMessageTextView.setText(assignmentFeed.getMessage());
        mAuthorTextView.setText(String.format("- %s", assignmentFeed.getAuthor()));
        mDueDateTextView.setText(String.format("Assignment due date : %s", FORMAT.format(assignmentFeed.getDueDate())));
    }

    private void updateUI(NotificationFeed notificationFeed) {
        if (notificationFeed.getTimestamp() != null)
            mDateTextView.setText(FORMAT.format(notificationFeed.getTimestamp()));
        mInfoTextView.setText(buildInfo("", "", ""));
        mTitleTextView.setText(notificationFeed.getTitle());
        mMessageTextView.setText(notificationFeed.getMessage());
        mAuthorTextView.setText(String.format("- %s", notificationFeed.getAuthor()));
    }

    @NonNull
    private String buildInfo(String year, String branch, String section) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(year)) {
            stringBuilder.append(getRomanYear(year));
        }
        if (!TextUtils.isEmpty(branch)) {
            stringBuilder.append(" ");
            stringBuilder.append(branch);
        }
        if (!TextUtils.isEmpty(section)) {
            stringBuilder.append(" ");
            stringBuilder.append(section);
        }
        return stringBuilder.toString();
    }

    private String getRomanYear(String year) {
        switch (year) {
            case "1":
                return "I";
            case "2":
                return "II";
            case "3":
                return "III";
            case "4":
                return "IV";
            default:
                return "";
        }
    }
}

/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;
import com.dharanaditya.collegeconnect.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnSuccessListener<DocumentReference>, OnFailureListener {
    private static final String TAG = AdminActivity.class.getSimpleName();
    @BindView(R.id.view_assignment_form)
    LinearLayout mAssignmentForm;

    @BindView(R.id.til_title)
    TextInputLayout mTitleInputLayout;
    @BindView(R.id.til_message)
    TextInputLayout mMessageInputLayout;
    @BindView(R.id.til_author)
    TextInputLayout mAuthorInputLayout;
    @BindView(R.id.til_subject)
    TextInputLayout mSubjectInputLayout;

    @BindView(R.id.et_title)
    EditText mTitleEditText;
    @BindView(R.id.et_message)
    EditText mMessageEditText;
    @BindView(R.id.et_author)
    EditText mAuthorEditText;
    @BindView(R.id.et_subject)
    EditText mSubjectEditText;

    @BindView(R.id.tv_due_date)
    TextView mDueDateTextView;

    @BindView(R.id.spinner_feed_selector)
    Spinner mFeedSpinner;
    @BindView(R.id.spinner_year_selector)
    Spinner mYearSpinner;
    @BindView(R.id.spinner_branch_selector)
    Spinner mBranchSpinner;
    @BindView(R.id.spinner_section_selector)
    Spinner mSectionSpinner;

    private String feedType;
    private String year;
    private String branch;
    private String section;
    private Date dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> feedSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.feed_type,
                android.R.layout.simple_spinner_dropdown_item);
        mFeedSpinner.setAdapter(feedSpinnerAdapter);
        mFeedSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.year,
                android.R.layout.simple_spinner_dropdown_item);
        mYearSpinner.setAdapter(yearSpinnerAdapter);
        mYearSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> branchSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.branch,
                android.R.layout.simple_spinner_dropdown_item);
        mBranchSpinner.setAdapter(branchSpinnerAdapter);
        mBranchSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> sectionSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.section,
                android.R.layout.simple_spinner_dropdown_item);
        mSectionSpinner.setAdapter(sectionSpinnerAdapter);
        mSectionSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_feed_selector:
                String feedType = (String) adapterView.getItemAtPosition(i);
                this.feedType = feedType;
                handleFormView(feedType);
                break;
            case R.id.spinner_year_selector:
                year = (String) adapterView.getItemAtPosition(i);
                Log.d(TAG, "onItemSelected: " + year);
                break;
            case R.id.spinner_branch_selector:
                branch = (String) adapterView.getItemAtPosition(i);
                Log.d(TAG, "onItemSelected: " + branch);
            case R.id.spinner_section_selector:
                section = (String) adapterView.getItemAtPosition(i);
                Log.d(TAG, "onItemSelected: " + section);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        showSnackBar("Failed to post Feed");
    }

    @Override
    public void onSuccess(DocumentReference documentReference) {
        showSnackBar("Feed Posted");
        invalidateViews();
    }


    private void invalidateViews() {
        mTitleEditText.setText("");
        mMessageEditText.setText("");
        mAuthorEditText.setText("");
        mSubjectEditText.setText("");
    }

    private void handleFormView(String feedType) {
        switch (feedType) {
            case Constants.TYPE_NOTIFICATION:
                mAssignmentForm.setVisibility(View.GONE);
                break;
            case Constants.TYPE_ASSIGNMENT:
                mSectionSpinner.setVisibility(View.VISIBLE);
                mAssignmentForm.setVisibility(View.VISIBLE);
                findViewById(R.id.view_date_info).setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_EXAM:
                mAssignmentForm.setVisibility(View.VISIBLE);
                mSectionSpinner.setVisibility(View.GONE);
                findViewById(R.id.view_date_info).setVisibility(View.GONE);
                break;
        }
    }

    private boolean validateTitle() {
        if (mTitleEditText.getText().toString().trim().isEmpty()) {
            mTitleInputLayout.setError("Title cannot be empty");
            requestFocus(mTitleEditText);
            return false;
        } else {
            mTitleInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMessage() {
        if (mMessageEditText.getText().toString().trim().isEmpty()) {
            mMessageInputLayout.setError("Message cannot be empty");
            requestFocus(mMessageEditText);
            return false;
        } else {
            mMessageInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAuthor() {
        if (mAuthorEditText.getText().toString().trim().isEmpty()) {
            mAuthorInputLayout.setError("Author cannot be empty");
            requestFocus(mAuthorEditText);
            return false;
        } else {
            mAuthorInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSubject() {
        if (mSubjectEditText.getText().toString().trim().isEmpty()) {
            mSubjectInputLayout.setError("Subject cannot be empty");
            requestFocus(mSubjectEditText);
            return false;
        } else {
            mSubjectInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDueDate() {
        if (dueDate == null) {
            showSnackBar("Please select a due date");
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @OnClick(R.id.btn_due_date)
    void selectDueDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                mDueDateTextView.setText(date);
                dueDate = calendar.getTime();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.fab_post)
    void postFeed() {
        if (!validateTitle()) {
            return;
        }
        if (!validateMessage()) {
            return;
        }
        if (!validateAuthor()) {
            return;
        }
        String title = mTitleEditText.getText().toString().trim();
        String message = mMessageEditText.getText().toString().trim();
        String author = mAuthorEditText.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getUid();

        switch (feedType) {
            case Constants.TYPE_NOTIFICATION:
                NotificationFeed notificationFeed = new NotificationFeed(title, message, author, uid);
                FirebaseFirestore.getInstance()
                        .collection(Constants.PATH_NOTIFICATION)
                        .add(notificationFeed)
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
                break;
            case Constants.TYPE_ASSIGNMENT:
                if (!validateDueDate()) return;
                if (!validateSubject()) return;

                String subject = mSubjectEditText.getText().toString().trim();
                AssignmentFeed assignmentFeed = new AssignmentFeed(title, message, author, uid,
                        branch, year, section, subject, dueDate);
                @SuppressLint("DefaultLocale") String assignmentPath = String.format(Constants.PATH_ASSIGNMENT, year);
                FirebaseFirestore.getInstance()
                        .collection(assignmentPath)
                        .add(assignmentFeed)
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
                break;
            case Constants.TYPE_EXAM:
                ExamFeed examFeed = new ExamFeed(title, message, author, uid, branch, year);
                @SuppressLint("DefaultLocale") String examPath = String.format(Constants.PATH_ASSIGNMENT, year);
                FirebaseFirestore.getInstance()
                        .collection(examPath)
                        .add(examFeed)
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
                break;
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

}

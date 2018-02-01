/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.adapters.FeedViewerAdapter;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;
import com.dharanaditya.collegeconnect.models.User;
import com.dharanaditya.collegeconnect.utils.FirestoreUtil;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,
        FeedListFragment.OnFeedListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    @BindView(R.id.vp_list_pager)
    ViewPager mViewPager;
    @BindView(R.id.view_error)
    RelativeLayout mErrorLayout;
    @BindView(R.id.tab_layout_main)
    TabLayout mTabLayout;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        FirebaseAuth.getInstance().addAuthStateListener(this);
        setupNavigationDrawer();
        setupViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_open_admin) {
            Log.d(TAG, "onOptionsItemSelected: Admin Console");
            return true;
        }
        return onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                break;
            case R.id.menu_stub_data:
                stubData();
                break;
            case R.id.menu_open_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response == null) {
                // User pressed back button
                finish();
                return;
            }
            updateUI(response.getErrorCode());
            if (resultCode == RESULT_OK) {
                // User Authenticated
                String uid = FirebaseAuth.getInstance().getUid();
                fetchUserDetails(uid);
            } else {
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    new AlertDialog.Builder(this)
                            .setTitle("No Internet Connectivity")
                            .setMessage("You need internet connectivity to login to the app. Please enable you'r internet connection and retry")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startSignIn();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar("Unknown Error");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, R.anim.slide_out_to_right);
    }

    @Override
    public void onItemClick(Object obj) {
        Intent showDetails = new Intent(this, DetailActivity.class);
        if (obj instanceof NotificationFeed) {
            NotificationFeed notificationFeed = (NotificationFeed) obj;
            showDetails.putExtra(DetailActivity.EXTRA_NOTIFICATION, Parcels.wrap(notificationFeed));
        } else if (obj instanceof ExamFeed) {
            ExamFeed examFeed = (ExamFeed) obj;
            showDetails.putExtra(DetailActivity.EXTRA_EXAM, Parcels.wrap(examFeed));
        } else if (obj instanceof AssignmentFeed) {
            AssignmentFeed assignmentFeed = (AssignmentFeed) obj;
            showDetails.putExtra(DetailActivity.EXTRA_ASSIGNMENT, Parcels.wrap(assignmentFeed));
        }
        startActivity(showDetails);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startSignIn();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    private void setupNavigationDrawer() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            ((TextView) headerView.findViewById(R.id.tv_nav_header_name)).setText(displayName);
        }
    }

    private void setupViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(FeedListFragment.newInstance("Notification", "notification"));
        fragments.add(FeedListFragment.newInstance("Assignment", "assignment"));
        fragments.add(FeedListFragment.newInstance("Examination", "examination"));
        FeedViewerAdapter feedViewerAdapter = new FeedViewerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(feedViewerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.75f;

            @Override
            public void transformPage(@NonNull View view, float position) {
                int pageWidth = view.getWidth();
                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);
                } else if (position <= 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);
                } else if (position <= 1) { // (0,1]
                    // Fade the page out.
                    view.setAlpha(1 - position);
                    // Counteract the default slide transition
                    view.setTranslationX(pageWidth * -position);
                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
    }

    private void startSignIn() {
        Intent startSignIn = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                        Collections.singletonList(
                                new AuthUI.IdpConfig.EmailBuilder()
                                        .setAllowNewAccounts(false)
                                        .setRequireName(false)
                                        .build()
                        )
                )
                .setIsSmartLockEnabled(false)
                .build();
        startActivityForResult(startSignIn, RC_SIGN_IN);
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUI(int errorCode) {
        switch (errorCode) {
            case ErrorCodes.NO_NETWORK:
                mViewPager.setVisibility(View.INVISIBLE);
                mErrorLayout.setVisibility(View.VISIBLE);
                return;
            case ErrorCodes.UNKNOWN_ERROR:
                // FIXME: 30/01/18
                return;
            default:
                mViewPager.setVisibility(View.VISIBLE);
                mErrorLayout.setVisibility(View.GONE);
        }
    }

    private void fetchUserDetails(String uid) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Fetching User Data", "Take a beathe", false);
        progressDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "onSuccess: Data Exits");
                            User user = documentSnapshot.toObject(User.class);
                            saveUserInfo(user);
                        } else {
                            Log.d(TAG, "onFailure: Data doesn't Exists");
                        }
                        progressDialog.cancel();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                        progressDialog.cancel();
                    }
                });
    }

    private void saveUserInfo(User user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.apply();
    }

    private void stubData() {
        FirestoreUtil.assignmentStubTest();
    }

}

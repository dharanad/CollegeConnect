/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.dharanaditya.collegeconnect.R;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by dharanaditya on 31/01/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null && preference instanceof CheckBoxPreference) {
            boolean isSubscribed = sharedPreferences.getBoolean(key, false);
            if (key.equals(getString(R.string.pref_notification_key))) {

            } else if (key.equals(getString(R.string.pref_assignment_key))) {
                key = String.format(key, 1, "ECE", "A");
            } else if (key.equals(getString(R.string.pref_exam_key))) {
                key = String.format(key, 1, "ECE");
            }
            if (isSubscribed) {
                Log.d(TAG, "onSharedPreferenceChanged: Subscribed " + key);
                FirebaseMessaging.getInstance().subscribeToTopic(key);
            } else {
                Log.d(TAG, "onSharedPreferenceChanged: Un subscribed " + key);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
            }
        }
    }
}

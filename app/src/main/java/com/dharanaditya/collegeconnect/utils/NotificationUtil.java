/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;

import java.util.Date;
import java.util.Map;

/**
 * Created by dharanaditya on 31/01/18.
 */

public class NotificationUtil {

    private static final int NOTIFICATION_ID = 0x001;
    private static final String TAG = NotificationUtil.class.getSimpleName();

    public static void showNotification(Context context, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.default_notification_channel_id);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, NotificationChannel.DEFAULT_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static NotificationFeed buildNotificationFeed(Map<String, String> data) {
        NotificationFeed notificationFeed = new NotificationFeed();
        notificationFeed.setTitle(data.get("title"));
        notificationFeed.setMessage(data.get("message"));
        notificationFeed.setAuthor(data.get("author"));
        notificationFeed.setTimestamp(new Date(data.get("timestamp")));
        Log.d(TAG, "Notification Feed Built : " + notificationFeed.toString());
        return notificationFeed;
    }

    public static AssignmentFeed buildAssignmentFeed(Map<String, String> data) {
        AssignmentFeed assignmentFeed = new AssignmentFeed();
        assignmentFeed.setTitle(data.get("title"));
        assignmentFeed.setMessage(data.get("message"));
        assignmentFeed.setAuthor(data.get("author"));
        assignmentFeed.setBranch(data.get("branch"));
        assignmentFeed.setSection(data.get("section"));
        assignmentFeed.setSubject(data.get("subject"));
        assignmentFeed.setYear(data.get("year"));
        assignmentFeed.setDueDate(new Date(data.get("dueDate")));
        assignmentFeed.setTimestamp(new Date(data.get("timestamp")));
        Log.d(TAG, "Assignment Feed Built : " + assignmentFeed.toString());
        return assignmentFeed;
    }

    public static ExamFeed buildExamFeed(Map<String, String> data) {
        ExamFeed examFeed = new ExamFeed();
        examFeed.setTitle(data.get("title"));
        examFeed.setMessage(data.get("message"));
        examFeed.setAuthor(data.get("author"));
        examFeed.setYear(data.get("year"));
        examFeed.setBranch(data.get("branch"));
        examFeed.setStartDate(new Date(data.get("startDate")));
        examFeed.setTimestamp(new Date(data.get("timestamp")));
        examFeed.setSubject(data.get("subject"));
        Log.d(TAG, "Exam Feed Built : " + examFeed.toString());
        return examFeed;
    }

}

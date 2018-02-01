/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.dharanaditya.collegeconnect.R;
import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.ExamFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;
import com.dharanaditya.collegeconnect.ui.DetailActivity;
import com.dharanaditya.collegeconnect.utils.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.parceler.Parcels;

import java.util.Map;

/**
 * Created by dharanaditya on 31/01/18.
 */

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = FCMService.class.getSimpleName();
    private static final int RC_OPEN_DETAILS = 1001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data payload : " + remoteMessage.getData());
            handleMessage(remoteMessage.getData());
        }
    }

    private void handleMessage(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("message");
        String channelId = getString(R.string.default_notification_channel_id);

        Intent resultIntent = new Intent(this, DetailActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        switch (data.get("type")) {
            case "notification":
                NotificationFeed notificationFeed = NotificationUtil.buildNotificationFeed(data);
                resultIntent.putExtra(DetailActivity.EXTRA_NOTIFICATION, Parcels.wrap(notificationFeed));
                break;
            case "assignment":
                AssignmentFeed assignmentFeed = NotificationUtil.buildAssignmentFeed(data);
                resultIntent.putExtra(DetailActivity.EXTRA_ASSIGNMENT, Parcels.wrap(assignmentFeed));
                break;
            case "exam":
                ExamFeed examFeed = NotificationUtil.buildExamFeed(data);
                resultIntent.putExtra(DetailActivity.EXTRA_EXAM, Parcels.wrap(examFeed));
                break;
        }

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(DetailActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(RC_OPEN_DETAILS, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_stat_ic_notification);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setContentIntent(pendingIntent);
        builder.setSound(defaultRingtone);
        builder.setAutoCancel(true);

        NotificationUtil.showNotification(this, builder.build());
    }
}


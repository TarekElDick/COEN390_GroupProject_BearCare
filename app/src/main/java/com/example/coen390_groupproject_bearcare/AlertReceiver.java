package com.example.coen390_groupproject_bearcare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    private String title, description;




    @Override
    public void onReceive(Context context, Intent intent) {
        // Show our notification.
        NotificationHelper notificationHelper = new NotificationHelper(context);

        title = intent.getStringExtra("notificationTitle");
        description = intent.getStringExtra("notificationDescription");
        NotificationCompat.Builder nb = notificationHelper.getChannel2Notification(title, description);
        notificationHelper.getManager().notify(2, nb.build());

    }
}
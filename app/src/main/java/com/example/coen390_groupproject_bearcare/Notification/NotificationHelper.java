package com.example.coen390_groupproject_bearcare.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.coen390_groupproject_bearcare.MainActivity;
import com.example.coen390_groupproject_bearcare.R;

// https://www.youtube.com/watch?v=ub4_f6ksxL0
public class NotificationHelper extends ContextWrapper {

    public static final String channel1ID = "channel1ID";
    public static final String channel1Name = "Warnings";
    public static final String channel2ID = "channel2ID";
    public static final String channel2Name = "Reminders";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel channel1 = new NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
        // add some settings
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // create it
        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(channel2ID, channel2Name, NotificationManager.IMPORTANCE_HIGH);
        // add some settings
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.canShowBadge();
        channel2.setLightColor(R.color.colorPrimary);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // create it
        getManager().createNotificationChannel(channel2);

    }

    // We need a notification manager to build this channel
    public NotificationManager getManager(){
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannel1Notification(String childName, String title, String description){
        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
                .setSmallIcon(R.drawable.bearcarelogonobackground)
                .setContentTitle(childName)
                .setContentText(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description));
    }

    public NotificationCompat.Builder getChannel2Notification(String childName, String title, String description){

        // Activity intent onNotification click
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Pending intent which is a wrapper around our intent
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,activityIntent, 0);

        return new NotificationCompat.Builder(getApplicationContext(), channel2ID)
                .setContentTitle(childName)
                .setContentText(title)
                .setSmallIcon(R.drawable.bearcarelogonobackground)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setContentIntent(contentIntent);
    }
}

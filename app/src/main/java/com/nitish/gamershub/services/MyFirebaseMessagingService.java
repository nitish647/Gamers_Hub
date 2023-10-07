package com.nitish.gamershub.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.FcmNotification;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.NotificationHelper;

import org.json.JSONObject;

import io.paperdb.Paper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public String TAG ="FirebaseNotification";
    Context context = this;

    MyFirebaseMessagingService() {
        Paper.init(context);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            FcmNotification fcmNotification = new Gson().fromJson(jsonObject.toString(),FcmNotification.class);

          Intent intent =  NotificationHelper.createNotificationIntent(fcmNotification.getType(),context);
            createNotification(fcmNotification.getTitle(),fcmNotification.getMessage(),intent);


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    public void createNotification(String title, String message, Intent resultIntent) {

        Log.d("pResponse","resultIntent "+resultIntent.getComponent().getClassName());

        String CHANNEL_ID = context.getPackageName();// The id of the channel.
        NotificationManager mNotificationManager;
        Notification.Builder mBuilder;
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                //*//* Request code *//*,
                PendingIntent.FLAG_IMMUTABLE
        );

        mBuilder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder
                    //.setSmallIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.gamers_hub_icon16)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            //.setLargeIcon(R.drawable.app_logo);

        }

        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationManager.notify(0, mBuilder.build());
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        AppHelper.saveFireBaseFcmToken(token);
    }
}

package com.mobilewallet.consumer.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.activities.MainActivity;
import com.mobilewallet.consumer.utils.Constants;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = "Mobile Wallet";
        String body = "";
        String type = "GENERAL";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if (remoteMessage.getData().size() > 0) {
            type = remoteMessage.getData().get("type");
            if (title == null) title = remoteMessage.getData().get("title");
            if (body == null || body.isEmpty()) body = remoteMessage.getData().get("message");
        }

        sendNotification(title, body, type);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // TODO: Send token to backend
    }

    private void sendNotification(String title, String message, String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getChannelId(type);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }

    private String getChannelId(String type) {
        if (type == null) return Constants.CHANNEL_GENERAL;

        switch (type) {
            case "PAYMENT_RECEIVED":
            case "PAYMENT_SENT":
                return Constants.CHANNEL_PAYMENTS;
            case "SECURITY":
                return Constants.CHANNEL_SECURITY;
            default:
                return Constants.CHANNEL_GENERAL;
        }
    }
}
package com.harismehmood.i200902_i200485.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.UserModel;
import com.harismehmood.i200902_i200485.activities.userMainChat_activity;
import com.harismehmood.i200902_i200485.utilities.Constants;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {
    public  static  final Integer NOTIFICATION_CHANNEL_ID = 1000; ;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        UserModel user = new UserModel();
        user.userId = remoteMessage.getData().get(Constants.USER_ID);
        user.userName = remoteMessage.getData().get(Constants.USER_NAME);
        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";
        Intent intent = new Intent(this, userMainChat_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notificatinon_icon_small)

                .setContentTitle(user.userName)
                .setContentText(remoteMessage.getData().get(Constants.KEY_CHAT_ROOM_MESSAGE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(remoteMessage.getData().get(Constants.KEY_CHAT_ROOM_MESSAGE)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    CharSequence channelName = "Chat Message";
                    String channelDescription = "This notification is for chat message notification";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                    channel.setDescription(channelDescription);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build());

    }





}




package com.lgt.twowink.Extras;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lgt.twowink.Activities.MainActivity;
import com.lgt.twowink.Activities.VideoCallingActivity;
import com.lgt.twowink.R;

import static com.lgt.twowink.Extras.Commn.CHANNEL_DESC;
import static com.lgt.twowink.Extras.Commn.CHANNEL_NAME;
import static com.lgt.twowink.Extras.Commn.CHANNNEL_ID;
import static com.lgt.twowink.Extras.Commn.KEY_INTENT_HELP;
import static com.lgt.twowink.Extras.Commn.KEY_INTENT_MORE;
import static com.lgt.twowink.Extras.Commn.NOTIFICATION_ID;
import static com.lgt.twowink.Extras.Commn.NOTIFICATION_REPLY;
import static com.lgt.twowink.Extras.Commn.REQUEST_CODE_HELP;
import static com.lgt.twowink.Extras.Commn.REQUEST_CODE_MORE;

public class MyNotification extends FirebaseMessagingService {
    Uri notification;
    Ringtone r;
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    SessionManager sessionManager;
    Activity activity;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sessionManager = new SessionManager();
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        Log.e("onMessageReceived",remoteMessage.getData()+"");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            commonNotification(remoteMessage);
        }else{
            recieveNotification(remoteMessage);
        }
    }

    private void recieveNotification(RemoteMessage remoteMessage) {

            String user=remoteMessage.getData().get(Commn.USER_ID);
            String icon=remoteMessage.getData().get(Commn.Current_Chat_Users);

                Log.d("remote_msg",""+remoteMessage);
                RemoteMessage.Notification notification = remoteMessage.getNotification();

                //PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (defaultSound==null){
                    // alert is null, using backup
                    defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (defaultSound == null){
                        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    }
                }
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), defaultSound);
                ringtone.setStreamType(AudioManager.STREAM_ALARM);
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder .setSmallIcon(Integer.parseInt(icon));
                //builder.setLargeIcon(getCroppedBitmap(chatUserImage));
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setLights(Color.YELLOW, 500, 5000);
                builder.setContentTitle("Calling..");
                builder.setShowWhen(true);
                builder.setAutoCancel(false);
                builder.setSound(defaultSound);
                //builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void recievedOreoNotification(RemoteMessage remoteMessage){
        NotificationCompat.Builder builder;
        Intent intent = new Intent(this, VideoCallingActivity.class);
        intent.putExtra(Commn.USER_ID,remoteMessage.getData().get("customer_id"));
        intent.putExtra("mUser",remoteMessage.getData().get("current_user_id"));
        intent.putExtra("KEY_NOTI",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (mChannel == null) {
            mChannel = new NotificationChannel
                    ("0", remoteMessage.getData().get("body"), importance);
            mChannel.setDescription( remoteMessage.getData().get("body"));
            mChannel.enableVibration(true);
            notifManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(this, "0");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_alert);

        // RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
        builder.setContentTitle(remoteMessage.getData().get("body"))
                .setSmallIcon(R.drawable.chat) // required
                .setContentText(remoteMessage.getData().get("body"))  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource
                        (getResources(), R.drawable.chat))
                //.setBadgeIconType(R.drawable.chat)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri
                        (RingtoneManager.TYPE_NOTIFICATION));
        Notification notification = builder.build();
        notifManager.notify(0, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void commonNotification(RemoteMessage remoteMessage){
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, VideoCallingActivity.class);
            intent.putExtra(Commn.USER_ID,remoteMessage.getData().get("customer_id"));
            intent.putExtra("mUser",remoteMessage.getData().get("current_user_id"));
            intent.putExtra("KEY_NOTI",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
        }*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESC);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent morePendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_MORE,
                new Intent(this, VideoCallingActivity.class)
                        .putExtra(KEY_INTENT_MORE, REQUEST_CODE_MORE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Pending intent for a notification button help
        PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_HELP,
                new Intent(this, MainActivity.class)
                        .putExtra(KEY_INTENT_HELP, REQUEST_CODE_HELP),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        /*RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
                .setLabel("Please enter your name")
                .build();

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
                        "Reply Now...", helpPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();*/
        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Creating the notifiction builder object
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(remoteMessage.getData().get("caller_name"))
                //.setContentText(remoteMessage.getData().get("customer_id"))
                .setAutoCancel(false)
                .setContentIntent(helpPendingIntent)
                //.addAction(action)
                .addAction(android.R.drawable.ic_menu_compass, "Answer", morePendingIntent)
                .addAction(android.R.drawable.ic_menu_directions, "Decline", helpPendingIntent);


        //finally displaying the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

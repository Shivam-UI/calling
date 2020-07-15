package com.lgt.twowink.Extras;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.lgt.twowink.Activities.VideoCallingActivity;
import com.lgt.twowink.R;
import com.tapadoo.alerter.Alerter;

public class MyNotification extends FirebaseMessagingService {

    private static final int NOTIF_ID = 1234;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private Notification mNotification;

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
            //recievedOreoNotification(remoteMessage);
            setUpNotification();
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

    private void commonNotification(RemoteMessage remoteMessage){
        activity= (Activity) getApplicationContext();
        Alerter.create(activity)
                .setTitle(remoteMessage.getData().get("body"))
                .setText(remoteMessage.getData().get("body"))
                .addButton("Answer", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Answer", Toast.LENGTH_SHORT).show();
                    }
                })
                .addButton("Decline", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "Decline", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void setUpNotification(){

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // we need to build a basic notification first, then update it
        Intent intentNotif = new Intent(this, VideoCallingActivity.class);
        intentNotif.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intentNotif, PendingIntent.FLAG_UPDATE_CURRENT);

        // notification's layout
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_alert);
        // notification's icon
        mBuilder = new NotificationCompat.Builder(this);

        CharSequence ticker = getResources().getString(R.string.app_name);
        int apiVersion = Build.VERSION.SDK_INT;

        if (apiVersion < Build.VERSION_CODES.O) {
            mNotification = new Notification(R.drawable.icon, ticker, System.currentTimeMillis());
            mNotification.contentView = mRemoteViews;
            mNotification.contentIntent = pendIntent;

            mNotification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
            mNotification.defaults |= Notification.DEFAULT_LIGHTS;

            // starting service with notification in foreground mode
            startForeground(NOTIF_ID, mNotification);

        }else if (apiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            mBuilder.setSmallIcon(R.drawable.icon)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentIntent(pendIntent)
                    .setContent(mRemoteViews)
                    .setTicker(ticker);

            // starting service with notification in foreground mode
            startForeground(NOTIF_ID, mBuilder.build());
        }
    }
}

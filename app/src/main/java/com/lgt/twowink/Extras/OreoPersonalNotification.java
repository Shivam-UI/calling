package com.lgt.twowink.Extras;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.lgt.twowink.R;

import java.io.IOException;
import java.net.URL;

public class OreoPersonalNotification extends ContextWrapper {

    public static final String CHANNEL_ID="com.lgt.patarpatarchat";
    public static final String CHANNEL_NAME="patarChat";
    public NotificationManager notificationManager;
    public OreoPersonalNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager(){
        if (notificationManager==null){
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationCompat.Builder getOreoNotification(String title, String body) {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.icon);
        builder.setShowWhen(true);
        builder.setAutoCancel(false);
        return builder;
    }

    public Bitmap getDataImage(String urlMessage){
        Bitmap bitmap=null;
        try {
            URL url = new URL(urlMessage);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch(IOException e) {
            System.out.println(e+"");
        }
        return bitmap;
    }
}

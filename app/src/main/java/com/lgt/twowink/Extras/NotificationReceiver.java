package com.lgt.twowink.Extras;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.lgt.twowink.Extras.Commn.CHANNNEL_ID;
import static com.lgt.twowink.Extras.Commn.KEY_INTENT_HELP;
import static com.lgt.twowink.Extras.Commn.KEY_INTENT_MORE;
import static com.lgt.twowink.Extras.Commn.NOTIFICATION_ID;
import static com.lgt.twowink.Extras.Commn.NOTIFICATION_REPLY;
import static com.lgt.twowink.Extras.Commn.REQUEST_CODE_HELP;
import static com.lgt.twowink.Extras.Commn.REQUEST_CODE_MORE;

public class NotificationReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {
        //getting the remote input bundle from intent
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        //if there is some input
        if (remoteInput != null) {

            //getting the input value
            CharSequence name = remoteInput.getCharSequence(NOTIFICATION_REPLY);

            //updating the notification with the input value
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setContentTitle("Hey Thanks, " + name);
            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        //if help button is clicked
        if (intent.getIntExtra(KEY_INTENT_HELP, -1) == REQUEST_CODE_HELP) {
            Toast.makeText(context, "You Clicked Help", Toast.LENGTH_LONG).show();
        }

        //if more button is clicked
        if (intent.getIntExtra(KEY_INTENT_MORE, -1) == REQUEST_CODE_MORE) {
            Toast.makeText(context, "You Clicked More", Toast.LENGTH_LONG).show();
        }
    }
}

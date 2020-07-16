package com.lgt.twowink.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lgt.twowink.Extras.SessionManager;

public class IsUserOffline extends Service {
    private SessionManager sessionManager;
    private String user_id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Offline
        Log.d("isUser","Offline");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Offline
/*        sessionManager = new SessionManager();
        user_id = sessionManager.getUser(this).getUser_id();
        onTerminate(user_id);*/
        Log.d("isUser","Offline");
    }


}

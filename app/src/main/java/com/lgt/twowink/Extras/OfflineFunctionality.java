package com.lgt.twowink.Extras;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OfflineFunctionality extends Application {
    private SessionManager sessionManager;
    private String user_id;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sessionManager=new SessionManager();
        user_id=sessionManager.getUser(this).getUser_id();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseReference online_status_all_users = FirebaseDatabase.getInstance().getReference().child("online_statuses");
        online_status_all_users.child(user_id).onDisconnect().setValue("offline");
    }


}

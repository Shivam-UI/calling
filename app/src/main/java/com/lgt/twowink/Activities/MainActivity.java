package com.lgt.twowink.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Fragments.CallsFragment;
import com.lgt.twowink.Fragments.ChatFragment;
import com.lgt.twowink.Fragments.HomeFragment;
import com.lgt.twowink.Fragments.ProfileFragment;
import com.lgt.twowink.Interface.UpdateUserDetails;
import com.lgt.twowink.Model.Token;
import com.lgt.twowink.R;
import com.lgt.twowink.Service.UpdateLocation;

import java.util.ArrayList;
import java.util.List;

import static com.lgt.twowink.Extras.Commn.FRAGMENT_CALLS;
import static com.lgt.twowink.Extras.Commn.FRAGMENT_CHAT;
import static com.lgt.twowink.Extras.Commn.FRAGMENT_HOME;
import static com.lgt.twowink.Extras.Commn.FRAGMENT_PROFILE;
import static com.lgt.twowink.Extras.Commn.user_token;

public class MainActivity extends AppCompatActivity implements UpdateUserDetails {

    private BottomNavigationView bottom_nav;
    private FragmentManager fragmentManager;
    private MainActivity activity;
    private Context context;
    FirebaseDatabase database;
    private SessionManager sessionManager;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = activity = this;
        sessionManager=new SessionManager();
        user_id=sessionManager.getUser(activity).getUser_id();
        iniViews();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        isUserOnline(user_id);
    }

    private void updateToken(String token){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(Commn.Tokens);
        Token token1=new Token(token);
        databaseReference.child(user_id).setValue(token1);

    }
    private void iniViews() {

        bottom_nav = findViewById(R.id.bottom_nav);
        fragmentManager = getSupportFragmentManager();
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermissions();

                        }
                        replaceFragment(new HomeFragment(), FRAGMENT_HOME);
                        return true;
                    case R.id.nav_chat:
                        replaceFragment(new ChatFragment(), FRAGMENT_CHAT);
                        return true;
                    case R.id.nav_calls:
                        replaceFragment(new CallsFragment(), FRAGMENT_CALLS);
                        return true;
                    case R.id.nav_profile:
                        replaceFragment(new ProfileFragment(), FRAGMENT_PROFILE);
                        return true;
                }
                return false;
            }
        });


    }

    private void replaceFragment(Fragment fragment, String fragment_tag) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).addToBackStack(fragment_tag).commit();


    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onChangeDetail(String isChanged) {
        Log.e("adavd", isChanged);
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Commn.location_permission) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((activity),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), 100);
            return false;
        } else {

            replaceFragment(new HomeFragment(), FRAGMENT_HOME);
            startService(new Intent(MainActivity.this, UpdateLocation.class));
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                replaceFragment(new HomeFragment(), FRAGMENT_HOME);
                startService(new Intent(MainActivity.this, UpdateLocation.class));
            }
            return;
        }
    }

    public static void customNotification(){

    }

    public static void isUserOnline(String mid){
        DatabaseReference online_status_all_users = FirebaseDatabase.getInstance().getReference().child("online_statuses").child(mid);
        online_status_all_users.child("status").setValue("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onTerminate(user_id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onTerminate(user_id);
    }



    public void onTerminate(String mid) {
        DatabaseReference online_status_all_users = FirebaseDatabase.getInstance().getReference().child("online_statuses").child(mid);
        online_status_all_users.child("status").onDisconnect().setValue("offline");
    }
}
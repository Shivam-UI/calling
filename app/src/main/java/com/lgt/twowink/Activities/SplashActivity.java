package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.R;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SessionManager.isSavedUser(getApplicationContext()).equalsIgnoreCase("true")){

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finishAffinity();
                }else {
                    startActivity(new Intent(getApplicationContext(),LaunchActivity.class));
                    finishAffinity();
                }
            }
        },1500);
    }
}
package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.R;

import java.util.Objects;

import io.fabric.sdk.android.Fabric;

public class LaunchActivity extends AppCompatActivity {
    private Button bt_signup,bt_login;
    private Context context;
    private LaunchActivity activity;
    private ImageView iv_launch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_launch);

        Fabric.with(this,new Crashlytics()
                ,new CrashlyticsNdk());

        context=activity=this;
        iniViews();
    }

    private void iniViews() {
        bt_signup=findViewById(R.id.bt_signup);
        bt_login=findViewById(R.id.bt_login);
        iv_launch=findViewById(R.id.iv_launch);

        iv_launch.setColorFilter(0xff555555, PorterDuff.Mode.MULTIPLY);
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context,SignUpActivity.class));
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,LoginActivity.class));
            }
        });


    }
}
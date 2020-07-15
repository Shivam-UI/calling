package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.R;

public class UserProfile extends AppCompatActivity {

    private Button bt_send_msg;
    private ImageView iv_back,iv_user_image;

    private TextView tv_user_name,tv_user_gender;
    private Context context;
    private UserProfile activity;
    private String user_id,user_name,user_image,gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context=activity=this;
        iniViews();
        getStringKeys();

        setClicks();
    }

    private void setClicks() {
        bt_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra(Commn.USER_ID,user_id);
                intent.putExtra(Commn.user_name,user_name);
                intent.putExtra(Commn.user_image,user_image);
                startActivity(intent);
            }
        });
    }


    private void iniViews() {
        bt_send_msg=findViewById(R.id.bt_send_msg);
        iv_back=findViewById(R.id.iv_back);
        iv_user_image=findViewById(R.id.iv_user_image);
        tv_user_name=findViewById(R.id.tv_user_name);
        tv_user_gender=findViewById(R.id.tv_user_gender);



        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });
    }
    private void getStringKeys() {
        if (getIntent().hasExtra(Commn.USER_ID)){
            user_id=getIntent().getStringExtra(Commn.USER_ID);
            user_name=getIntent().getStringExtra(Commn.user_name);
            user_image=getIntent().getStringExtra(Commn.user_image);
            gender=getIntent().getStringExtra(Commn.gender);
            Glide.with(context).load(user_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user_image);

            tv_user_name.setText(user_name);
            tv_user_gender.setText(gender);
        }
    }
}
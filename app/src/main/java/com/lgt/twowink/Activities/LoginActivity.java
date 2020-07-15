package com.lgt.twowink.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.UserDetails;
import com.lgt.twowink.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView tv_go_to_signup;
    private Context context;
    private LoginActivity activity;
    private ImageView iv_Login;
    private   String user_name,password;
    private TextView tv_forgot_password;
    private TextInputEditText et_username,et_password;
    private boolean isfilled=false;

    private SessionManager sessionManager;
    //forgot password
    private BottomSheetDialog forgot_dialog_sheet;
    private String registered_phone;
    private TextView tv_reset_password;
    private TextInputEditText et_MobileNumber;
    private ImageView iv_close_forgot;
    private static final int RESOLVE_HINT = 3;
    private FrameLayout bottom_frame;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=activity=this;
        iniViews();
    }

    private void iniViews() {
        sessionManager=new SessionManager();
        tv_go_to_signup=findViewById(R.id.tv_go_to_signup);
        iv_Login=findViewById(R.id.iv_Login);
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        tv_forgot_password=findViewById(R.id.tv_forgot_password);

        tv_go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,SignUpActivity.class));

            }
        });
        iv_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();

            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    requestHint();
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                openForgotDialog();
            }
        });
    }
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

    private void openForgotDialog() {
        forgot_dialog_sheet=new BottomSheetDialog(activity,R.style.DialogStyle);
        forgot_dialog_sheet.setContentView(R.layout.forgot_password_layout);
        forgot_dialog_sheet.show();

        et_MobileNumber=forgot_dialog_sheet.findViewById(R.id.et_MobileNumber);
        tv_reset_password=forgot_dialog_sheet.findViewById(R.id.tv_reset_password);
        iv_close_forgot=forgot_dialog_sheet.findViewById(R.id.iv_close_forgot);
        bottom_frame=forgot_dialog_sheet.findViewById(R.id.bottom_frame);


        tv_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registered_phone=et_MobileNumber.getText().toString().trim();

                if (TextUtils.isEmpty(registered_phone)){
                    et_MobileNumber.setError("Enter Registered Number");
                    et_MobileNumber.requestFocus();
                }else {
                    callForgotPassword();
                }
            }
        });
        iv_close_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBottomSheet();

            }
        });

        forgot_dialog_sheet.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BottomSheetBehavior.from(bottom_frame).setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                },1000);

            }
        });

    }

    private void hideBottomSheet() {
        if (forgot_dialog_sheet!=null){
            if (forgot_dialog_sheet.isShowing()){
                forgot_dialog_sheet.dismiss();
            }
        }
    }

    private void callForgotPassword() {

            Commn.showDialog(activity);

            StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.forget_password_api, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Commn.hideDialog(activity);
                    hideBottomSheet();
                    Log.e("forget_password_api",response+"");
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String message=jsonObject.getString("message");

                        Commn.myToast(context,message);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Commn.hideDialog(activity);
                    Commn.myToast(context,error.getMessage()+"");
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>params=new HashMap<>();
                    params.put("mobile",registered_phone);
                    Log.e("forget_password_api",params+"");
                    return params;
                }
            };
            Commn.requestQueue(getApplicationContext(),stringRequest);


    }

    private void validateLogin() {
        user_name=et_username.getText().toString().trim();
        password=et_password.getText().toString().trim();
        isfilled=true;
        if (TextUtils.isEmpty(user_name)){
            et_username.setError("Enter user name");
            et_username.requestFocus();
            isfilled=false;
        }
        if (TextUtils.isEmpty(password)){
            et_password.setError("Enter password");
            et_password.requestFocus();
            isfilled=false;
        }
        if (isfilled){

            loginApi();
         //   startActivity(new Intent(context,MainActivity.class));

        }


    }

    private void loginApi() {
        Commn.showDialog(activity);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.login_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Commn.hideDialog(activity);
                Log.e("login_api",response+"");
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String message=jsonObject.getString("message");
                    String status=jsonObject.getString("status");

                    if (jsonObject.has("otp_status")){

                        String otp_status=jsonObject.getString("otp_status");
                        if (otp_status.equalsIgnoreCase("0")){
                            Intent intent=new Intent(context,OtpActivity.class);
                            intent.putExtra(Commn.NOT_VERIFIED,Commn.NOT_VERIFIED);
                            startActivity(intent);
                        }

                    }else {
                        if (status.equalsIgnoreCase("1")){
                            Commn.myToast(context,message);

                            JSONObject data=jsonObject.getJSONObject("data");
                            String user_id=data.getString("user_id");
                            String name=data.getString("name");
                            String user_name=data.getString("user_name");

                            String mobile=data.getString("mobile");
                            String call_coin=data.getString("call_coin");
                            String chat_coin=data.getString("chat_coin");
                            String refer_code=data.getString("refer_code");
                            String user_image=data.getString("user_image");
                            saveUserDetail(user_id,name,user_name,mobile,call_coin,chat_coin,refer_code,user_image);


                        }else {
                            Commn.myToast(context,message);
                        }
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.hideDialog(activity);
                Commn.myToast(context,error.getMessage()+"");
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("user_name",user_name);
                params.put("password",password);
                Log.e("login_api",params+"");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(),stringRequest);
    }

    private void saveUserDetail(String user_id, String name, String user_name, String mobile, String call_coin, String chat_coin, String refer_code,String user_image) {


        try {
            UserDetails userDetails=new UserDetails();
            userDetails.setUser_id(user_id);
            userDetails.setName(name);
            userDetails.setUser_name(user_name);

            userDetails.setMobile(mobile);
            userDetails.setCall_coin(call_coin);
            userDetails.setChat_coin(chat_coin);
            userDetails.setRefer_code(refer_code);
            userDetails.setUser_image(user_image);

            sessionManager.setUser(context,userDetails);
            SessionManager.saveUser(context,"true");
            startActivity(new Intent(context,MainActivity.class));
            finishAffinity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RESOLVE_HINT){
            if (resultCode==RESULT_OK){
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                Log.e("my_no",credential.getId().substring(3)+"");
                removeCountryCode(credential.getId());
            }
        }
    }

    private void removeCountryCode(String full_phone) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(full_phone, "CH");
            Log.e("my_no",swissNumberProto.getNationalNumber()+"");
            if (forgot_dialog_sheet!=null){
                if (forgot_dialog_sheet.isShowing()){
                    et_MobileNumber.setText(String.valueOf(swissNumberProto.getNationalNumber()));

                }
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
    }
}
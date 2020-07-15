package com.lgt.twowink.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    public static TextView tv_resend_code,tv_OtpTimer;
    private EditText et_one,et_two,et_three,et_four;
    String OTP;
    ImageView iv_back;
    private TextView tv_verify;
    private static final int SMS_CONSENT_REQUEST = 2;

    private boolean otp_verified=false;
    Context context;
    private boolean fillOTP=false;

    private CountDownTimer countDownTimer;
    private SessionManager sessionManager;
    OtpActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        context=activity=this;

        InializationViews();

    }


    private void InializationViews() {
        sessionManager=new SessionManager();
        tv_resend_code=findViewById(R.id.tv_resend_code);
        et_one=findViewById(R.id.et_one);
        et_two=findViewById(R.id.et_two);
        et_three=findViewById(R.id.et_three);
        et_four=findViewById(R.id.et_four);
        tv_verify=findViewById(R.id.tv_verify);
        tv_OtpTimer=findViewById(R.id.tv_OtpTimer);
        iv_back=findViewById(R.id.iv_back);


        tv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillOTP=false;
                String Otp1 = et_one.getText().toString();
                String Otp2 = et_two.getText().toString();
                String Otp3 = et_three.getText().toString();
                String Otp4 = et_four.getText().toString();

                if (Otp1.equals("")){
                    Commn.myToast(context,"Enter OTP");
                    fillOTP=true;
                }
                if (Otp2.equals("")){
                    Commn.myToast(context,"Enter OTP");
                    fillOTP=true;
                } if (Otp3.equals("")){
                    Commn.myToast(context,"Enter OTP");
                    fillOTP=true;
                } if (Otp4.equals("")){
                    Commn.myToast(context,"Enter OTP");
                    fillOTP=true;
                }
                if ( !fillOTP){
                    tv_verify.setEnabled(true);
                    callVerifyOtp();
                }


            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        startCountDown();

        onTextChange();


        tv_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callResendApi();

            }
        });

        if (getIntent().hasExtra(Commn.NOT_VERIFIED)){
            callResendApi();
        }

    }

    private void callVerifyOtp() {
        Commn.showDialog(activity);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.verify_otp_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Commn.hideDialog(activity);
                Log.e("otpverify_response",response+"");
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String status=jsonObject.getString("status");
                    String message=jsonObject.getString("message");
                    if (status.equals("1")){
                        Commn.myToast(context,message);
                       SessionManager.saveUser(context,"true");
                        Intent intent=new Intent(context,MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }else {
                        Commn.myToast(context,message);
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
                params.put("user_id",sessionManager.getUser(context).getUser_id());
                params.put("otp",GetOTP()+"");
                Log.e("otp_verify_params",params+"");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(),stringRequest);

    }

    private void callResendApi() {
        Commn.showDialog(activity);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.resendotp_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Commn.hideDialog(activity);
                Log.e("otpresend_response",response+"");
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String status=jsonObject.getString("status");
                    String message=jsonObject.getString("message");
                    if (status.equals("1")){

                        startCountDown();
                        Commn.myToast(context,message);

                    }else {
                        Commn.myToast(context,message);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("user_id",sessionManager.getUser(context).getUser_id());
                Log.e("otp_resend_params",params+"");
                return params;
            }
        };
        Commn.requestQueue(context,stringRequest);
    }

    private void startCountDown() {
        tv_OtpTimer.setVisibility(View.VISIBLE);
        tv_resend_code.setVisibility(View.GONE);
        countDownTimer =  new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                //tv_Timer.setText("Resend OTP in: " + millisUntilFinished / 1000);
                tv_OtpTimer.setText("Didn't receive the OTP? Request for a new one in "+ String.format("%d:%d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }
            public void onFinish() {
                tv_resend_code.setVisibility(View.VISIBLE);
                tv_OtpTimer.setVisibility(View.GONE);
            }

        }.start();
    }

    private void onTextChange() {
        et_one.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_one.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_two.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        et_two.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_two.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_three.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        et_three.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_three.getText().toString().length() == 1)     //size as per your requirement
                {
                    et_four.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });
        et_four.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_four.getText().toString().length() == 1)     //size as per your requirement
                {
                    OTP = GetOTP();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(et_four.getWindowToken(), 0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    public String GetOTP(){
        String GETOTP = "";
        String Otp1 = et_one.getText().toString();
        String Otp2 = et_two.getText().toString();
        String Otp3 = et_three.getText().toString();
        String Otp4 = et_four.getText().toString();

        if (Otp1.equals("")){
            Commn.myToast(context,"Enter OTP");
        }
        else if (Otp2.equals("")){
            Commn.myToast(context,"Enter OTP");
        }else if (Otp3.equals("")){
            Commn.myToast(context,"Enter OTP");
        }else if (Otp4.equals("")){
            Commn.myToast(context,"Enter OTP");
        }
        else {
            GETOTP = Otp1+Otp2+Otp3+Otp4;
        }

        return GETOTP;
    }
    protected void onResume() {

        Task<Void> task = SmsRetriever.getClient(getApplicationContext()).startSmsUserConsent(null /* or null */);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("succcessss","done");

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failerrr",e.toString());
            }
        });

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);

        super.onResume();

    }
    private BroadcastReceiver smsVerificationReceiver= new  BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(smsVerificationReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==SMS_CONSENT_REQUEST){
            if (resultCode == RESULT_OK) {
                // Get SMS message content
                assert data != null;
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                // Extract one-time code from the message and complete verification
                // `sms` contains the entire text of the SMS message, so you will need
                // to parse the string.
                String oneTimeCode = parseOneTimeCode(message); // define this function

                assert oneTimeCode != null;
                String fullMsg=oneTimeCode.replaceAll("[^0-9]","");

                char o1 = fullMsg.charAt(0);
                char o2 = fullMsg.charAt(1);
                char o3 = fullMsg.charAt(2);
                char o4 = fullMsg.charAt(3);


                et_one.setText(o1+"");
                et_two.setText(o2+"");
                et_three.setText(o3+"");
                et_four.setText(o4+"");

                callVerifyOtp();

                Log.e("codeeeee",oneTimeCode.replaceAll("[^0-9]",""));

                //   ToastMessage.makeText(getActivity(),oneTimeCode,ToastMessage.LENGTH_SHORT).show();
                // send one time code to the server
            } else {
                // Consent canceled, handle the error ...
            }
        }

        // Obtain the phone number from the result


    }
    private String parseOneTimeCode(String message) {
        return message;
    }


}
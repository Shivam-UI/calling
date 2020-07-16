package com.lgt.twowink.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.UserDetails;
import com.lgt.twowink.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity {
    private TextView tv_go_to_login;
    private Context context;
    private SignUpActivity activity;
    private ImageView iv_signup;
    public  static   double latitude,longitude;

    private static final int RESOLVE_HINT = 3;
    public static String phone_number;
    private   String full_name,user_name,password,confirm_password,refer_code;
    private TextInputEditText et_full_name,et_username,et_MobileNumber,et_password,et_confirm_password,et_refer_code;
    private boolean isfilled=false;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context=activity=this;
        iniViews();

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

    private void iniViews() {
        sessionManager=new SessionManager();

        tv_go_to_login=findViewById(R.id.tv_go_to_login);
        iv_signup=findViewById(R.id.iv_signup);

        et_full_name=findViewById(R.id.et_full_name);
        et_username=findViewById(R.id.et_username);
        et_MobileNumber=findViewById(R.id.et_MobileNumber);
        et_password=findViewById(R.id.et_password);
        et_confirm_password=findViewById(R.id.et_confirm_password);
        et_refer_code=findViewById(R.id.et_refer_code);

        tv_go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,LoginActivity.class));
                finish();
            }
        });
        iv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateSignup();

            }
        });

    }

    private void validateSignup() {
        full_name=et_full_name.getText().toString().trim();
        user_name=et_username.getText().toString().trim();
        phone_number=et_MobileNumber.getText().toString().trim();
        password=et_password.getText().toString().trim();
        confirm_password=et_confirm_password.getText().toString().trim();
        refer_code=et_refer_code.getText().toString().trim();

        isfilled=true;
        if (TextUtils.isEmpty(full_name)){
            et_full_name.setError("Enter your name");
            et_full_name.requestFocus();
            isfilled=false;
        }
        if (TextUtils.isEmpty(user_name)){
            et_username.setError("Enter user name");
            et_username.requestFocus();
            isfilled=false;
        }
        if (TextUtils.isEmpty(phone_number)){
            et_MobileNumber.setError("Enter mobile number");
            et_MobileNumber.requestFocus();
            isfilled=false;
        }
        if (TextUtils.isEmpty(password)){
            et_password.setError("Enter password");
            et_password.requestFocus();
            isfilled=false;
        }
        if (TextUtils.isEmpty(confirm_password)){
            et_confirm_password.setError("Enter confirm password");
            et_confirm_password.requestFocus();
            isfilled=false;
        }

        if (!confirm_password.equals(password)){
            et_confirm_password.setError("both password should be same");
            et_confirm_password.requestFocus();
            isfilled=false;
        }

        if (isfilled){
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermissions();
            }


        }


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
            getLATLNG();

        }
        return true;
    }

    private void getLATLNG() {
        final LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (network_enabled) {


            final Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.e("coordinates", longitude + ",lati" + latitude + "");
                callSignUpApi();
            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                getLATLNG();
            }
            return;
        }
    }
    private void callSignUpApi() {
        Commn.showDialog(activity);
        StringRequest request=new StringRequest(Request.Method.POST, MyApi.sign_up_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("sign_up_api",response.toString()+"");
                Commn.hideDialog(activity);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String message=jsonObject.getString("message");
                    String status=jsonObject.getString("status");

                    if (status.equals("1")){
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

                    } else if(status.equals("0")){
                        Commn.myToast(context,message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.myToast(context,error.getMessage()+"");
                Commn.hideDialog(activity);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("name",full_name);
                params.put("user_name",user_name);
                params.put("mobile",phone_number);
                params.put("password",password);
                params.put("used_refer",refer_code);
                params.put("latitude",String.valueOf(latitude));
                params.put("longitute",String.valueOf(longitude));
                Log.e("sign_up_api",params.toString()+"");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(),request);
    }

    private void saveUserDetail(String user_id, String name, String user_name, String mobile, String call_coin, String chat_coin
            , String refer_code,String user_image) {


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

            VerificationPhone();
        }catch (Exception e){
            e.printStackTrace();
        }





    }

    private void VerificationPhone() {
        startActivity(new Intent(context,OtpActivity.class));
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
            et_MobileNumber.setText(String.valueOf(swissNumberProto.getNationalNumber()));
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
    }
}
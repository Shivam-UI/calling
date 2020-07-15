package com.lgt.twowink.Extras;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Commn {
    public static String FRAGMENT_HOME="FRAGMENT_HOME";
    public static String FRAGMENT_CHAT="FRAGMENT_CHAT";
    public static String FRAGMENT_CALLS="FRAGMENT_CALLS";
    public static String FRAGMENT_PROFILE="FRAGMENT_PROFILE";
    public static String NOT_VERIFIED="NOT_VERIFIED";
    public static String play_store_url="https://play.google.com/store/apps/details?id=";
    public static String FIREBASE_URL = "https://fcm.googleapis.com/";

    public static String User_Messages="User_Messages";
    public static String user_name="user_name";
    public static String user_image="user_image";
    public static String gender="gender";
    public static String user_id="user_id";
    public static String Current_Chat_Users="Current_Chat_Users";
    public static int TEXT_TYPE=0;
    public static String USER_ID="USER_ID";
    public static String Tokens="Tokens";
    public static String user_token="user_token";


    public static   String[] storage_permission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static   String[] location_permission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static   String[] video_calling_permission = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_WIFI_STATE

    };

    public static  ProgressDialog progressDialog;
    public static void myToast(Context context, String msg){

        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

    }

    public static void showDialog(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }
    public static void hideDialog(Context context){
        if (progressDialog!=null){
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog=null;
            }

        }

    }

    public static void requestQueue(Context context, StringRequest stringRequest){

        RequestQueue requestQueue = SingletonRequestQueue.getInstance(context).getRequestQueue();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    public static String Time(long time){

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        String dateString = formatter.format(new Date(time));

        return dateString;

    }

    public static void updateUserLocation(Context applicationContext, final double latitude, final double longitude, final String user_id) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.update_user_location_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("user_id",user_id);
                params.put("latitude",String.valueOf(latitude));
                params.put("longitute",String.valueOf(longitude));
                return params;
            }
        };
        Commn.requestQueue(applicationContext,stringRequest);
    }
}

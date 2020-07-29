package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.instamojo.android.Instamojo;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Order;
import com.lgt.twowink.Adapter.PackagesListAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.PaymentGate;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Interface.PlaymentMethod;
import com.lgt.twowink.Model.PackagesModel;
import com.lgt.twowink.Model.PaymentModel;
import com.lgt.twowink.PG.ChecksumUtils;
import com.lgt.twowink.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.lgt.twowink.Extras.PaymentGate.getApiService;

public class PackagesListActivity extends AppCompatActivity implements PlaymentMethod, PaymentResultListener {

    // PAYMENT
    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();
    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;
    private boolean mCustomUIFlow = false;
    private static final String TAG = "Razorpay";
    private PackagesListAdapter adapter;
    private RecyclerView rv_package_list;
    private List<PackagesModel> package_list = new ArrayList<>();
    private ProgressBar prgressbar;
    private ImageView iv_back;
    private Context context;
    private PackagesListActivity activity;
    private SwipeRefreshLayout package_swipe_refresh;
    private Intent intent;
    SessionManager sessionManager;
    Checkout checkout;
    Calendar cal;
    String Calling_time,package_ID,package_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_list);
        context = activity = this;
        checkout = new Checkout();
        iniViews();
        Checkout.preload(getApplicationContext());
        sessionManager = new SessionManager();
        intent = getIntent();
        Log.d("from_", intent.getStringExtra("KEY_TYPE"));
        setOnSwipeRefresh();
    }

    private void iniViews() {
        rv_package_list = findViewById(R.id.rv_package_list);
        iv_back = findViewById(R.id.iv_back);
        package_swipe_refresh = findViewById(R.id.package_swipe_refresh);
        prgressbar = findViewById(R.id.prgressbar);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void setOnSwipeRefresh() {
        package_swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                package_swipe_refresh.setRefreshing(true);
                loadPackagesList();
            }
        });
        package_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPackagesList();
            }
        });
    }

    private void loadPackagesList() {

        package_list.clear();
        prgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApi.package_list_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                package_swipe_refresh.setRefreshing(false);
                prgressbar.setVisibility(View.GONE);
                Log.e("package_list_api", response + "");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonObject.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String tbl_package_id = jsonObject1.getString("tbl_package_id");
                                String package_name = jsonObject1.getString("name");
                                String price = jsonObject1.getString("price");
                                String coins = jsonObject1.getString("coins");
                                String color = jsonObject1.getString("color");
                                String image = jsonObject1.getString("image");

                                package_list.add(new PackagesModel(tbl_package_id, package_name, price, coins, color, image));
                            }
                            setPackageAdapter(package_list);
                        }
                    } else if (status.equalsIgnoreCase("0")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgressbar.setVisibility(View.GONE);
                Commn.myToast(context, error.getMessage() + "");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("type", "" + intent.getStringExtra("KEY_TYPE"));
                return param;
            }
        };
        Commn.requestQueue(getApplicationContext(), stringRequest);


    }

    private void setPackageAdapter(List<PackagesModel> package_list) {
        adapter = new PackagesListAdapter(activity, package_list, PackagesListActivity.this);
        rv_package_list.setHasFixedSize(true);
        rv_package_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        rv_package_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    /*private void intializePayment(final String token, PackagesModel packagesModel) {
        Log.d("init_payment", "payment_test");
        RequestQueue mRequestQue = Volley.newRequestQueue(context);
        try {
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("allow_repeated_payments", "false");
            notificationObj.put("amount", packagesModel.getPrice());
            notificationObj.put("buyer_name", sessionManager.getUser(this).getUser_name());
            notificationObj.put("buyer_phone", sessionManager.getUser(this).getMobile());
            //notificationObj.put("longurl", "https://www.instamojo.com/@twowink/"+orderId);
            notificationObj.put("purpose", packagesModel.getPackage_name());
            notificationObj.put("buyer_email", "test@gmai");


            //replace notification with data when went send data
            Log.d("data_pay", "" + notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MyApi.PAYMENT_URL,
                    notificationObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("MUR", response.toString());
                            try {
                                JSONObject jsonObject = response.getJSONObject("payment_request");
                                String Order_id = jsonObject.getString("id");

                            } catch (Exception d) {
                                Log.d("error", "" + d);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MUR", "onError: " + error.networkResponse);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("X-Api-Key", MyApi.X_Api_Key);
                    header.put("X-Auth-Token", MyApi.X_Auth_Token);
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    // position,user_id,package_name,price,mobile_number,user_name,user_email
    @Override
    public void makePayment(String item_position, String user_id, String pack_name, String pack_price, String mobile_number, String user_name, String user_email) {
        // getOrderId();
        Log.d("In_Payment", "status" + pack_price);
        // set payment
        int update_pack_price = 0;
        package_ID = "";package_Name= "";
        package_ID = item_position;
        package_Name = pack_name;
        // change price
        update_pack_price = Integer.parseInt(pack_price) * 100;
        checkout.setKeyID(Commn.PAYMENT_TOKEN);
        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.icon);
        /**
         * Reference to current activity
         */
        final Activity activity = this;
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", user_name);
            options.put("description", package_Name);
            options.put("currency", "INR");
            options.put("amount", update_pack_price); //pass amount in currency subunits
            options.put("prefill.email", user_email);
            options.put("prefill.contact",mobile_number);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String status) {
        Log.d("PaymentSuccess","Success " + status);
        String Status = "Successful";
        PaymentStatus(Status,package_ID,package_Name);
    }

    @Override
    public void onPaymentError(int i, String status) {
        Log.d("PaymentError",i+" Something Wrong : " + status);
        String Status = "Failed";
        PaymentStatus(Status,package_ID,package_Name);
    }

    private void PaymentStatus(final String status,final String pack_id,final String pack_name){
        cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        Calling_time = hour + ":" + minute + ":" + second;
        // {"message":"Registered Successfully.","status":"1","data":{"user_id":"34","package_type":"Message",
        // "package_coins":"2500","payment_status":"pay_FKF9vZzovmhtGr","transection_id":"123456789","getway_response":"Pink message"}}
        StringRequest mPaymentRequest = new StringRequest(Request.Method.POST, MyApi.pack_purchase_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PaymentResponse","Success " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject pay_info = jsonObject.getJSONObject("data");
                        String coin_have = pay_info.getString("package_coins");
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PackagesListActivity.this,SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitle("Congratulations");
                        sweetAlertDialog.setContentText("Pack Activate Successful\n Enjoy your "+package_Name + " and you have" +coin_have+" coins.");
                        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                startActivity(new Intent(PackagesListActivity.this,MainActivity.class));
                                finishAffinity();
                            }
                        });
                        sweetAlertDialog.show();
                    } else if (status.equalsIgnoreCase("0")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }


                }catch (JSONException ex){
                    Log.d("error_pay",""+ex.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PaymentError","Success " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("user_id",sessionManager.getUser(getApplicationContext()).getUser_id());
                param.put("package_id",pack_id);
                param.put("time",Calling_time);
                param.put("payment_status",status);
                param.put("getway_response",pack_name);
                param.put("transection_id","123456789");
                return param;
            }
        };
        Commn.requestQueue(this,mPaymentRequest);
    }
}
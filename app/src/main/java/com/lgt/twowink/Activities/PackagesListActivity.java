package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.lgt.twowink.Adapter.PackagesListAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.PaymentGate;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Interface.PlaymentMethod;
import com.lgt.twowink.Model.PackagesModel;
import com.lgt.twowink.Model.PaymentModel;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.lgt.twowink.Extras.PaymentGate.getApiService;

public class PackagesListActivity extends AppCompatActivity implements PlaymentMethod, Instamojo.InstamojoPaymentCallback {

    // PAYMENT
    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();
    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;
    private boolean mCustomUIFlow = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_list);
        context = activity = this;
        iniViews();
        sessionManager = new SessionManager();
        intent = getIntent();
        Log.d("from_", intent.getStringExtra("KEY_TYPE"));
      //  Instamojo.getInstance().initialize(this, Instamojo.Environment.PRODUCTION);
        setOnSwipeRefresh();
        //getAccessToken();
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

    String orderId = "";

    /*@Override
    public void makePayment(final int position) {
        PackagesModel packagesModel = package_list.get(position);
        //  Log.e("pay_req",position+" "+u_id+" "+Number+" "+price+" "+u_name+" "+u_email);
        // init payment
        Instamojo.getInstance().initialize(this, Instamojo.Environment.PRODUCTION);
        // server call
        *//**//*
     *//*Map<String,String> param = new HashMap<>();
        param.put("allow_repeated_payments","false");
        param.put("amount",price);
        param.put("buyer_name",u_name);
        param.put("purpose",pack_name);
        param.put("phone",Number);
        param.put("send_email","true");
        param.put("send_sms","true");
        param.put("email",u_email);
        Call<PaymentModel> call = PaymentGate.getApiService().makePayment(MyApi.X_Api_Key,MyApi.X_Auth_Token,param);
        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, retrofit2.Response<PaymentModel> response) {
                Log.d("responce",call.toString()+""+response.body());
            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
                Log.d("",call+""+t.getLocalizedMessage());
            }
        });*//*
        orderId = getRandomString(10);
        Instamojo.getInstance().initiatePayment(PackagesListActivity.this, orderId, PackagesListActivity.this);

        getAccessToken(packagesModel);
    }*/

    private static final String ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    ProgressDialog progressDialog;

    private void getAccessToken(final PackagesModel packagesModel) {
        RequestQueue mRequestQue = Volley.newRequestQueue(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            JSONObject object = new JSONObject();
            object.put("client_id", MyApi.CLIENT_ID);
            object.put("client_secret", MyApi.SECRET_KEY);
            object.put("grant_type", "client_credentials");
            StringRequest jsonRequest = new StringRequest(Request.Method.POST, "https://api.instamojo.com/oauth2/token/", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        Log.d("Response", response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        //intializePayment(jsonObject.getString("token_type")+" "+jsonObject.getString("access_token"),packagesModel);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.d("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> object = new HashMap<>();
                    object.put("client_id", MyApi.CLIENT_ID);
                    object.put("client_secret", MyApi.SECRET_KEY);
                    object.put("grant_type", "client_credentials");
                    return object;
                }
            };
            mRequestQue.add(jsonRequest);
        } catch (Exception e0) {
            e0.printStackTrace();
        }
    }

    String orderUrl = "https://api.instamojo.com";

    private void getOrderId() {
        try {
            JSONObject param = new JSONObject();
            param.put("amount", "25");
            param.put("purpose", "Buy");
            param.put("buyer_name", "Shivam");
            param.put("email ", "createdinam@gmail.com");
            param.put("phone", "9999870918");
            param.put("currency ", "INR");
            param.put("send_email ", "true");
            param.put("send_sms ", "true");
            //param.put("RedirectURL ", orderUrl + "/integrations/android/redirect/");
            JsonObjectRequest paymentRequest = new JsonObjectRequest(Request.Method.POST, MyApi.PAYMENT_URL, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("resp_pay", "" + response);
                    try {
                        JSONObject jsonObject = response.getJSONObject("payment_request");
                        Log.d("long_url", "" + jsonObject.getString("longurl"));
                        Log.d("long_url", "" + jsonObject.getString("id"));
                        Instamojo.getInstance().initialize(PackagesListActivity.this, Instamojo.Environment.TEST);
                        Instamojo.getInstance().initiatePayment(PackagesListActivity.this, jsonObject.getString("id"), PackagesListActivity.this);
                      //  Instamojo.getInstance().initiatePayment(PackagesListActivity.this, jsonObject.getString("longurl"), PackagesListActivity.this);
                        //Instamojo.getInstance().initiatePayment(PackagesListActivity.this, jsonObject.getString("id"), PackagesListActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error_pay", "" + error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("X-Api-Key", MyApi.X_Api_Key);
                    header.put("X-Auth-Token", MyApi.X_Auth_Token);
                    return header;
                }
            };
            // Commn.requestQueue(this, paymentRequest);
            Volley.newRequestQueue(this).add(paymentRequest);
        } catch (Exception e) {
            Log.d("error_pay", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void intializePayment(final String token, PackagesModel packagesModel) {
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
                                Instamojo.getInstance().initiatePayment(PackagesListActivity.this, Order_id, PackagesListActivity.this);
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
    }

    public void makePay() {

    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d("PAY_TAG", "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
    }

    @Override
    public void onPaymentCancelled() {
        Log.d("ERROR_TAG", "Payment cancelled");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d("Payment_Failure_TAG", "Initiate payment failed");
        Toast.makeText(context, "Initiating payment failed. Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                Log.d("Payment_Success", transactionID + orderID);
            } else {
                Log.d("Payment_Failure_TAG", "Oops!! Payment was cancelled");
            }
        }
    }

    @Override
    public void makePayment(int position, String u_id, String pack_name, String Number, String price, String u_name, String u_email) {
        getOrderId();
    }
}
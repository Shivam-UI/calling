package com.lgt.twowink.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lgt.twowink.Activities.EditProfile;
import com.lgt.twowink.Activities.LaunchActivity;
import com.lgt.twowink.Activities.MainActivity;
import com.lgt.twowink.Activities.PackagesListActivity;
import com.lgt.twowink.Adapter.HistroyAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Interface.UpdateUserDetails;
import com.lgt.twowink.Model.HistoryModel;
import com.lgt.twowink.Model.UserDetails;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    HistroyAdapter histroyAdapter;
    private Button bt_logout, tv_refer_code;
    private TextView tv_total_time, tv_name, tv_user_name, tv_call_coins, tv_chat_coins, tv_full_name, tv_user_name2, tv_edit_profile, tv_income_coin;
    private String UserId, mobile, ref_code;
    private CircleImageView iv_user_image;
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rv_payment_history_list;
    UserDetails userDetails;
    public static ProfileFragment instance;
    List<HistoryModel> mList ;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mList = new ArrayList<>();
        userDetails = new UserDetails();
        instance = this;
        iniViews(view);
        UserId = sessionManager.getUser(getContext()).getUser_id();
        mobile = sessionManager.getUser(getContext()).getMobile();
        ref_code = sessionManager.getUser(getContext()).getRefer_code();
        getUserData();
        shoPaymentHistory(UserId);
        setOnRefresh();
        return view;
    }

    public void getUserData() {

        StringRequest userRequest = new StringRequest(Request.Method.POST, MyApi.my_profile_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("data", response.toString());
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.drawable.profile);
                requestOptions.placeholder(R.drawable.profile);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject DataObject = jsonObject.getJSONObject("data");
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        // DataObject.getString("user_image");
                        tv_name.setText(DataObject.getString("name"));
                        tv_user_name.setText(DataObject.getString("user_name"));
                        tv_income_coin.setText(DataObject.getString("income"));
                        tv_total_time.setText("05:15:45 HRS");
                        tv_call_coins.setText(DataObject.getString("call_coin"));
                        tv_chat_coins.setText(DataObject.getString("chat_coin"));
                        userDetails.setUser_id(UserId);
                        userDetails.setName(DataObject.getString("name"));
                        userDetails.setUser_name(DataObject.getString("user_name"));
                        userDetails.setMobile(mobile);
                        userDetails.setCall_coin(DataObject.getString("call_coin"));
                        userDetails.setChat_coin(DataObject.getString("chat_coin"));
                        userDetails.setRefer_code(ref_code);
                        userDetails.setUser_image(DataObject.getString("user_image"));
                        tv_full_name.setText(DataObject.getString("name"));
                        tv_user_name2.setText(DataObject.getString("user_name"));
                        Glide.with(getActivity()).applyDefaultRequestOptions(requestOptions).load(DataObject.getString("user_image"))
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user_image);
                        swipeRefresh.setRefreshing(false);
                    } else if (status.equals("0")) {
                        Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                    sessionManager.setUser(getContext(), userDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("user_id", UserId);
                Log.d("param", "" + param);
                return param;
            }
        };
        Commn.requestQueue(getContext(), userRequest);
    }

    public static ProfileFragment getInstance() {
        return instance;
    }

    private void iniViews(View view) {
        sessionManager = new SessionManager();

        bt_logout = view.findViewById(R.id.bt_logout);
        rv_payment_history_list = view.findViewById(R.id.rv_payment_history_list);
        tv_total_time = view.findViewById(R.id.tv_total_time);
        tv_name = view.findViewById(R.id.tv_name);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_income_coin = view.findViewById(R.id.tv_income_coin);
        tv_call_coins = view.findViewById(R.id.tv_call_coins);
        tv_chat_coins = view.findViewById(R.id.tv_chat_coins);
        tv_full_name = view.findViewById(R.id.tv_full_name);
        tv_user_name2 = view.findViewById(R.id.tv_user_name2);
        tv_refer_code = view.findViewById(R.id.tv_refer_code);
        tv_edit_profile = view.findViewById(R.id.tv_edit_profile);
        iv_user_image = view.findViewById(R.id.iv_user_image);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        // payment history
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_payment_history_list.hasFixedSize();
        rv_payment_history_list.setLayoutManager(layoutManager);

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });
        tv_refer_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    readyToShare();
                }

            }
        });

        tv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

    }

    private void readyToShare() {

        String contentToShare = "Download " + getString(R.string.app_name) + " app at " + Commn.play_store_url
                + Objects.requireNonNull(getActivity()).getPackageName() + " and Enter this " + sessionManager.getUser(getActivity()).getRefer_code() + " Referral code to get money";

        if (Patterns.WEB_URL.matcher(Commn.play_store_url + getActivity().getPackageName()).matches()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, contentToShare);

            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share"));
        } else {
            Commn.myToast(getActivity(), "Coming soon...");
        }

    }

    public void setDetails() {
        if (getActivity() != null) {
            tv_name.setText(sessionManager.getUser(getActivity()).getName());
            tv_user_name.setText(sessionManager.getUser(getActivity()).getUser_name());
            tv_call_coins.setText(sessionManager.getUser(getActivity()).getCall_coin());
            tv_chat_coins.setText(sessionManager.getUser(getActivity()).getChat_coin());
            tv_full_name.setText(sessionManager.getUser(getActivity()).getName());
            tv_user_name2.setText(sessionManager.getUser(getActivity()).getUser_name());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.error(R.drawable.profile);
            requestOptions.placeholder(R.drawable.profile);
            Glide.with(getActivity()).applyDefaultRequestOptions(requestOptions).load(sessionManager.getUser(getActivity()).getUser_image())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user_image);
            swipeRefresh.setRefreshing(false);
        }
    }

    private void shoPaymentHistory(final String UserId){
        StringRequest historyRequest = new StringRequest(Request.Method.POST, MyApi.package_purchase_list_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("history_Res","Success " + response);
                mList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0;i < jsonArray.length();i++){
                            JSONObject pay_info = jsonArray.getJSONObject(i);
                            HistoryModel historyModel = new HistoryModel();
                            historyModel.setTransection_id(pay_info.getString("transection_id"));
                            historyModel.setPackage_name(pay_info.getString("package_name"));
                            historyModel.setPackage_type(pay_info.getString("package_type"));
                            historyModel.setPackage_coins(pay_info.getString("package_coins"));
                            historyModel.setPayment_status(pay_info.getString("payment_status"));
                            historyModel.setPackage_image(pay_info.getString("package_image"));
                            historyModel.setTime(pay_info.getString("time"));
                            mList.add(i,historyModel);
                        }
                        histroyAdapter = new HistroyAdapter(getContext(),mList);
                    }
                    rv_payment_history_list.setAdapter(histroyAdapter);
                }catch (JSONException ex){
                    Log.d("error_history",""+ex.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("history_Error","Success " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("user_id",UserId);
                return param;
            }
        };
        Commn.requestQueue(getContext(),historyRequest);
    }

    private void logoutDialog() {

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Sure Logout");
        ad.setMessage("Do You Want To Logout ?");

        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                logout(dialog);

            }
        });
        ad.create();
        ad.show();
    }

    private void logout(DialogInterface dialog) {
        if (dialog != null) {

            dialog.dismiss();
        }

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SessionManager.isUserSaved, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getActivity(), LaunchActivity.class);
        startActivity(intent);
        getActivity().finishAffinity();
    }

    private void setOnRefresh() {
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                //setDeatils();
                getUserData();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //setDeatils();
                getUserData();
            }
        });
    }
}
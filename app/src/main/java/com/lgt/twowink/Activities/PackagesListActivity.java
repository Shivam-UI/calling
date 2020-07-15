package com.lgt.twowink.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lgt.twowink.Adapter.PackagesListAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Model.PackagesModel;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackagesListActivity extends AppCompatActivity {


    private PackagesListAdapter adapter;
    private RecyclerView rv_package_list;
    private List<PackagesModel>package_list=new ArrayList<>();
    private ProgressBar prgressbar;
    private ImageView iv_back;
    private Context context;
    private PackagesListActivity activity;
    private SwipeRefreshLayout package_swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_list);

        context=activity=this;
        iniViews();

        setOnSwipeRefresh();


    }




    private void iniViews() {
        rv_package_list=findViewById(R.id.rv_package_list);
        iv_back=findViewById(R.id.iv_back);
        package_swipe_refresh=findViewById(R.id.package_swipe_refresh);
        prgressbar=findViewById(R.id.prgressbar);
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
        StringRequest stringRequest=new StringRequest(Request.Method.GET, MyApi.package_list_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                package_swipe_refresh.setRefreshing(false);
                prgressbar.setVisibility(View.GONE);
                Log.e("package_list_api",response+"");
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String message=jsonObject.getString("message");
                    String status=jsonObject.getString("status");

                    if (status.equalsIgnoreCase("1")){
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        if (jsonObject.length()>0){
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tbl_package_id=jsonObject1.getString("tbl_package_id");
                                String package_name=jsonObject1.getString("name");
                                String price=jsonObject1.getString("price");
                                String coins=jsonObject1.getString("coins");
                                String color=jsonObject1.getString("color");
                                String image=jsonObject1.getString("image");

                                package_list.add(new PackagesModel(tbl_package_id,package_name,price,coins,color,image));
                            }
                            setPackageAdapter(package_list);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgressbar.setVisibility(View.GONE);
                Commn.myToast(context,error.getMessage()+"");
            }
        });
        Commn.requestQueue(getApplicationContext(),stringRequest);


    }

    private void setPackageAdapter(List<PackagesModel> package_list) {
        adapter=new PackagesListAdapter(activity,package_list);
        rv_package_list.setHasFixedSize(true);
        rv_package_list.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));

        rv_package_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
package com.lgt.twowink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lgt.twowink.Activities.MainActivity;
import com.lgt.twowink.Activities.VideoCallingActivity;
import com.lgt.twowink.Adapter.NearbyDatesAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.DummyUrls;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.NearbyDatesModel;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import im.delight.android.location.SimpleLocation;


public class HomeFragment extends Fragment  {

    private RecyclerView rv_nearby_dates;
    private NearbyDatesAdapter nearbyDatesAdapter;
    private List<NearbyDatesModel>nearby_dates_list=new ArrayList<>();

    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefresh;
    private SimpleLocation simpleLocation;
   private ProgressBar progress_bar;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_home, container, false);

        iniViews(view);
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                loadNearbyDates();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNearbyDates();
            }
        });
        return view;
    }

    private void iniViews(View view) {
        sessionManager=new SessionManager();
        simpleLocation=new SimpleLocation(Objects.requireNonNull(getActivity()));
        // if we can't access the location yet
        if (!simpleLocation.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(getActivity());
        }
        rv_nearby_dates=view.findViewById(R.id.rv_nearby_dates);
        swipeRefresh=view.findViewById(R.id.swipeRefresh);
        progress_bar=view.findViewById(R.id.progress_bar);
    }

    private void loadNearbyDates() {
        nearby_dates_list.clear();
        progress_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, MyApi.user_list_by_location_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeRefresh.setRefreshing(false);

                Log.e("user_list_by_location",response.toString()+"");
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String status=jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")){
                        progress_bar.setVisibility(View.GONE);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        if (jsonArray.length()>0){
                            for (int i=0;i<jsonArray.length();i++){

                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tbl_user_registration_id=jsonObject1.getString("tbl_user_registration_id");
                                String name=jsonObject1.getString("name");
                                String mobile=jsonObject1.getString("mobile");
                                String email=jsonObject1.getString("email");
                                String gender=jsonObject1.getString("gender");
                                String user_image=jsonObject1.getString("user_image");
                                nearby_dates_list.add(new NearbyDatesModel(tbl_user_registration_id,name,mobile,gender,user_image));

                            }

                            setNearbyDatesAdapter();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("user_id",sessionManager.getUser(Objects.requireNonNull(getActivity())).getUser_id());
                params.put("latitude",String.valueOf(simpleLocation.getLatitude()));
                params.put("longitute",String.valueOf(simpleLocation.getLongitude()));
                return params;
            }
        };
        Commn.requestQueue(getActivity(),stringRequest);





    }

    public void setNearbyDatesAdapter(){
        nearbyDatesAdapter=new NearbyDatesAdapter(getActivity(),nearby_dates_list);
        rv_nearby_dates.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(),2);
        rv_nearby_dates.setLayoutManager(layoutManager);
        rv_nearby_dates.setAdapter(nearbyDatesAdapter);
        nearbyDatesAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        // make the device update its location
        simpleLocation.beginUpdates();
    }

    @Override
    public void onPause() {
        // make the device update its location
        simpleLocation.endUpdates();
        super.onPause();
    }
}
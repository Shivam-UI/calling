package com.lgt.twowink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lgt.twowink.Adapter.CurrentCallsAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.CurrentCallsModel;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CallsFragment extends Fragment {

    private RecyclerView rv_current_chat;
    private List<CurrentCallsModel> chat_convers_list = new ArrayList<>();
    private SessionManager sessionManager;
    private CurrentCallsAdapter currentConversationAdapter;

    public CallsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        sessionManager = new SessionManager();
        Commn.showDialog(view.getContext());
        iniViews(view);
        loadNearbyDates(chat_convers_list);
        loadCallHistory();
        return view;
    }

    private void loadCallHistory() {
        StringRequest callHistory = new StringRequest(Request.Method.POST, MyApi.call_history_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                chat_convers_list.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    Log.e("msg",message);
                    for (int i = 0;i < jsonArray.length();i++){
                        JSONObject time = jsonArray.getJSONObject(i);
                        CurrentCallsModel call = new CurrentCallsModel();
                        call.setLast_msg(time.getString("call_duration"));
                        call.setUser_image(time.getString("image"));
                        call.setUser_name(time.getString("user_name"));
                        chat_convers_list.add(i,call);
                        Log.e("time_data",time.getString("call_duration")+" | "+i);
                    }
                    loadNearbyDates(chat_convers_list);
                    Commn.hideDialog(getContext());
                } catch (JSONException ex) {

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
                param.put("user_id", sessionManager.getUser(getContext()).getUser_id());
                Log.e("call_data", param.toString());
                return param;
            }
        };
        Commn.requestQueue(getContext(), callHistory);
    }

    private void iniViews(View view) {
        rv_current_chat = view.findViewById(R.id.rv_current_chat);
    }

    private void loadNearbyDates(List<CurrentCallsModel> chat_convers_list) {
        /*chat_convers_list.clear();
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates1, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates2, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "offline"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates3, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates4, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates2, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates3, "dsgfdsfdfsgdfsgdsgdsdsdsdsdsd", "offline"));*/

        currentConversationAdapter = new CurrentCallsAdapter(getActivity(),chat_convers_list);
        rv_current_chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_current_chat.setLayoutManager(layoutManager);
        rv_current_chat.setAdapter(currentConversationAdapter);
        currentConversationAdapter.notifyDataSetChanged();
    }
}
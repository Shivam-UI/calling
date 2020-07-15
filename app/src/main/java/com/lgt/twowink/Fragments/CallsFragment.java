package com.lgt.twowink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgt.twowink.Adapter.CurrentCallsAdapter;
import com.lgt.twowink.Extras.DummyUrls;
import com.lgt.twowink.Model.CurrentCallsModel;
import com.lgt.twowink.Model.CurrentConversationModel;
import com.lgt.twowink.R;

import java.util.ArrayList;
import java.util.List;


public class CallsFragment extends Fragment {

    private RecyclerView rv_current_chat;
    private List<CurrentCallsModel> chat_convers_list=new ArrayList<>();

    private CurrentCallsAdapter currentConversationAdapter;

    public CallsFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calls, container, false);
        iniViews(view);
        loadNearbyDates();
        return view;
    }
    private void iniViews(View view) {
        rv_current_chat=view.findViewById(R.id.rv_current_chat);
    }

    private void loadNearbyDates() {
        chat_convers_list.clear();
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates1,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates2,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","offline"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates3,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates4,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates2,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","online"));
        chat_convers_list.add(new CurrentCallsModel("Jimmy shu", DummyUrls.nearby_dates3,"dsgfdsfdfsgdfsgdsgdsdsdsdsdsd","offline"));

        currentConversationAdapter=new CurrentCallsAdapter(getActivity(),chat_convers_list);
        rv_current_chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_current_chat.setLayoutManager(layoutManager);
        rv_current_chat.setAdapter(currentConversationAdapter);
        currentConversationAdapter.notifyDataSetChanged();
    }
}
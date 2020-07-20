package com.lgt.twowink.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lgt.twowink.Adapter.CurrentConversationAdapter;
import com.lgt.twowink.Adapter.NearbyDatesAdapter;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.DummyUrls;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.ChatModel;
import com.lgt.twowink.Model.CurrentConversationModel;
import com.lgt.twowink.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatFragment extends Fragment {

    private RecyclerView rv_current_chat;
    private List<CurrentConversationModel> chat_convers_list=new ArrayList<>();

    private FirebaseDatabase database;
    private TextView tv_conversation;
    private DatabaseReference conver_Ref;
    private SessionManager sessionManager;

    private CurrentConversationAdapter currentConversationAdapter;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        Commn.showDialog(view.getContext());
        iniViews(view);
        loadNearbyDates();
        return view;
    }
    private void iniViews(View view) {
        database=FirebaseDatabase.getInstance();
        sessionManager=new SessionManager();
        rv_current_chat=view.findViewById(R.id.rv_current_chat);
        tv_conversation=view.findViewById(R.id.tv_conversation);
    }

    private void loadNearbyDates() {
        chat_convers_list.clear();

        conver_Ref=database.getReference().child(Commn.Current_Chat_Users);

        conver_Ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()){

                    CurrentConversationModel model=snapshot.getValue(CurrentConversationModel.class);
                    if (model.getFrom().equalsIgnoreCase(sessionManager.getUser(getActivity()).getUser_id())){
                       // holder.itemView.setVisibility(View.GONE);
                    }else {
                        chat_convers_list.add(model);
                    }
                    Commn.hideDialog(getContext());
                    setAdapter();
                    if (chat_convers_list.size()==0){
                        tv_conversation.setVisibility(View.VISIBLE);
                    }else {
                        tv_conversation.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapter() {
        currentConversationAdapter=new CurrentConversationAdapter(getActivity(),chat_convers_list);
        rv_current_chat.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        rv_current_chat.setLayoutManager(layoutManager);
        rv_current_chat.setAdapter(currentConversationAdapter);
        currentConversationAdapter.notifyDataSetChanged();
    }

}
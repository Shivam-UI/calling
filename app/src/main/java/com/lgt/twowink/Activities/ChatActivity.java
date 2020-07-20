package com.lgt.twowink.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lgt.twowink.Adapter.ChatHolder;

import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.ChatModel;
import com.lgt.twowink.Model.UserDetails;
import com.lgt.twowink.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChatActivity extends AppCompatActivity {

    private ImageView iv_back, iv_send_message;
    private EditText et_message;
    private String typed_message;
    private Context context;
    private ChatActivity activity;
    private RecyclerView rv_chat;
    private String from_key, user_name, user_image;
    private ImageView iv_user_image;
    private TextView tv_username;
    private DatabaseReference chatRef;
    private FirebaseDatabase database;
    private SessionManager sessionManager;
    FirebaseRecyclerOptions<ChatModel> options;
    FirebaseRecyclerAdapter<ChatModel, RecyclerView.ViewHolder> adapterchat;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = activity = ChatActivity.this;
        //user_id=sessionManager.getUser(this).getUser_id();
        iniViews();
        loadChat();
    }

    private void iniViews() {

        database = FirebaseDatabase.getInstance();
        sessionManager = new SessionManager();


        iv_back = findViewById(R.id.iv_back);
        et_message = findViewById(R.id.et_message);
        iv_send_message = findViewById(R.id.iv_send_message);
        iv_user_image = findViewById(R.id.iv_user_image);
        tv_username = findViewById(R.id.tv_username);
        rv_chat = findViewById(R.id.rv_chat);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });
        getStringKey();
        iv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typed_message = et_message.getText().toString().trim();
                if (TextUtils.isEmpty(typed_message)) {
                    Commn.myToast(context, "Can't send empty message");
                } else {
                    Log.e("eeeeeeee",sessionManager.getUser(context).getChat_coin());
                    if (Integer.parseInt(sessionManager.getUser(context).getChat_coin()) > 0) {
                        startChat();
                    } else {
                        //lessCoinsDialog();
                        insufficientBalance("You don't have enough coin balance to perform this operation");
                    }

                }
            }
        });


    }

    private void saveUserDetail(String coins) {

        try {
            UserDetails userDetails = new UserDetails();
            userDetails.setUser_id(sessionManager.getUser(context).getUser_id());
            userDetails.setName(sessionManager.getUser(context).getName());
            userDetails.setUser_name(sessionManager.getUser(context).getUser_name());

            userDetails.setMobile(sessionManager.getUser(context).getMobile());
            userDetails.setCall_coin(sessionManager.getUser(context).getCall_coin());
            userDetails.setChat_coin(coins);
            userDetails.setRefer_code(sessionManager.getUser(context).getRefer_code());
            userDetails.setUser_image(user_image);
            sessionManager.setUser(context, userDetails);
            SessionManager.saveUser(context,"true");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insufficientBalance(String msg) {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(true);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                Intent alert_chat = new Intent(ChatActivity.this, PackagesListActivity.class);
                alert_chat.putExtra("KEY_CHAT","Chat");
                alert_chat.putExtra("KEY_TYPE","Message");
                startActivity(alert_chat);
            }
        });
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        pDialog.show();
    }

    // not in use
    private void lessCoinsDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);

        ad.setMessage("You don't have enough coin balance to perform this operation");

        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                startActivity(new Intent(activity, PackagesListActivity.class));

            }
        });
        ad.create();
        ad.show();
    }

    private void getStringKey() {
        if (getIntent().hasExtra(Commn.USER_ID)) {
            from_key = getIntent().getStringExtra(Commn.USER_ID);
            user_name = getIntent().getStringExtra(Commn.user_name);
            user_image = getIntent().getStringExtra(Commn.user_image);

            tv_username.setText(user_name);
            Glide.with(context).load(user_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user_image);


            Log.e("from_key", from_key + "");
        }
    }

    private void startChat() {
        chatRef = database.getReference();
        String currentUserRef = Commn.User_Messages + "/" + sessionManager.getUser(context).getUser_id() + "/" + from_key;
        String chat_user_Ref = Commn.User_Messages + "/" + from_key + "/" + sessionManager.getUser(context).getUser_id();

        DatabaseReference user_message_push = chatRef.child(Commn.User_Messages).
                child(sessionManager.getUser(context).getUser_id()).child(from_key).push();
        String push_id = user_message_push.getKey();

        Map messageMap = new HashMap();
        messageMap.put("messageid", push_id);
        messageMap.put("user_image", sessionManager.getUser(context).getUser_image());
        messageMap.put("user_name", sessionManager.getUser(context).getName());
        messageMap.put("message", typed_message);
        messageMap.put("seen", "false");
        messageMap.put("messageType", Commn.TEXT_TYPE);
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", sessionManager.getUser(context).getUser_id());
        Map messageUserMap = new HashMap();
        messageUserMap.put(currentUserRef + "/" + push_id, messageMap);
        messageUserMap.put(chat_user_Ref + "/" + push_id, messageMap);
        et_message.setText("");

        chatRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                updateCurrentUsers();

                updateCoins();
            }
        });


    }

    private void updateCoins() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApi.check_coins_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Commn.hideDialog(activity);
                Log.e("check_coins_api", response + "");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String coins = jsonObject.getString("coins");
                        saveUserDetail(coins);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.hideDialog(activity);
                Commn.myToast(context, error.getMessage() + "");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUser(context).getUser_id());
                params.put("type", "Message");
                Log.e("check_coins_api", params + "");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(), stringRequest);
    }

    private void updateCurrentUsers() {


        DatabaseReference reference = database.getReference();
        String currentUserRef = Commn.Current_Chat_Users + "/" + sessionManager.getUser(context).getUser_id();
        Map messageMap = new HashMap();

        messageMap.put(Commn.user_image, sessionManager.getUser(context).getUser_image());

        messageMap.put(Commn.user_name, sessionManager.getUser(context).getName());
        messageMap.put("message", typed_message);
        messageMap.put("seen", "false");
        messageMap.put("messageType", Commn.TEXT_TYPE);
        messageMap.put("time", Commn.Time(System.currentTimeMillis()));
        messageMap.put("from", sessionManager.getUser(context).getUser_id());
        Map messageUserMap = new HashMap();
        messageUserMap.put(currentUserRef, messageMap);


        reference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                updateAnotherUsers();


            }
        });


    }

    private void updateAnotherUsers() {
        DatabaseReference reference = database.getReference();

        String chat_user_Ref = Commn.Current_Chat_Users + "/" + from_key;

        Map messageMap = new HashMap();

        messageMap.put(Commn.user_image, user_image);
        messageMap.put(Commn.user_name, user_name);
        messageMap.put("message", typed_message);
        messageMap.put("seen", "false");
        messageMap.put("messageType", Commn.TEXT_TYPE);
        messageMap.put("time", Commn.Time(System.currentTimeMillis()));
        messageMap.put("from", from_key);
        Map messageUserMap = new HashMap();

        messageUserMap.put(chat_user_Ref, messageMap);


        reference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {


            }
        });
    }

    private void loadChat() {

        iniFirebaseOptions();


        adapterchat = new FirebaseRecyclerAdapter<ChatModel, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull ChatModel chatModel) {

                Log.e("from_key", chatModel.getMessageType() + "");

                Log.e("from_key", chatModel.getMessage() + "");
                ((ChatHolder) viewHolder).tv_chat_msg.setText(chatModel.getMessage());
                ((ChatHolder) viewHolder).tv_chat_time.setText(Commn.Time(chatModel.getTime()));
                if (chatModel.getFrom().equalsIgnoreCase(sessionManager.getUser(context).getUser_id())) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(150, 0, 0, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    params2.setMargins(0, 7, 0, 0);
                    ((ChatHolder) viewHolder).tv_chat_time.setLayoutParams(params2);
                    ((ChatHolder) viewHolder).tv_chat_msg.setBackground(context.getResources().getDrawable(R.drawable.sent_chat_bg));
                    ((ChatHolder) viewHolder).tv_chat_msg.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    ((ChatHolder) viewHolder).tv_chat_msg.setLayoutParams(params);

                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 150, 0);
                    ((ChatHolder) viewHolder).tv_chat_msg.setLayoutParams(params);

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.START;
                    params2.setMargins(0, 7, 0, 0);
                    ((ChatHolder) viewHolder).tv_chat_time.setLayoutParams(params2);
                    ((ChatHolder) viewHolder).tv_chat_msg.setBackground(context.getResources().getDrawable(R.drawable.recieved_chat_bg));
                    ((ChatHolder) viewHolder).tv_chat_msg.setTextColor(context.getResources().getColor(R.color.white));
                }
            }


            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View sender_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
                return new ChatHolder(sender_view);


            }
        };

        adapterchat.startListening();
        setChatAdapter();

    }

    private void setChatAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);

        rv_chat.setLayoutManager(layoutManager);

        rv_chat.setHasFixedSize(true);
        rv_chat.setAdapter(adapterchat);
        rv_chat.setNestedScrollingEnabled(false);
        adapterchat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = adapterchat.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.

                Log.e("countttmess", friendlyMessageCount + "");
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rv_chat.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void iniFirebaseOptions() {
        chatRef = database.getReference().child(Commn.User_Messages).child(sessionManager.getUser(context).getUser_id()).child(from_key);
        options = new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(chatRef, ChatModel.class).build();
    }

}
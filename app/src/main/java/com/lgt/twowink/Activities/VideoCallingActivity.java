package com.lgt.twowink.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lgt.twowink.Extras.AgoraConfig;
import com.lgt.twowink.Extras.Client;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Interface.APIService;
import com.lgt.twowink.Model.Data;
import com.lgt.twowink.Model.MyResponse;
import com.lgt.twowink.Model.Sender;
import com.lgt.twowink.Model.Token;
import com.lgt.twowink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class VideoCallingActivity extends AppCompatActivity {


    private static final String TAG = VideoCallingActivity.class.getSimpleName();
    private static ScheduledExecutorService timer;
    private static final int PERMISSION_REQ_ID = 22;
    private static String customer_id, current_user_id, Calling_time, caller_name;
    Calendar cal;
    boolean isChecked;
    private static int user_history_token = 0;
    Intent intent;
    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;


    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });


        }


        /**
         * Occurs when the first remote video frame is received and decoded.
         * This callback is triggered in either of the following scenarios:
         *
         *     The remote user joins the channel and sends the video stream.
         *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
         *         The remote user leaves channel.
         *         The remote user drops offline.
         *         The remote user calls the muteLocalVideoStream method.
         *         The remote user calls the disableVideo method.
         *
         * @param uid User ID of the remote user sending the video streams.
         * @param width Width (pixels) of the video stream.
         * @param height Height (pixels) of the video stream.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
         */
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    setupRemoteVideo(uid);
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                    Toast.makeText(VideoCallingActivity.this, "Call End", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VideoCallingActivity.this, MainActivity.class));
                    finishAffinity();
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        // Destroys remote view
        mRemoteView = null;
    }

    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);

        initUI();
        timer = Executors.newSingleThreadScheduledExecutor();
        intent = getIntent();
        customer_id = intent.getStringExtra(Commn.USER_ID);
        current_user_id = intent.getStringExtra("mUser");
        caller_name = intent.getStringExtra("Caller_name");
        Log.d("call_m", "" + caller_name);
        handler = new Handler();
        delay();
        // timer.scheduleAtFixedRate(calling, 1, 1, TimeUnit.MINUTES);
        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
    }

    CountDownTimer countDownTimer = new CountDownTimer(60 * 60 * 60 * 1000, 60 * 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

        }
    };

    private void delay() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("user", customer_id + " -> " + current_user_id + " is_calling. " + cal.getTime());
                callStarting("Call", current_user_id);
                handler.postDelayed(runnable, 60 * 1000);
            }
        };
        handler.postDelayed(runnable, 60 * 1000);
    }

    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

        // Sample logs are optional.

    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
        // is call start
        Toast.makeText(this, "Calling", Toast.LENGTH_SHORT).show();
        callStarting("Call", current_user_id);
        callUpdateHistory(current_user_id, customer_id);
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_240P_3, false);
        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = AgoraConfig.Token;
       /* if (TextUtils.isEmpty(token) || TextUtils.equals(token, AgoraConfig.Token)) {
            token = null; // default, no token
        }*/
        mRtcEngine.joinChannel(token, AgoraConfig.APP_Channel, "Extra Optional Data", 0);
        isChecked = getIntent().getBooleanExtra("KEY_NOTI", false);
    }

    private void sendNotification() {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child(Commn.Tokens).child(customer_id);
        Log.d("customer_id", customer_id);

        tokens.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                Data data = new Data(current_user_id, R.drawable.icon, caller_name,
                        customer_id);
                Log.d("sp", token.getUser_token() + " | " + dataSnapshot);
                Sender sender = new Sender(data, token.getUser_token());
                APIService apiService = Client.getClient(Commn.FIREBASE_URL).create(APIService.class);
                apiService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                Log.d("Notification", call + "" + response.body());
                                Log.d("Notification", " | " + response.isSuccessful());
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.d("Error", call + " " + t.getStackTrace());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("db_error", "" + databaseError);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.
          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.ic_baseline_mic_off_24 : R.drawable.ic_baseline_mic_24;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.profile);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.profile);
        }

        showButtons(!mCallEnd);
    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
        handler.removeCallbacks(runnable);
        callEnding(user_history_token);

    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private void callStarting(final String mtype, final String mid) {
        StringRequest request = new StringRequest(Request.Method.POST, MyApi.check_coins_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("calling_api", response.toString() + "");
                Commn.hideDialog(getApplicationContext());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    Log.d("balance", "" + message);
                    if (status.equals("1")) {
                        if (isChecked) {
                        } else {
                            sendNotification();
                        }
                    } else if (status.equals("0")) {
                        SweetAlertDialog pDialog = new SweetAlertDialog(VideoCallingActivity.this, SweetAlertDialog.WARNING_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText(message);
                        pDialog.setCancelable(true);
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent packageList = new Intent(VideoCallingActivity.this, PackagesListActivity.class);
                                packageList.putExtra("KEY_TYPE","Call");
                                startActivity(packageList);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.myToast(getApplicationContext(), error.getMessage() + "");
                Commn.hideDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", mid);
                params.put("type", mtype);
                Log.e("calling_api", params.toString() + "");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(), request);
    }

    private void callUpdateHistory(final String mid, final String cus_id) {
        cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        Calling_time = hour + ":" + minute + ":" + second;
        StringRequest request = new StringRequest(Request.Method.POST, MyApi.user_history_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Commn.hideDialog(getApplicationContext());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("data");
                    JSONObject user = jsonObject.getJSONObject("data");
                    Log.e("history_api", message + " | " + status + " | " + user.getInt("tbl_user_call_history_id"));
                    user_history_token = user.getInt("tbl_user_call_history_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.myToast(getApplicationContext(), error.getMessage() + "");
                Commn.hideDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", mid);
                params.put("customer_id", cus_id);
                params.put("time", Calling_time);
                Log.e("history_api", params.toString() + "");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(), request);
    }

    private void callEnding(final int tbl_user) {
        cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        Calling_time = hour + ":" + minute + ":" + second;
        StringRequest request = new StringRequest(Request.Method.POST, MyApi.user_history_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("call_end_api", response.toString() + "");
                Commn.hideDialog(getApplicationContext());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Commn.myToast(getApplicationContext(), error.getMessage() + "");
                Commn.hideDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("endtime", Calling_time);
                params.put("tbl_user_call_history_id", "" + tbl_user);
                Log.e("call_end_api", params.toString() + "");
                return params;
            }
        };
        Commn.requestQueue(getApplicationContext(), request);
    }
}

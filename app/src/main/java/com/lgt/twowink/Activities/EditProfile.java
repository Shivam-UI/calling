package com.lgt.twowink.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Extras.VolleyMultipartRequest;
import com.lgt.twowink.Fragments.ProfileFragment;
import com.lgt.twowink.Interface.UpdateUserDetails;
import com.lgt.twowink.Model.UserDetails;
import com.lgt.twowink.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private TextView tv_update_profile;
    private CircleImageView iv_user_image;
    private ImageView iv_back;
    private TextInputEditText et_name,et_username;
    private SessionManager sessionManager;
    private Context context;
    private EditProfile activity;
    private Bitmap bitmap;

    UpdateUserDetails update_listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context=activity=EditProfile.this;
        iniaViews();

    }



    private void iniaViews() {
        sessionManager=new SessionManager();
        tv_update_profile=findViewById(R.id.tv_update_profile);
        iv_user_image=findViewById(R.id.iv_user_image);
        et_name=findViewById(R.id.et_name);
        et_username=findViewById(R.id.et_username);
        iv_back=findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setUserInfo();

        iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions();
                }
            }
        });

        tv_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                updateProfile();
            }
        });
    }

    private void updateProfile() {
        Commn.showDialog(activity);
        VolleyMultipartRequest volleyMultipartRequest=new VolleyMultipartRequest(Request.Method.POST, MyApi.edit_profile_api, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("upload_response",response+"");
                Commn.hideDialog(activity);
                try {
                    JSONObject jsonObject=new JSONObject(new String(response.data));
                    String message=jsonObject.getString("message");
                    String status=jsonObject.getString("status");

                    if (status.equals("1")){

                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        String name=jsonObject1.getString("name");
                        String user_image=jsonObject1.getString("user_image");
                        Commn.myToast(context,message);
                        saveData(name,user_image);
                    }else {

                        Commn.myToast(context,message);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",sessionManager.getUser(activity).getUser_id());
                params.put("name",et_name.getText().toString());
                Log.e("upload_params",params+"");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();

                if (bitmap!=null){
                    params.put("image", new DataPart(sessionManager.getUser(activity).getUser_id()+ ".png", getFileDataFromDrawable(bitmap)));
                }

                Log.e("PARAMS", params + "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volleyMultipartRequest.setShouldCache(true);
        requestQueue.add(volleyMultipartRequest);
    }

    private void saveData(String name, String user_image) {
        try {
            UserDetails userDetails=new UserDetails();
            userDetails.setUser_id(sessionManager.getUser(context).getUser_id());
            userDetails.setName(name);
            userDetails.setUser_name(sessionManager.getUser(context).getUser_name());

            userDetails.setMobile(sessionManager.getUser(context).getMobile());
            userDetails.setCall_coin(sessionManager.getUser(context).getCall_coin());
            userDetails.setChat_coin(sessionManager.getUser(context).getChat_coin());
            userDetails.setRefer_code(sessionManager.getUser(context).getRefer_code());
            userDetails.setUser_image(user_image);

            sessionManager.setUser(context,userDetails);
            ProfileFragment.getInstance().setDeatils();

            updateData(name,user_image);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateData(final String username, final String userimage) {
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child(Commn.Current_Chat_Users).child(sessionManager.getUser(context).getUser_id());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    reference.child("user_name").setValue(username);
                    reference.child("user_image").setValue(userimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void setUserInfo() {
        et_name.setText(sessionManager.getUser(context).getName());
        et_username.setText(sessionManager.getUser(context).getUser_name());

       Glide.with(context).load(sessionManager.getUser(context).getUser_image()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user_image);
    }
    private boolean checkPermissions() {

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Commn.storage_permission) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((activity),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), 100);
            return false;
        } else {
            bitmap=null;
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(activity);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bitmap=null;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(activity);
            } else {

            }
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){

                assert result != null;
                Uri imageUri=result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    iv_user_image.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                bitmap=null;
            }
        }
    }

    private void changeProfilePicture() {

    }

}
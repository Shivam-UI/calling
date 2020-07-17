package com.lgt.twowink.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lgt.twowink.Activities.ChatActivity;
import com.lgt.twowink.Activities.PackagesListActivity;
import com.lgt.twowink.Activities.UserProfile;
import com.lgt.twowink.Activities.VideoCallingActivity;
import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.MyFirebaseIdService;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Model.NearbyDatesModel;
import com.lgt.twowink.R;

import java.util.List;
import java.util.Objects;

public class NearbyDatesAdapter extends RecyclerView.Adapter<NearbyDatesAdapter.ViewHolder> {
    MyFirebaseIdService firebaseIdService;
    private Context context;
    private List<NearbyDatesModel> list;
    SessionManager sessionManager;
    public NearbyDatesAdapter(Context context, List<NearbyDatesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NearbyDatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.nearby_dates_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyDatesAdapter.ViewHolder holder, final int position) {
        sessionManager = new SessionManager();
        final NearbyDatesModel model = list.get(position);
        try {
            Glide.with(context).load(model.getUser_image()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_user_image);
            holder.tv_user_name.setText(model.getUser_name());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra(Commn.USER_ID,model.getUser_id());
                intent.putExtra(Commn.user_name,model.getUser_name());
                intent.putExtra(Commn.user_image,model.getUser_image());
                intent.putExtra(Commn.gender,model.getGender());
                context.startActivity(intent);
            }
        });
        holder.iv_start_video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseIdService = new MyFirebaseIdService();
                Intent intent = new Intent(context, VideoCallingActivity.class);
                intent.putExtra(Commn.USER_ID,model.getUser_id());
                intent.putExtra("Caller_name",sessionManager.getUser(Objects.requireNonNull(context)).getUser_name());
                intent.putExtra("mUser",sessionManager.getUser(Objects.requireNonNull(context)).getUser_id());
                Log.d("who's_calling",""+sessionManager.getUser(Objects.requireNonNull(context)).getUser_name());
                context.startActivity(intent);
            }
        });

        holder.iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Commn.USER_ID,model.getUser_id());
                    intent.putExtra(Commn.user_name,model.getUser_name());
                    intent.putExtra(Commn.user_image,model.getUser_image());
                    Log.e("from_key",model.getUser_id()+",");
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_user_image, iv_chat,iv_start_video_call;
        private TextView tv_user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_user_image = itemView.findViewById(R.id.iv_user_image);
            iv_chat = itemView.findViewById(R.id.iv_chat);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            iv_start_video_call = itemView.findViewById(R.id.iv_start_video_call);
        }
    }
}

package com.lgt.twowink.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lgt.twowink.Activities.VideoCallingActivity;
import com.lgt.twowink.Model.CurrentCallsModel;
import com.lgt.twowink.Model.CurrentConversationModel;
import com.lgt.twowink.R;

import java.util.List;

public class CurrentCallsAdapter extends RecyclerView.Adapter<CurrentCallsAdapter.ViewHolder> {

    private Context context;
    private List<CurrentCallsModel> list;

    public CurrentCallsAdapter(Context context, List<CurrentCallsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CurrentCallsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.current_calls_layout, parent, false);
        return new CurrentCallsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentCallsAdapter.ViewHolder holder, int position) {

        CurrentCallsModel model=list.get(position);
        try {

            Glide.with(context).load(model.getUser_image()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_user_image);

            holder.tv_user_name.setText(model.getUser_name());

            holder.iv_start_video_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    context.startActivity(new Intent(context, VideoCallingActivity.class));

                }
            });
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_user_image,iv_start_video_call;
        private TextView tv_user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_user_image=itemView.findViewById(R.id.iv_user_image);
            tv_user_name=itemView.findViewById(R.id.tv_user_name);
            iv_start_video_call=itemView.findViewById(R.id.iv_start_video_call);

        }
    }
}

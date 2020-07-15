package com.lgt.twowink.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lgt.twowink.R;

public class ChatHolder extends RecyclerView.ViewHolder {

    public TextView tv_chat_msg,tv_chat_time;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);

        tv_chat_msg=itemView.findViewById(R.id.tv_chat_msg);
        tv_chat_time=itemView.findViewById(R.id.tv_chat_time);
    }


}

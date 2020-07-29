package com.lgt.twowink.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lgt.twowink.Model.HistoryModel;
import com.lgt.twowink.R;

import java.util.List;

public class HistroyAdapter extends RecyclerView.Adapter<HistroyAdapter.HistoryHolder> {

    Context mContext;
    List<HistoryModel> mList;

    public HistroyAdapter(Context mContext, List<HistoryModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.pack_history_list,parent,false);
        return new HistoryHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        holder.tv_package_name.setText("Pack Name : "+mList.get(position).getPackage_name());
        holder.tv_package_type.setText("Pack Type : "+mList.get(position).getPackage_type());
        holder.tv_recharge_date.setText(mList.get(position).getTime()+" Second");
        holder.tv_recharge_status.setText(mList.get(position).getPayment_status());
        holder.tv_earn_coins.setText("You Earn : "+mList.get(position).getPackage_coins());
        Glide.with(mContext)
                .load(mList.get(position)
                        .getPackage_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_package_icon);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        ImageView iv_package_icon;
        TextView tv_package_name,tv_package_type,tv_earn_coins,tv_recharge_date,tv_recharge_status;
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            iv_package_icon=itemView.findViewById(R.id.iv_package_icon);
            tv_package_name=itemView.findViewById(R.id.tv_package_name);
            tv_package_type=itemView.findViewById(R.id.tv_package_type);
            tv_earn_coins=itemView.findViewById(R.id.tv_earn_coins);
            tv_recharge_date=itemView.findViewById(R.id.tv_recharge_date);
            tv_recharge_status=itemView.findViewById(R.id.tv_recharge_status);
        }
    }
}

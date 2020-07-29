package com.lgt.twowink.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lgt.twowink.Extras.SessionManager;
import com.lgt.twowink.Interface.PlaymentMethod;
import com.lgt.twowink.Model.PackagesModel;
import com.lgt.twowink.R;

import java.util.List;

public class PackagesListAdapter extends RecyclerView.Adapter<PackagesListAdapter.ViewHolder> {

    private Context context;
    private List<PackagesModel> list;
    PlaymentMethod playmentMethod;
    SessionManager sessionManager;
    public PackagesListAdapter(Context context, List<PackagesModel> list, PlaymentMethod mMethod) {
        this.context = context;
        this.list = list;
        this.playmentMethod = mMethod;
    }


    @NonNull
    @Override
    public PackagesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.packages_list_layout, parent, false);
        return new PackagesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackagesListAdapter.ViewHolder holder, final int position) {
        sessionManager = new SessionManager();
        final String u_id = sessionManager.getUser(context).getUser_id();
        final String u_name = sessionManager.getUser(context).getUser_name();
        final String u_number = sessionManager.getUser(context).getMobile();
        final String u_email = "test@gmail.com";
        final PackagesModel model = list.get(position);
        try {

            holder.tv_package_amount.setText(context.getString(R.string.Rs) + model.getPrice());
            holder.tv_package_name.setText(model.getPackage_name());
            holder.tv_package_coins.setText(model.getCoins() + " Coins");

            Glide.with(context).load(model.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.iv_package_circle);
            changeBG(model, context, holder);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // position,user_id,package_name,price,mobile_number,_user_name,user_email
                    playmentMethod.makePayment(model.getTbl_package_id(),u_id,model.getPackage_name(),model.getPrice(),u_number,u_name,u_email);
                }
            });

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    private void changeBG(PackagesModel model, Context context, ViewHolder holder) {

        GradientDrawable bgShape = (GradientDrawable) holder.iv_package_circle.getBackground();
        bgShape.mutate();
        bgShape.setColor(Color.parseColor(model.getPackage_color()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_package_amount, tv_package_name, tv_package_coins;
        private ImageView iv_package_circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_package_amount = itemView.findViewById(R.id.tv_package_amount);
            tv_package_name = itemView.findViewById(R.id.tv_package_name);
            tv_package_coins = itemView.findViewById(R.id.tv_package_coins);
            iv_package_circle = itemView.findViewById(R.id.iv_package_circle);
        }
    }
}

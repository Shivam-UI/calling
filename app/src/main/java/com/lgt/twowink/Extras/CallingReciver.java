package com.lgt.twowink.Extras;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.lgt.twowink.R;
import com.tapadoo.alerter.Alerter;

public class CallingReciver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        mContext = context;
        Alerter.create((Activity) context)
                .setTitle("")
                .setText("")
                .addButton("Answer", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Answer", Toast.LENGTH_SHORT).show();
                    }
                })
                .addButton("Decline", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Decline", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

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
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

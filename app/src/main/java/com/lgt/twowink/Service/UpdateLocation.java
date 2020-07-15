package com.lgt.twowink.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.lgt.twowink.Extras.Commn;
import com.lgt.twowink.Extras.SessionManager;

public class UpdateLocation extends Service {

    PowerManager pm;
    PowerManager.WakeLock wl;
    private SessionManager sessionManager=new SessionManager();

    double latitude,longitude;
    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 300000);
            // whatever you want to do below
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (network_enabled) {


                Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.e("coordinates", longitude + ",lati" + latitude + "");
                    Commn.updateUserLocation(getApplicationContext(),latitude,longitude
                            ,sessionManager.getUser(getApplicationContext()).getUser_id());
                    Log.e("fdsdsoo","update");

                }
            }

        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handler.post(periodicUpdate);
        return START_STICKY;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GpsTrackerWakelock");
        wl.acquire();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        wl.release();
        Log.e("calll","esss");
        stopSelf();



    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        wl.release();
        Log.e("calll","esss");
        stopSelf();

    }
}

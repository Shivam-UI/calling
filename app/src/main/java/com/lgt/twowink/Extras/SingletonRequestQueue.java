package com.lgt.twowink.Extras;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonRequestQueue {

    private static SingletonRequestQueue mSingletonRequestQueue;
    private Context mContext;
    private RequestQueue mRequestQueue;

    public SingletonRequestQueue(Context mContext) {
        this.mContext = mContext;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SingletonRequestQueue getInstance(Context mContext){
        if(mSingletonRequestQueue == null){
            mSingletonRequestQueue = new SingletonRequestQueue(mContext);
        }
        return mSingletonRequestQueue;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);

        }

        return mRequestQueue;
    }
}

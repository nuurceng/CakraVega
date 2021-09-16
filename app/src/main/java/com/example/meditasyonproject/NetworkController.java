package com.example.meditasyonproject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkController {
    private static Context mCtx;
    private static NetworkController mInstance;
    private RequestQueue mRequestQueue; //istek kuyruğu

    private NetworkController(Context context){
        mCtx = context;
        this.mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkController getInstance(MainActivity conntext) {
        NetworkController networkController;
        synchronized (NetworkController.class) {
            if (mInstance == null) {
                mInstance = new NetworkController(conntext);
            }
            networkController = mInstance;
        }
        return networkController;
    }

    RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) { //yeni talep kuyruğuna istek ekledik
        getRequestQueue().add(req);
    }
}

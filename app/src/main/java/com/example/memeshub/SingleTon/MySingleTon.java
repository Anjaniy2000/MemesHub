package com.example.memeshub.SingleTon;

import android.annotation.SuppressLint;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/* Implementation Of SingleTon Class Pattern For API - Calling: */
public class MySingleTon {
    @SuppressLint("StaticFieldLeak")
    private static MySingleTon instance;
    private RequestQueue requestQueue;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    /*  Constructor: */
    private MySingleTon(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleTon getInstance(Context context) {
        if (instance == null) {
            instance = new MySingleTon(context);
        }
        return instance;
    }
    
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}


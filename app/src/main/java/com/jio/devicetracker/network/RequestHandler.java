// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.network;


import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class RequestHandler {

    private static final String URL = "https://stg.borqs.io";
    private static final String FMS_BASE_URL = "http://49.40.22.92:8080";
    private static RequestHandler mRequestHandler;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    public static synchronized RequestHandler getInstance(Context context) {
        RequestHandler.mContext = context;
        if (mRequestHandler == null) {
            mRequestHandler = new RequestHandler();
        }
        return mRequestHandler;
    }


    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void handleRequest(IRequest req) {

        VolleyManager volleyReq = new VolleyManager(mContext, req.getMethod(), constructUrl(req.getAction()), req);
        volleyReq.setTag(req.getTag());
        volleyReq.setShouldCache(false);
        volleyReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(volleyReq);
    }

    public void handleFMSRequest(IRequest req) {

        VolleyManager volleyReq = new VolleyManager(mContext, req.getMethod(), constructFMSUrl(req.getAction()), req);
        volleyReq.setTag(req.getTag());
        volleyReq.setShouldCache(false);
        volleyReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(volleyReq);
    }

    private String constructUrl(String action) {
        return URL + action;
    }

    private String constructFMSUrl(String action) {
        return FMS_BASE_URL + action;
    }
}
/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.network;


import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jio.devicetracker.util.Constant;


public class RequestHandler {
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

    private String constructUrl(String action) {
        return Constant.SIT_URL + action;
    }
}
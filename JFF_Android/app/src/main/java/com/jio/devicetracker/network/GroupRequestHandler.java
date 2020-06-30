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

public class GroupRequestHandler {
    private static GroupRequestHandler mRequestHandler;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    /**
     * @param context
     * @return Request Handler instance, so that we can handle the request
     */
    public static synchronized GroupRequestHandler getInstance(Context context) {
        mContext = context;
        if (mRequestHandler == null) {
            mRequestHandler = new GroupRequestHandler();
        }
        return mRequestHandler;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * @return A started {@link RequestQueue} instance.
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add request to the queue
     * @param req
     * @param <T>
     */
    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Makes an final API call, after adding request into the queue
     * @param req
     */
    public void handleRequest(IRequest req) {
        GroupVolleyManager volleyReq = new GroupVolleyManager(mContext, req.getMethod(), constructUrl(req.getAction()), req);
        volleyReq.setTag(req.getTag());
        volleyReq.setShouldCache(false);
        volleyReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 30, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(volleyReq);
    }

    /**
     * Constructs an URI
     * @param action
     * @return constructed URL
     */
    private String constructUrl(String action) {
        return Constant.SIT_URL + action;
    }
}

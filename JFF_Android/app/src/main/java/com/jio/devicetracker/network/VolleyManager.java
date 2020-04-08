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
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of volley networking library for api call
 */
public class VolleyManager extends StringRequest {
    private static Map<Integer, String> mStatusCodes;

    static {
        mStatusCodes = new HashMap();
        mStatusCodes.put(400, "");
        mStatusCodes.put(401, "");
        mStatusCodes.put(403, "");
        mStatusCodes.put(404, "");
        mStatusCodes.put(500, "");
    }

    private  Response.Listener<String> mSucessListener;
    private  Response.ErrorListener mErrorListener;
    private String mParams;
    private boolean mHandleError;

    @SuppressWarnings("PMD.UnusedFormalParameter")
    public VolleyManager(Context context, int method, String url, IRequest req) {
        super(method, url, null, null);
        mParams = req.getReqParams();
        this.mSucessListener = req.getSuccessListener();
        this.mErrorListener = req.getErrorListener();
        this.mHandleError = req.isHandleError();

    }

    @Override
    public Map<String, String> getHeaders(){
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json; charset=utf-8");
        return header;

    }


    @Override
    protected void deliverResponse(String response) {
        //CLog.v("VM deliver response");
        this.mSucessListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] val = new byte[0];
        try {
            val = mParams.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return val;
    }


    @Override
    public void deliverError(VolleyError error) {

        if (error == null || error.networkResponse == null) {
            // displayAlert(mContext.getString(R.string.req_error));
            return;
        }
        if (mHandleError) {
            mErrorListener.onErrorResponse(error);
            return;
        }
        if (mStatusCodes.containsKey(error.networkResponse.statusCode)) {
            //displayAlert(mStatusCodes.get(error.networkResponse.statusCode));
            return;
        }


    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        String json = null;
        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
    }
}

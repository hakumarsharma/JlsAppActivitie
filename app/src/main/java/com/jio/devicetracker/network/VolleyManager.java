// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jio.devicetracker.database.pojo.FMSHeader;
import com.jio.devicetracker.jiotoken.JiotokenHandler;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.DashboardActivity;
import com.jio.devicetracker.view.RegistrationActivity;


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
    private Context context = null;

    static {
        mStatusCodes = new HashMap();
        //TODO: Add appropriate error messages
        mStatusCodes.put(400, ""); // Unauthorized [login failed]
        mStatusCodes.put(401, ""); // Bad Request
        mStatusCodes.put(403, ""); // forbidden [restricted access]
        mStatusCodes.put(404, ""); //not found [webservice does not exist]
        mStatusCodes.put(500, ""); //internal server error
    }

    private  Response.Listener<String> mSucessListener;
    private  Response.ErrorListener mErrorListener;
    private String mParams;
    //private UIUtils mUtil;
    private boolean mHandleError;

    public VolleyManager(Context context, int method, String url, IRequest req) {
        super(method, url, null, null);
        mParams = req.getReqParams();
        this.context = context;
        /*CLog.i("AttachedmParams " + mParams+"::::"+url);
        CLog.i("Is valid json:" + isJSONValid(mParams));*/
        this.mSucessListener = req.getSuccessListener();
        this.mErrorListener = req.getErrorListener();
        this.mHandleError = req.isHandleError();

    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if(RegistrationActivity.isFMSFlow == false) {
            Map<String, String> header = new HashMap<String, String>();
            header.put("Content-Type", "application/json; charset=utf-8");
            return header;
        }
        else {
            FMSHeader fmsHeader = new FMSHeader();
            fmsHeader.setReqId(DashboardActivity.trackeeIMEI);
            fmsHeader.setTrkId(Util.getInstance().getIMEI(context));
            fmsHeader.setCrmId(JiotokenHandler.crmId);
            fmsHeader.setSesId(new String[]{Util.getInstance().getSessionId()});
            fmsHeader.setSesTyp(2);
            fmsHeader.setSvc("svckm");
            fmsHeader.setMode(2);
            Map<String, String> header = new HashMap<>();
            header.put("authorization", "Bearer "+ JiotokenHandler.ssoToken);
            Gson gson = new Gson();
            header.put("tracker-Info", gson.toJson(fmsHeader));
            return header;
        }
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
       /* Log.v("VM error");
        Log.v(error.getMessage());
        CLog.v(error.getLocalizedMessage());*/
        // UIUtils.getInstance().hideProgressDialog();
        if (error == null || error.networkResponse == null) {
            //  TODO Unknown error Show Alert dialog

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


    /* private void displayAlert(String msg) {
         if (UIUtils.getInstance().isNetworkAvailable(mContext)) {
             UIUtils.getInstance().showAlertDialog(mContext, msg, null); // TODO: Display netwrok related msg here
         } else {
             UIUtils.getInstance().showAlertDialog(mContext, msg, null);
         }
     }
 */
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

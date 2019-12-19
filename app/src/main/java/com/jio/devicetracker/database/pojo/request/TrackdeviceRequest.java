// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;


public class TrackdeviceRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private TrackerdeviceData trackerData;

    public TrackdeviceRequest(Response.Listener mListner,Response.ErrorListener errorListener,String token,TrackerdeviceData data)
    {
        this.sucessListener = mListner;
        this.errorListener = errorListener;
        this.userToken =token;
        this.trackerData = data;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(trackerData);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return "/accounts/api/devicestatuses/search?skip=0&limit=20&tsp=1572443375692&ugs_token="+userToken+"";
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    @Override
    public Response.Listener<String> getSuccessListener() {
        return sucessListener;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public boolean isHandleError() {
        return true;
    }
}

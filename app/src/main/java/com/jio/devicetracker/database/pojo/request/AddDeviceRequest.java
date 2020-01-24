// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;

public class AddDeviceRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private String id;
    private AddDeviceData trackerData;

    public AddDeviceRequest(Response.Listener mListner, Response.ErrorListener errorListener, String token, String id, AddDeviceData data)
    {
        this.sucessListener = mListner;
        this.errorListener = errorListener;
        this.userToken = token;
        this.id = id;
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
        return  "/accounts/api/users/"+id+"/devices/verifyandassign?ugs_token="+userToken;
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

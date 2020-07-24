package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.SOSData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class CreateSOSContactRequest implements IRequest {

    private Response.Listener successListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private String deviceId;
    private SOSData sosData;

    public CreateSOSContactRequest(Response.Listener successListener, Response.ErrorListener errorListener, SOSData sosData, String userToken, String deviceId) {
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.userToken = userToken;
        this.deviceId = deviceId;
        this.sosData = sosData;
    }

    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(sosData);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return Constant.DELETE_DEVICE_URL + deviceId + Constant.SOS_URL + userToken;
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    @Override
    public Response.Listener<String> getSuccessListener() {
        return successListener;
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

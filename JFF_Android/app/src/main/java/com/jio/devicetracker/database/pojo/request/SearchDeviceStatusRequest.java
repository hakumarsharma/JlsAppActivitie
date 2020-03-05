// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/*********************** Search Device Status request handler to get lat and long *************************/
public class SearchDeviceStatusRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private SearchDeviceStatusData searchDeviceStatusData;

    public SearchDeviceStatusRequest(Response.Listener mListner, Response.ErrorListener errorListener, String token, SearchDeviceStatusData data)
    {
        this.sucessListener = mListner;
        this.errorListener = errorListener;
        this.userToken = token;
        this.searchDeviceStatusData = data;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(searchDeviceStatusData);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return Constant.SEARCH_DEVICE_STATUS+userToken;
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

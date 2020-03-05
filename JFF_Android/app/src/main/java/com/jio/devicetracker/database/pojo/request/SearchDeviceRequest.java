// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.SearchDevice;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import static com.android.volley.Request.Method.POST;

/**
 * Implementation of search device api .
 */
public class SearchDeviceRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private SearchDevice searchDeviceData;

    public SearchDeviceRequest(Response.Listener mListner, Response.ErrorListener errorListener, String token, SearchDevice data)
    {
        this.sucessListener = mListner;
        this.errorListener = errorListener;
        this.userToken = token;
        this.searchDeviceData = data;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(searchDeviceData);
    }

    @Override
    public int getMethod() {
        return POST;
    }

    @Override
    public String getAction() {
        return Constant.SEARCH_DEVICE_URL +userToken;
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

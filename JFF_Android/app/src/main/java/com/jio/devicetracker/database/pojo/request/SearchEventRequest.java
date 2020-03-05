// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;


public class SearchEventRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private MultipleselectData.SearchEventData searchEventData;

    public SearchEventRequest(Response.Listener mListner, Response.ErrorListener errorListener, String token, MultipleselectData.SearchEventData data)
    {
        this.sucessListener = mListner;
        this.errorListener = errorListener;
        this.userToken = token;
        this.searchEventData = data;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(searchEventData);
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getAction() {
        return Constant.SEARCH_EVENT_REQUEST+userToken;
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

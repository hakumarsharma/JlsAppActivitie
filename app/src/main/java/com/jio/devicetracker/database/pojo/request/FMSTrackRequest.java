package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.FMSAPIData;
import com.jio.devicetracker.database.pojo.FMSHeader;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;

public class FMSTrackRequest implements IRequest {
    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private FMSAPIData user;

    public FMSTrackRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, FMSAPIData object)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.user = object;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(user);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return "/cats-servces/v2.0/api/live/location";
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

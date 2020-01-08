package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.ForgetPassToken;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;

public class ForgetpasswordTokenRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private ForgetPassToken data;

    public ForgetpasswordTokenRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, ForgetPassToken object)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.data = object;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(data);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return "/ugs/api/user/forgotpassword";
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
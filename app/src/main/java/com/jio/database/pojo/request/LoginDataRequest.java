package com.jio.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.database.pojo.Userdata;
import com.jio.network.IRequest;
import com.jio.util.Util;

public class LoginDataRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private Userdata user;

    public LoginDataRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, Userdata object)
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
        return "/accounts/api/users/login";
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
        return false;
    }
}

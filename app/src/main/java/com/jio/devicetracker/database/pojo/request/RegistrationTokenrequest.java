package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;

import static com.android.volley.Request.Method.POST;

public class RegistrationTokenrequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private RegisterRequestData data;

    public RegistrationTokenrequest(Response.Listener sucessListener, Response.ErrorListener errorListener, RegisterRequestData data)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.data = data;

    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(data);
    }

    @Override
    public int getMethod() {
        return POST;
    }

    @Override
    public String getAction() {
        return "/accounts/api/users/tokens";
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

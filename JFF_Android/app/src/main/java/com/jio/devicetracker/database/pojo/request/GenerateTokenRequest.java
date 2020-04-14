package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.GenerateTokenData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import static com.android.volley.Request.Method.POST;

public class GenerateTokenRequest implements IRequest {
    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private GenerateTokenData data;

    public GenerateTokenRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, GenerateTokenData data) {
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
        return Constant.GENERATE_TOKEN_REQUEST_URL;
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


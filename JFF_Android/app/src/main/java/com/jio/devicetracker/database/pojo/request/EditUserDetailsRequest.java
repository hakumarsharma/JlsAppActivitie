package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.EditMemberDetailsData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class EditUserDetailsRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String userId;
    private EditMemberDetailsData data;
    private long epochTime;

    public EditUserDetailsRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, EditMemberDetailsData data, long epochTime, String userId)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.epochTime = epochTime;
        this.userId = userId;
        this.data = data;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(data);
    }

    @Override
    public int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public String getAction() {
        return Constant.ACCOUNTS_API_USER_URL + userId + Constant.EDIT_USER_DETAIL_URL;
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

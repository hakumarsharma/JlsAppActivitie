package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class GetUserDevicesListRequest implements IRequest {
    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private SearchDeviceStatusData assignedUser;

    public GetUserDevicesListRequest(Response.Listener sucessListener, Response.ErrorListener errorListener,SearchDeviceStatusData assignedUser)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.assignedUser = assignedUser;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(assignedUser);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return Constant.GET_DEVICES_LIST;
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

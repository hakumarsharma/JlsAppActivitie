/*************************************************************
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class ExitRemovedGroupRequest implements IRequest {
    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private String groupId;
    private String userId;
    private ExitRemovedGroupData exitRemovedGroupData;

    public ExitRemovedGroupRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, ExitRemovedGroupData exitRemovedGroupData, String groupId, String userId)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.groupId = groupId;
        this.userId = userId;
        this.exitRemovedGroupData = exitRemovedGroupData;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(exitRemovedGroupData);
    }

    @Override
    public int getMethod() {
        return Request.Method.DELETE;
    }

    @Override
    public String getAction() {
        return Constant.ACCOUNTS_API_USER_URL + userId + Constant.SESSION_GROUPS_URL + groupId
                + Constant.SESSION_GROUP_CONSENTS_URL1 + Constant.STATUS_URL;
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


package com.jio.network;

import com.android.volley.Response;

/**
 * Created by M1028309 on 4/20/2015.
 */
public interface IRequest {

    String getReqParams();


    int getMethod();


    String getAction();


    Response.ErrorListener getErrorListener();


    Response.Listener<String> getSuccessListener();


    String getTag();

    boolean isHandleError();
}


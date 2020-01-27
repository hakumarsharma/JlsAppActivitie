// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.network;

import com.android.volley.Response;

/**
 * Implementation of interface to provide communication channel between UI and networking library .
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


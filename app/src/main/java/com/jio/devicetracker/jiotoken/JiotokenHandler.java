// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.jiotoken;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jio.iot.sso.JiotAuthInfo;
import com.jio.iot.sso.JiotSSOConstants;

public class JiotokenHandler extends Handler {

    private String jToken = null;
    public static String ssoToken = null;
    public static String crmId = null;
    private String cookie = null;
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case JiotSSOConstants.JIOT_GET_SSO_RESP_MSG_CODE:
                JiotAuthInfo authInfo = (JiotAuthInfo) msg.obj;
                if (null != authInfo) {
                    jToken = authInfo.getAuthToken();
                    ssoToken = authInfo.getSsoToken();
                    crmId = authInfo.getCrmId();
                    cookie = authInfo.getCookie();
                } else {
                    Bundle b = msg.getData();
                }
                break;
            case  JiotSSOConstants.JIOT_SSO_ERROR_RESP_MSG_CODE:
                Bundle b = msg.getData();
                break;
            default:
                Log.i("TAG", "Token is not generated");
        }
    }
}

// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.jiotoken;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jio.iot.sso.JiotAuthInfo;
import com.jio.iot.sso.JiotSSOConstants;

public class JiotokenHandler extends Handler {

    public static String ssoToken = null;
    public static String crmId = null;
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case JiotSSOConstants.JIOT_GET_SSO_RESP_MSG_CODE:
                JiotAuthInfo authInfo = (JiotAuthInfo) msg.obj;
                if (null != authInfo) {
                    ssoToken = authInfo.getSsoToken();
                    crmId = authInfo.getCrmId();
                }
                Log.d("SSO Token --> ", ssoToken);
                Log.d("Crm Id --> ", crmId);
                break;
            default:
                Log.i("TAG", "Token is not generated");
                break;
        }
    }
}

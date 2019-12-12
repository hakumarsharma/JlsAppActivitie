package com.jio.jiotoken;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jio.iot.sso.JiotAuthInfo;
import com.jio.iot.sso.JiotSSOConstants;

public class JiotokenHandler extends Handler {

    String jToken,ssoToken,crmId,cookie;
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
                    int httpCode = b.getInt("HttpCode");
                    int errCode = b.getInt("ErrorCode");
                }
                break;
            case  JiotSSOConstants.JIOT_SSO_ERROR_RESP_MSG_CODE:
                Bundle b = msg.getData();
                int httpCode = b.getInt("HttpCode");
                int errCode = b.getInt("ErrorCode");
                break;
        }
    }
}

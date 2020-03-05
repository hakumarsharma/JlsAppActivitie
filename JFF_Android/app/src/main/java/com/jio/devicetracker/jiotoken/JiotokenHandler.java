/*************************************************************
 *
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

package com.jio.devicetracker.jiotoken;


import android.os.Handler;
import android.os.Message;

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
                break;
            default:
                // Something wrong
                break;
        }
    }
}

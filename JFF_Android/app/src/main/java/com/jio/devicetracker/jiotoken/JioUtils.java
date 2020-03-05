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

import android.content.Context;

import com.jio.devicetracker.util.Constant;
import com.jio.iot.sso.JiotAuthenticationManager;
import com.jio.iot.sso.JiotSSOApp;

public final class JioUtils {
    private static JiotSSOApp jioSSo;
    private static JiotAuthenticationManager mJioAuth;

    private JioUtils() {

    }

    public static void getSSOIdmaToken(Context mContext)
    {
        jioSSo = new JiotSSOApp(Constant.REG_ID,Constant.REG_KEY,Constant.REG_SVC);
        mJioAuth = JiotAuthenticationManager.getAuhtenticateManager(mContext);
        mJioAuth.getSSOToken(Constant.App_Name, jioSSo, new JiotokenHandler());
    }
}

package com.jio.jiotoken;

import android.content.Context;
import android.util.Log;

import com.jio.iot.sso.JiotAuthenticationManager;
import com.jio.iot.sso.JiotSSOApp;
import com.jio.util.Constant;

public class JioutilsToken {
    private static JiotSSOApp jioSSo;
    private static JiotAuthenticationManager mJioAuth;

    public static void getSSOIdmaToken(Context mContext)
    {
        jioSSo = new JiotSSOApp(Constant.REG_ID,Constant.REG_KEY,Constant.REG_SVC);
        mJioAuth = JiotAuthenticationManager.getAuhtenticateManager(mContext);
        mJioAuth.getSSOToken(Constant.App_Name,jioSSo,new JiotokenHandler());
    }
}

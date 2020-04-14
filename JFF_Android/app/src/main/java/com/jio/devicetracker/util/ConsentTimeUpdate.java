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

package com.jio.devicetracker.util;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.ConsentTimeupdateData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of worker class to update consent time in background
 */
public class ConsentTimeUpdate extends Worker {

    private DBManager mDbmanager;

    public ConsentTimeUpdate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mDbmanager = new DBManager(getApplicationContext());

    }

    // Update the consent time in background
    @NonNull
    @Override
    public Result doWork() {
        boolean expiryTokenFlag = false;
        List<ConsentTimeupdateData> mList = new ArrayList<>();
        long unixTime = System.currentTimeMillis() / 1000L;
        AdminLoginData adminData = mDbmanager.getAdminLoginDetail();
        if (adminData != null && adminData.getTokenExpirytime() != null && unixTime >= Long.valueOf(adminData.getTokenExpirytime())) {
            expiryTokenFlag = true;
        }

        List<AddedDeviceData> data = mDbmanager.getConsentTime();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMin = Calendar.getInstance().get(Calendar.MINUTE);
        for (AddedDeviceData addedData : data) {
            ConsentTimeupdateData consentData = new ConsentTimeupdateData();
            String consenttime = addedData.getConsentTime();
            int consentApprovaltime = addedData.getConsentApprovalTime();
            if (!"".equals(consenttime)) {
                String[] split = consenttime.split(":");
                String consentHour = split[0];
                String consentMintue = split[1];
                int diff = currentMin - Integer.valueOf(consentMintue);
                if (consentHour.equals(String.valueOf(currentHour)) && diff > consentApprovaltime || !consentHour.equals(String.valueOf(currentHour)) && diff > 0) {
                    consentData.setConsentStatus(Constant.CONSENT_NOT_SENT);
                    consentData.setConsentTime("00:00");
                    consentData.setPhoneNumber(addedData.getPhoneNumber());
                    mList.add(consentData);

                } else if (currentHour != Integer.valueOf(consentHour) && diff < 0) {
                    int diff1 = 60 + currentMin - Integer.valueOf(consentMintue);
                    if (diff1 > consentApprovaltime) {
                        consentData.setConsentStatus(Constant.CONSENT_NOT_SENT);
                        consentData.setConsentTime("00:00");
                        consentData.setPhoneNumber(addedData.getPhoneNumber());
                        mList.add(consentData);
                    }
                }
            }
        }

        mDbmanager.updateConsentTimeandStatus(mList);
        Intent intent = new Intent();
        intent.setAction("custom-intent");
        intent.putExtra("TokenFlag", expiryTokenFlag);
        getApplicationContext().sendBroadcast(intent);
        return Result.success();
    }

}

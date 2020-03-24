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
package com.jio.devicetracker.database.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.ConsentTimeupdateData;
import com.jio.devicetracker.database.pojo.EditProfileData;
import com.jio.devicetracker.database.pojo.GroupmemberListData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DataBase manager class to manage all operation like insert,delete,update and fetch in database.
 */
public class DBManager {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    public static List<String> phoneNumner;

    public DBManager(Context context) {
        mDBHelper = new DatabaseHelper(context);
    }

    public long insertInBorqsDB(HomeActivityListData deviceData, String email) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.EMAIL, email);
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        if (deviceData.getConsentStaus() != null) {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, deviceData.getConsentStaus());
        } else {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        contentValue.put(DatabaseHelper.CONSENT_TIME, "");
        contentValue.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, 1234);
        return mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);

    }

    public long insertGroupDataInBorqsDeviceDB(GroupmemberListData deviceData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getNumber());
        if(deviceData.getConsentStatus()!=null){
            contentValue.put(DatabaseHelper.CONSENT_STATUS,deviceData.getConsentStatus());
        }else {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        contentValue.put(DatabaseHelper.CONSENT_TIME, "");
        contentValue.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT,1234);
        contentValue.put(DatabaseHelper.GROUP_NAME,deviceData.getGroupName());
        return  mDatabase.insert(DatabaseHelper.TABLE_NAME_DEVICE, null, contentValue);
    }
    public long insertInBorqsDeviceDB(HomeActivityListData deviceData, String email) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.EMAIL, email);
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        if (deviceData.getConsentStaus() != null) {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, deviceData.getConsentStaus());
        } else {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        contentValue.put(DatabaseHelper.CONSENT_TIME, "");
        contentValue.put(DatabaseHelper.LAT, deviceData.getLat());
        contentValue.put(DatabaseHelper.LON, deviceData.getLng());
        contentValue.put(DatabaseHelper.IS_GROUP_MEMBER, deviceData.isGroupMember() ? 1 : 0);
        contentValue.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, 1234);
        return mDatabase.insert(DatabaseHelper.TABLE_NAME_DEVICE, null, contentValue);

    }

    public void insertInBorqsDB(List<HomeActivityListData> deviceData, String email) {
        List<HomeActivityListData> homeData = new ArrayList<>();
        HomeActivityListData homeActivityListData = new HomeActivityListData();
        homeActivityListData.setName("Umapathi");
        homeActivityListData.setPhoneNumber("9019020584");
        homeActivityListData.setLat("12.9140667");
        homeActivityListData.setLng("77.6650655");

        HomeActivityListData homeActivityListData1 = new HomeActivityListData();
        homeActivityListData1.setName("Teja sree");
        homeActivityListData1.setPhoneNumber("8088422893");
        homeActivityListData1.setLat("12.9950641");
        homeActivityListData1.setLng("77.6810009");


        HomeActivityListData homeActivityListData2 = new HomeActivityListData();
        homeActivityListData2.setName("sree");
        homeActivityListData2.setPhoneNumber("9019930384");
        homeActivityListData2.setLat("12.8950641");
        homeActivityListData2.setLng("77.5810009");

        HomeActivityListData homeActivityListData3 = new HomeActivityListData();
        homeActivityListData3.setName("Harish");
        homeActivityListData3.setPhoneNumber("9019930384");
        homeActivityListData3.setLat("12.9050641");
        homeActivityListData3.setLng("77.6310009");

        HomeActivityListData homeActivityListData4 = new HomeActivityListData();
        homeActivityListData4.setName("Ashish");
        homeActivityListData4.setPhoneNumber("9019020587");
        homeActivityListData4.setLat("12.6950641");
        homeActivityListData4.setLng("77.7910009");

        homeData.add(homeActivityListData);
        homeData.add(homeActivityListData1);
        homeData.add(homeActivityListData2);
        homeData.add(homeActivityListData3);
        homeData.add(homeActivityListData4);


        mDatabase = mDBHelper.getWritableDatabase();
        for (HomeActivityListData addData : homeData) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.NAME, addData.getName());
            contentValue.put(DatabaseHelper.EMAIL, email);
            contentValue.put(DatabaseHelper.IMEI_NUM, addData.getImeiNumber());
            contentValue.put(DatabaseHelper.DEVICE_NUM, addData.getPhoneNumber());
            Log.d("DB", "Value of consentstatus" + addData.getConsentStaus());
            if (addData.getConsentStaus() != null) {
                contentValue.put(DatabaseHelper.CONSENT_STATUS, addData.getConsentStaus());
            } else {
                contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
            }
            //contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
            contentValue.put(DatabaseHelper.CONSENT_TIME, "");
            contentValue.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, 1234);
            contentValue.put(DatabaseHelper.LAT, addData.getLat());
            contentValue.put(DatabaseHelper.LON, addData.getLng());
            Log.d(addData.getImeiNumber(), "insertInBorqsDB");
            mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);

        }
    }

    public long insertAdminData(RegisterData data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, data.getName());
        contentValue.put(DatabaseHelper.EMAIL, data.getEmail());
        contentValue.put(DatabaseHelper.DEVICE_NUM, data.getPhoneNumber());
        contentValue.put(DatabaseHelper.DOB, data.getDob());
        contentValue.put(DatabaseHelper.PASS, data.getPassword());
        contentValue.put(DatabaseHelper.USER_ID, "");
        return mDatabase.insert(DatabaseHelper.TABLE_NAME_USER, null, contentValue);
    }

    public long insertLoginData(LogindetailResponse data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.USER_TOKEN, data.getUgsToken());
        contentValue.put(DatabaseHelper.USER_ID, data.getUser().getId());
        contentValue.put(DatabaseHelper.TOKEN_EXPIRY_TIME, data.getUgsTokenExpiry());
        contentValue.put(DatabaseHelper.EMAIL, data.getUser().getEmail());
        contentValue.put(DatabaseHelper.USER_NAME, data.getUser().getName());
        return mDatabase.insert(DatabaseHelper.TABLE_USER_LOGIN, null, contentValue);
    }

    public long insertInFMSDB(AddedDeviceData deviceData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.RELATION, deviceData.getRelation());
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        if (deviceData.getLat() != null && deviceData.getLng() != null) {
            contentValue.put(DatabaseHelper.LAT, Double.parseDouble(deviceData.getLat()));
            contentValue.put(DatabaseHelper.LON, Double.parseDouble(deviceData.getLng()));
        }
        return mDatabase.insert(DatabaseHelper.TABLE_NAME_FMS, null, contentValue);
    }

    public List<AddedDeviceData> getAlldataFromFMS() {
        List<AddedDeviceData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String selectquery = "select * from " + DatabaseHelper.TABLE_NAME_FMS;
        Cursor cursor = mDatabase.rawQuery(selectquery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AddedDeviceData data = new AddedDeviceData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)));
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                mlist.add(data);
            }
            cursor.close();
        }
        return mlist;
    }

    public List<HomeActivityListData> getAlldata(String email) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        if (email != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_STATUS, DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.IMEI_NUM};
            String[] arg = {email};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.EMAIL + " = ? ", arg, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    HomeActivityListData data = new HomeActivityListData();
                    data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)).trim());
                    data.setLat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAT)));
                    data.setLng(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON)));
                    data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                    mlist.add(data);
                }
                cursor.close();
            }
        }
        return mlist;
    }

    public List<HomeActivityListData> getAllDevicedata(String email) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        if (email != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_STATUS, DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.IMEI_NUM, DatabaseHelper.IS_GROUP_MEMBER};
            String[] arg = {email};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_DEVICE, column, DatabaseHelper.EMAIL + " = ? ", arg, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    HomeActivityListData data = new HomeActivityListData();
                    data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)).trim());
                    data.setLat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAT)));
                    data.setLng(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON)));
                    data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IS_GROUP_MEMBER)) > 0) {
                        data.setGroupMember(true);
                    } else {
                        data.setGroupMember(false);
                    }
                    mlist.add(data);
                }
                cursor.close();
            }
        }
        return mlist;
    }

    public void updateBorqsData(List<TrackerdeviceResponse.Data> data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        for (TrackerdeviceResponse.Data trackerDeviceResponseData : data) {
            String[] arg = new String[]{trackerDeviceResponseData.getmDevice().getPhoneNumber()};
            if (trackerDeviceResponseData.getEvent() != null) {
                contentValue.put(DatabaseHelper.LAT, Double.parseDouble(trackerDeviceResponseData.getEvent().getLocation().getLatLocation().getLatitu()));
                contentValue.put(DatabaseHelper.LON, Double.parseDouble(trackerDeviceResponseData.getEvent().getLocation().getLatLocation().getLongni()));
                mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, contentValue, DatabaseHelper.DEVICE_NUM + "= ?", arg);
            }
        }
    }

    public void updateLogoutData() {
        mDatabase = mDBHelper.getWritableDatabase();
        int row = mDatabase.delete(DatabaseHelper.TABLE_USER_LOGIN, null, null);
        if (row == 1) {
            Log.d("Database Info --> ", "Logout data is updated");
        }
    }

    public void updateConsentInBors(String phoneNumber, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    public void updateConsentInDeviceBors(String phoneNumber, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_DEVICE, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    public void updateConsentInFMS(String phoneNumber, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);

    }

    public void updatependingConsent(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, "Pending");
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);

    }

    public void updatependingConsentFMS(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, "Pending");
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);


    }

    public void updateProfile(String priviousNumber, String name, String newNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.NAME, name);
        //values.put(DatabaseHelper.IMEI_NUM, imei);
        if (!priviousNumber.equals(newNumber)) {
            values.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_DEVICE, values, DatabaseHelper.DEVICE_NUM + "= '" + priviousNumber + "';", null);

    }

    public void updateProfileFMS(String priviousNumber, String name, String newNumber, String imei) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.NAME, name);
        //values.put(DatabaseHelper.RELATION, relation);
        values.put(DatabaseHelper.IMEI_NUM, imei);
        if (!priviousNumber.equals(newNumber)) {
            values.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= " + priviousNumber, null);
    }


    public void deleteSelectedDataformFMS(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_NAME_FMS, DatabaseHelper.DEVICE_NUM + "=" + phoneNumber, null);
    }

    public void deleteSelectedData(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_NAME_DEVICE, DatabaseHelper.DEVICE_NUM + "= '" + phoneNumber + "';", null);
    }


    public Map<Double, Double> getLatLongForMap(List<MultipleselectData> mList, String phoneNumber) {
        Map<Double, Double> latLong = new HashMap<>();
        phoneNumner = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        for (MultipleselectData multipleselectData : mList) {
            String[] column = {DatabaseHelper.LAT, DatabaseHelper.LON};
            String[] arg = {phoneNumber, "yes jiotracker"};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.DEVICE_NUM + " =? AND " + DatabaseHelper.CONSENT_STATUS + "=?", arg, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    phoneNumner.add(multipleselectData.getPhone());
                    latLong.put(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LON)));
                }
                cursor.close();
            }
        }
        return latLong;
    }

    public EditProfileData getUserdataForEdit(String phoneNumber) {
        EditProfileData data = null;
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.DEVICE_NUM, DatabaseHelper.NAME, DatabaseHelper.IMEI_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.DEVICE_NUM + "=" + phoneNumber, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data = new EditProfileData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                // data.setRelation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATION)));
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
            }
            cursor.close();
        }
        return data;
    }

    public EditProfileData getUserdataForEditFMS(String phoneNumber) {
        EditProfileData data = null;
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.DEVICE_NUM, DatabaseHelper.RELATION, DatabaseHelper.NAME, DatabaseHelper.IMEI_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_FMS, column, DatabaseHelper.DEVICE_NUM + "=" + phoneNumber, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data = new EditProfileData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setRelation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATION)));
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
            }
            cursor.close();
        }
        return data;
    }

    public String getAdminDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        String userName = "";
        String[] column = {DatabaseHelper.NAME};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_USER_LOGIN, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            }
            cursor.close();
        }
        return userName;
    }

    public AdminLoginData getAdminLoginDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        AdminLoginData adminData = null;
        String[] column = {DatabaseHelper.EMAIL, DatabaseHelper.USER_TOKEN, DatabaseHelper.USER_ID, DatabaseHelper.TOKEN_EXPIRY_TIME, DatabaseHelper.USER_NAME};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_USER_LOGIN, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                adminData = new AdminLoginData();
                adminData.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMAIL)));
                adminData.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                adminData.setUserToken(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_TOKEN)));
                adminData.setTokenExpirytime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOKEN_EXPIRY_TIME)));
                adminData.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_NAME)));
            }
            cursor.close();
        }
        return adminData;
    }

    public String getAdminphoneNumber() {
        mDatabase = mDBHelper.getWritableDatabase();
        String phoneNumber = "";
        String[] column = {DatabaseHelper.DEVICE_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_USER, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM));
            }
            cursor.close();
        }
        return phoneNumber;
    }

    public String getConsentStatusBorqs(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        String consentStatus = "";
        String[] column = {DatabaseHelper.CONSENT_STATUS};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.DEVICE_NUM + " = " + phoneNumber, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                consentStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS));
            }
            cursor.close();
        }
        return consentStatus;
    }

    public RegisterData getAdminRegistrationDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        RegisterData data = new RegisterData();
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.EMAIL, DatabaseHelper.DEVICE_NUM, DatabaseHelper.PASS, DatabaseHelper.DOB};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_USER, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PASS)));
                data.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMAIL)));
                data.setDob(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOB)));
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
            }
            cursor.close();
        }
        return data;
    }

    public void updateConsentTime(String phoneNumber, String consentTime, int approvalTime) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_TIME, consentTime);
        values.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, approvalTime);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    public List<AddedDeviceData> getConsentTime() {
        List<AddedDeviceData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();

        String[] column = {DatabaseHelper.CONSENT_TIME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AddedDeviceData data = new AddedDeviceData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setConsentTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_TIME)));
                data.setConsentApprovalTime(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT)));
                mlist.add(data);
            }
            cursor.close();
        }

        return mlist;
    }

    public void updateConsentTimeandStatus(List<ConsentTimeupdateData> mList) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = null;
        for (ConsentTimeupdateData consentData : mList) {
            values = new ContentValues();
            values.put(DatabaseHelper.CONSENT_TIME, consentData.getConsentTime());
            values.put(DatabaseHelper.CONSENT_STATUS, consentData.getConsentStatus());
            mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + consentData.getPhoneNumber(), null);
        }
    }

    public List<HomeActivityListData> getGroupdata(String groupName) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_DEVICE + " WHERE " + DatabaseHelper.GROUP_NAME + " = ?" ;
        if (groupName != null) {
            String[] column = {DatabaseHelper.DEVICE_NUM};
            String[] arg = {groupName};
            //Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_DEVICE, column, DatabaseHelper.GROUP_NAME + " = "  + groupName  , null, null, null, null);
            Cursor cursor = mDatabase.rawQuery(query, new String[] { groupName });
            if (cursor != null)  {
                while (cursor.moveToNext()) {
                    HomeActivityListData data = new HomeActivityListData();
                    data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    mlist.add(data);
                }
                cursor.close();
            }
        }
        return mlist;
    }

    public List<MultipleselectData> getGroupLatLongdata(String groupName) {
        List<MultipleselectData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_DEVICE + " WHERE " + DatabaseHelper.GROUP_NAME + " = ?  AND "+ DatabaseHelper.CONSENT_STATUS + " = ?" ;
        if (groupName != null) {
            String[] column = {DatabaseHelper.DEVICE_NUM};
            String[] arg = {groupName};
            //Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_DEVICE, column, DatabaseHelper.GROUP_NAME + " = "  + groupName  , null, null, null, null);
            Cursor cursor = mDatabase.rawQuery(query, new String[] { groupName, Constant.CONSENT_STATUS_MSG});
            if (cursor != null)  {
                while (cursor.moveToNext()) {
                    MultipleselectData data = new MultipleselectData();
                    data.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setLat(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT))));
                    data.setLng(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LON))));
                    mlist.add(data);
                }
                cursor.close();
            }
        }
        return mlist;
    }
}

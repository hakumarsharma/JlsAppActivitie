// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.EditProfileData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    public static List<String> phoneNumner;

    public DBManager(Context context) {
        mDBHelper = new DatabaseHelper(context);
    }

    public long insertInBorqsDB(AddedDeviceData deviceData, String email) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.EMAIL, email);
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        contentValue.put(DatabaseHelper.LAT, Double.parseDouble(deviceData.getLat()));
        contentValue.put(DatabaseHelper.LON, Double.parseDouble(deviceData.getLng()));
        return  mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);

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

    public List<AddedDeviceData> getAlldata(String email) {
        List<AddedDeviceData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        if (email != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM,DatabaseHelper.CONSENT_STATUS, DatabaseHelper.LAT, DatabaseHelper.LON};
            String[] arg = {email};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.EMAIL + " = ? " , arg, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    AddedDeviceData data = new AddedDeviceData();
                    data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)).trim());
                    data.setLat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAT)));
                    data.setLng(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON)));
                    mlist.add(data);
                }
                cursor.close();
            }
        }
        return mlist;
    }

    public void updateBorqsData(List<TrackerdeviceResponse.Data> data){
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        for(TrackerdeviceResponse.Data trackerDeviceResponseData : data) {
            String[] arg = new String[]{trackerDeviceResponseData.getmDevice().getPhoneNumber()};
            if(trackerDeviceResponseData.getEvent() != null) {
                contentValue.put(DatabaseHelper.LAT, Double.parseDouble(trackerDeviceResponseData.getEvent().getLocation().getLatLocation().getLatitu()));
                contentValue.put(DatabaseHelper.LON, Double.parseDouble(trackerDeviceResponseData.getEvent().getLocation().getLatLocation().getLongni()));
                mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, contentValue, DatabaseHelper.DEVICE_NUM + "= ?", arg);
            }
        }
    }

    public void updateLogoutData(){
        mDatabase = mDBHelper.getWritableDatabase();
        int row = mDatabase.delete(DatabaseHelper.TABLE_USER_LOGIN, null, null);
        if(row == 1){
            Log.d("Database Info --> ", "Logout data is updated");
        }
    }

    public void updateConsentInBors(String phoneNumber, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
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

    public void updateProfile(String priviousNumber, String name, String newNumber, String imei) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.NAME, name);
        values.put(DatabaseHelper.IMEI_NUM, imei);
        if (!priviousNumber.equals(newNumber)) {
            values.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + priviousNumber, null);

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
        mDatabase.delete(DatabaseHelper.TABLE_NAME_BORQS, DatabaseHelper.DEVICE_NUM + "=" + phoneNumber, null);
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
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_USER, column, null, null, null, null, null);
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
}
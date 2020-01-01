// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.EditProfileData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBManager {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    public static List<String> phoneNumner;

    public DBManager(Context context) {
        mContext = context;
        mDBHelper = new DatabaseHelper(context);
    }

    public long insertInBorqsDB(AddedDeviceData deviceData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.RELATION, deviceData.getRelation());
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        contentValue.put(DatabaseHelper.LAT, Double.parseDouble(deviceData.getLat()));
        contentValue.put(DatabaseHelper.LON, Double.parseDouble(deviceData.getLng()));
        long rowInserted = mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);

        return rowInserted;
    }

    public long insertAdminData(RegisterData data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, data.getName());
        contentValue.put(DatabaseHelper.EMAIL, data.getEmail());
        contentValue.put(DatabaseHelper.DEVICE_NUM, data.getPhoneNumber());
        contentValue.put(DatabaseHelper.DOB, data.getDob());
        contentValue.put(DatabaseHelper.PASS, data.getPassword());
        contentValue.put(DatabaseHelper.USER_ID,"");

        long rowInserted = mDatabase.insert(DatabaseHelper.TABLE_NAME_USER, null, contentValue);

        return rowInserted;
    }

    public long insertInFMSDB(AddedDeviceData deviceData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.RELATION, deviceData.getRelation());
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        if(deviceData.getLat() != null && deviceData.getLng() != null) {
            contentValue.put(DatabaseHelper.LAT, Double.parseDouble(deviceData.getLat()));
            contentValue.put(DatabaseHelper.LON, Double.parseDouble(deviceData.getLng()));
        }
        long rowInserted = mDatabase.insert(DatabaseHelper.TABLE_NAME_FMS, null, contentValue);
        return rowInserted;
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

        }
        return mlist;
    }

    public List<AddedDeviceData> getAlldata() {
        List<AddedDeviceData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String selectquery = "select * from " + DatabaseHelper.TABLE_NAME_BORQS;
        Cursor cursor = mDatabase.rawQuery(selectquery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AddedDeviceData data = new AddedDeviceData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setRelation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATION)));
                data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)));
                mlist.add(data);
            }

        }
        return mlist;
    }

    public void updateConsentInBors(List<String> phoneNumber) {
        String number = "";
        mDatabase = mDBHelper.getWritableDatabase();
        for (int i = 0; i < phoneNumber.size(); i++) {
            number = phoneNumber.get(i);
            String[] arg = new String[]{number};
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CONSENT_STATUS, "Yes");
            mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= ?", arg);

        }
    }

    public void updateConsentInBors(String phoneNumber,String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= "+phoneNumber, null);

    }

    public void updateConsentInFMS(String phoneNumber,String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= "+phoneNumber, null);

    }

    public void updatependingConsent(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, "Pending");
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= "+phoneNumber, null);

    }
    public void updatependingConsentFMS(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, "Pending");
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= "+phoneNumber, null);


    }

    public void updateProfile(String priviousNumber,String name,String newNumber,String relation,String imei ) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.NAME, name);
        values.put(DatabaseHelper.RELATION, relation);
        values.put(DatabaseHelper.IMEI_NUM, imei);
        if(!priviousNumber.equals(newNumber))
        {
            values.put(DatabaseHelper.CONSENT_STATUS,"Consent not sent");
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= "+priviousNumber, null);

    }

    public void updateProfileFMS(String priviousNumber,String name,String newNumber,String imei ) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.NAME, name);
        //values.put(DatabaseHelper.RELATION, relation);
        values.put(DatabaseHelper.IMEI_NUM, imei);
        if(!priviousNumber.equals(newNumber))
        {
            values.put(DatabaseHelper.CONSENT_STATUS,"Consent not sent");
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_FMS, values, DatabaseHelper.DEVICE_NUM + "= "+priviousNumber, null);

    }


    public void deleteSelectedDataformFMS(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_NAME_FMS, DatabaseHelper.DEVICE_NUM + "=" +phoneNumber, null);
    }

    public void deleteSelectedData(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_NAME_BORQS, DatabaseHelper.DEVICE_NUM + "=" +phoneNumber, null);
    }


    public HashMap<Double,Double> getLatLongForMap(List<MultipleselectData> mList) {
        HashMap<Double,Double> latLong = new HashMap<>();
        phoneNumner = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        for(int i=0;i<mList.size();i++) {
            String[] column = {DatabaseHelper.LAT, DatabaseHelper.LON};
            String [] arg = {mList.get(i).getPhone(),"Yes"};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column,DatabaseHelper.DEVICE_NUM +" =? AND " +DatabaseHelper.CONSENT_STATUS +"=?",arg,null,null,null );
            if(cursor != null)
            {
                while (cursor.moveToNext()) {
                    phoneNumner.add(mList.get(i).getPhone());
                    latLong.put(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)),cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LON)));
                }
            }
        }

        return latLong;

    }

    public EditProfileData getUserdataForEdit(String phoneNumber) {
        EditProfileData data = null;
        mDatabase = mDBHelper.getWritableDatabase();
        String [] column = {DatabaseHelper.DEVICE_NUM,DatabaseHelper.RELATION,DatabaseHelper.NAME,DatabaseHelper.IMEI_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS,column,DatabaseHelper.DEVICE_NUM +"="+phoneNumber,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data = new EditProfileData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setRelation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATION)));
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
            }

        }
        return data;
    }

    public EditProfileData getUserdataForEditFMS(String phoneNumber) {
        EditProfileData data = null;
        mDatabase = mDBHelper.getWritableDatabase();
        String [] column = {DatabaseHelper.DEVICE_NUM,DatabaseHelper.RELATION,DatabaseHelper.NAME,DatabaseHelper.IMEI_NUM};
        //String selectquery = "select " +DatabaseHelper.DEVICE_NUM +"  from " + DatabaseHelper.TABLE_NAME_BORQS+ " where "+ DatabaseHelper.DEVICE_NUM + " = " + phoneNumber;
        //Cursor cursor = mDatabase.rawQuery(selectquery, null);
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_FMS,column,DatabaseHelper.DEVICE_NUM +"="+phoneNumber,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data = new EditProfileData();
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setRelation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATION)));
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
            }

        }
        return data;
    }

    public String getAdminDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        String userName = "";
        String [] column = {DatabaseHelper.NAME};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_USER,column,null,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                 userName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            }

        }
        return userName;
    }

    public String getAdminphoneNumber() {
        mDatabase = mDBHelper.getWritableDatabase();
        String phoneNumber = "";
        String [] column = {DatabaseHelper.DEVICE_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_USER,column,null,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM));
            }

        }
        return phoneNumber;
    }

    public String getConsentStatusBorqs(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        String consentStatus = "";
        String [] column = {DatabaseHelper.CONSENT_STATUS};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS,column,DatabaseHelper.DEVICE_NUM +" = "+phoneNumber,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                consentStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS));
            }

        }
        return consentStatus;
    }




}

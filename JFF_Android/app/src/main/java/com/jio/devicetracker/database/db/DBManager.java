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

import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.ConsentTimeupdateData;
import com.jio.devicetracker.database.pojo.GetDeviceLocationData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.LoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DataBase manager class to manage all operation like insert,delete,update and fetch in database.
 */
public class DBManager {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;


    public DBManager(Context context) {
        mDBHelper = new DatabaseHelper(context);
    }

    /**
     * Insert data into the TABLE_NAME_BORQS table, It is a main table to store device data
     *
     * @param deviceData
     * @param email
     * @return long
     */
    public long insertInBorqsDB(HomeActivityListData deviceData, String email) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, deviceData.getName());
        contentValue.put(DatabaseHelper.EMAIL, email);
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceData.getPhoneNumber());
        contentValue.put(DatabaseHelper.DEVICE_TYPE, deviceData.getDeviceType());
        contentValue.put(DatabaseHelper.GROUP_NAME, deviceData.getGroupName());
        if (deviceData.getConsentStaus() != null) {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, deviceData.getConsentStaus());
        } else {
            contentValue.put(DatabaseHelper.CONSENT_STATUS, "Consent not sent");
        }
        contentValue.put(DatabaseHelper.CONSENT_TIME, "");
        contentValue.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, 1234);
        return mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);

    }

    /**
     * Insert data into the TABLE_NAME_BORQS table, It is a main table to store device data, stores by by taking value from list
     *
     * @param deviceData
     * @param email
     */
    public void insertInBorqsDB(List<HomeActivityListData> deviceData, String email) {
        mDatabase = mDBHelper.getWritableDatabase();
        for (HomeActivityListData addData : deviceData) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.NAME, addData.getName());
            contentValue.put(DatabaseHelper.EMAIL, email);
            contentValue.put(DatabaseHelper.IMEI_NUM, addData.getImeiNumber());
            contentValue.put(DatabaseHelper.DEVICE_NUM, addData.getPhoneNumber());
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
            contentValue.put(DatabaseHelper.DEVICE_ID, addData.getDeviceId());
            mDatabase.insert(DatabaseHelper.TABLE_NAME_BORQS, null, contentValue);
        }
    }

    /**
     * Inserts Login data into the login(TABLE_USER_LOGIN) table
     *
     * @param data
     * @return
     */
    public long insertLoginData(LogindetailResponse data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.USER_TOKEN, data.getUgsToken());
        contentValue.put(DatabaseHelper.USER_ID, data.getUser().getId());
        contentValue.put(DatabaseHelper.TOKEN_EXPIRY_TIME, data.getUgsTokenExpiry());
        contentValue.put(DatabaseHelper.EMAIL, data.getUser().getEmail());
        contentValue.put(DatabaseHelper.USER_NAME, LoginActivity.userName);
        contentValue.put(DatabaseHelper.PHONE_COUNTRY_CODE, data.getUser().getPhoneCountryCode());
        return mDatabase.insert(DatabaseHelper.TABLE_USER_LOGIN, null, contentValue);
    }

    /**
     * Returns all data from TABLE_NAME_BORQS table in the form of list
     *
     * @param email
     * @return all the data available inside the table TABLE_NAME_BORQS
     */
    public List<HomeActivityListData> getAlldata(String email) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        if (email != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_STATUS, DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.IMEI_NUM, DatabaseHelper.DEVICE_TYPE, DatabaseHelper.GROUP_NAME, DatabaseHelper.IS_GROUP_MEMBER, DatabaseHelper.IS_CREATED};
            String[] arg = {email};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_BORQS, column, DatabaseHelper.EMAIL + " = ? ", arg, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IS_CREATED)) > 0) {
                        HomeActivityListData data = new HomeActivityListData();
                        data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                        data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                        data.setConsentStaus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_STATUS)).trim());
                        data.setLat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAT)));
                        data.setLng(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LON)));
                        data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                        data.setDeviceType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE)));
                        data.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_NAME)));
                        mlist.add(data);
                    }
                }
                cursor.close();
            }
        }
        return mlist;
    }

    /**
     * Returns all Borqs data in the form of list from the table TABLE_NAME_BORQS
     *
     * @param email
     * @return all the data available inside the table TABLE_NAME_BORQS
     */
    public List<HomeActivityListData> getAllBorqsData(String email) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        if (email != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_STATUS, DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.IMEI_NUM, DatabaseHelper.DEVICE_TYPE, DatabaseHelper.GROUP_NAME, DatabaseHelper.IS_GROUP_MEMBER, DatabaseHelper.IS_CREATED, DatabaseHelper.DEVICE_ID};
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
                    data.setDeviceType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE)));
                    data.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_NAME)));
                    data.setDeviceId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_ID)));
                    mlist.add(data);
                }
            }
            cursor.close();
        }
        return mlist;
    }

    /**
     * Update lat and long in to the TABLE_NAME_BORQS table
     *
     * @param deviceId
     * @param mData
     */
    public void updateLatLangInBorqsDB(String deviceId, GetDeviceLocationData mData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.LAT, mData.getLat());
        values.put(DatabaseHelper.LON, mData.getLang());
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + deviceId, null);

    }

    /**
     * Update Logout data in TABLE_USER_LOGIN table
     */
    public void updateLogoutData() {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_USER_LOGIN, null, null);
    }

    /**
     * Update consent in TABLE_NAME_BORQS table
     *
     * @param phoneNumber
     * @param message
     */
    public void updateConsentInDeviceBors(String phoneNumber, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    /**
     * Update Pending Consent in TABLE_NAME_BORQS table
     *
     * @param phoneNumber
     */
    public void updatependingConsent(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_STATUS, Constant.CONSENT_PENDING);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    /**
     * Update device type and device name in TABLE_NAME_BORQS table
     *
     * @param deviceType
     * @param groupName
     * @param imeiNumber
     * @param mName
     * @param isGroupMember
     */
    public void updateDeviceTypeAndGroupName(String deviceType, String groupName, String imeiNumber, String mName, int isGroupMember) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_TYPE, deviceType);
        values.put(DatabaseHelper.GROUP_NAME, groupName);
        values.put(DatabaseHelper.NAME, mName);
        values.put(DatabaseHelper.IS_CREATED, 1);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.IMEI_NUM + "= " + imeiNumber, null);
    }

    /**
     * Update Group name and is group member in TABLE_NAME_BORQS table
     *
     * @param isGroupMember
     * @param imeiNumber
     * @param groupName
     */
    public void updateIsGroupMember(int isGroupMember, String imeiNumber, String groupName) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.IS_GROUP_MEMBER, isGroupMember);
        values.put(DatabaseHelper.GROUP_NAME, groupName);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.IMEI_NUM + "= " + imeiNumber, null);
    }

    /**
     * Update profile information in Database
     *
     * @param priviousNumber
     * @param name
     * @param newNumber
     */
    public void updateProfile(String priviousNumber, String name, String newNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_NUM, newNumber);
        values.put(DatabaseHelper.GROUP_NAME, name);
        //values.put(DatabaseHelper.IMEI_NUM, imei);
        if (!priviousNumber.equals(newNumber)) {
            values.put(DatabaseHelper.CONSENT_STATUS, Constant.REQUEST_CONSENT);
        }
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= '" + priviousNumber + "';", null);
    }

    /**
     * Delete the selected data from the table TABLE_NAME_BORQS
     *
     * @param phoneNumber
     */
    public void deleteSelectedData(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_NAME_BORQS, DatabaseHelper.DEVICE_NUM + "= '" + phoneNumber + "';", null);
    }

    /**
     * Returns User name from the table TABLE_USER_LOGIN
     *
     * @return user name
     */
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

    /**
     * Returns user Login detail
     *
     * @return user Login data
     */
    public AdminLoginData getAdminLoginDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        AdminLoginData adminData = null;
        String[] column = {DatabaseHelper.EMAIL, DatabaseHelper.USER_TOKEN, DatabaseHelper.USER_ID, DatabaseHelper.TOKEN_EXPIRY_TIME, DatabaseHelper.USER_NAME, DatabaseHelper.PHONE_COUNTRY_CODE};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_USER_LOGIN, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                adminData = new AdminLoginData();
                adminData.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMAIL)));
                adminData.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                adminData.setUserToken(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_TOKEN)));
                adminData.setTokenExpirytime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOKEN_EXPIRY_TIME)));
                adminData.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_NAME)));
                adminData.setPhoneCountryCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHONE_COUNTRY_CODE)));
            }
            cursor.close();
        }
        return adminData;
    }

    /**
     * Returns user phone number
     *
     * @return User phone number
     */
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

    /**
     * Returns consent status for the particular device
     *
     * @param phoneNumber
     * @return
     */
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

    /**
     * Update Consent time and approval time for the particular device in the table TABLE_NAME_BORQS
     *
     * @param phoneNumber
     * @param consentTime
     * @param approvalTime
     */
    public void updateConsentTime(String phoneNumber, String consentTime, int approvalTime) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CONSENT_TIME, consentTime);
        values.put(DatabaseHelper.CONSENT_TIME_APPROVAL_LIMIT, approvalTime);
        mDatabase.update(DatabaseHelper.TABLE_NAME_BORQS, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
    }

    /**
     * Returns the Consent time for the device
     *
     * @return Consent time in the form of AddedDeviceData object
     */
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

    /**
     * Update consent time and Status in the table TABLE_NAME_BORQS
     *
     * @param mList
     */
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

    /**
     * Returns Group data from the table TABLE_NAME_DEVICE
     *
     * @param groupName
     * @return
     */
    public List<HomeActivityListData> getGroupdata(String groupName) {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_DEVICE + " WHERE " + DatabaseHelper.GROUP_NAME + " = ?";
        if (groupName != null) {
            String[] column = {DatabaseHelper.DEVICE_NUM};
            String[] arg = {groupName};
            //Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_DEVICE, column, DatabaseHelper.GROUP_NAME + " = "  + groupName  , null, null, null, null);
            Cursor cursor = mDatabase.rawQuery(query, new String[]{groupName});
            if (cursor != null) {
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

    /**
     * Returns lat and long for the particular Group name
     *
     * @param groupName
     * @return
     */
    public List<MultipleselectData> getGroupLatLongdata(String groupName) {
        List<MultipleselectData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_DEVICE + " WHERE " + DatabaseHelper.GROUP_NAME + " = ?  AND " + DatabaseHelper.CONSENT_STATUS + " = ?";
        if (groupName != null) {
            String[] column = {DatabaseHelper.DEVICE_NUM};
            String[] arg = {groupName};
            //Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NAME_DEVICE, column, DatabaseHelper.GROUP_NAME + " = "  + groupName  , null, null, null, null);
            Cursor cursor = mDatabase.rawQuery(query, new String[]{groupName, Constant.CONSENT_STATUS_MSG});
            if (cursor != null) {
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

    /**
     * Insert into the Group Table
     */
    public long insertIntoGroupTable(CreateGroupResponse createGroupResponse) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.GROUPID, createGroupResponse.getData().getId());
        contentValue.put(DatabaseHelper.GROUP_NAME, createGroupResponse.getData().getName());
        contentValue.put(DatabaseHelper.STATUS, createGroupResponse.getData().getStatus());
        return mDatabase.insert(DatabaseHelper.TABLE_GROUP, null, contentValue);
    }

    /**
     * insert into the Group table after fetching data through GetGroupInfoPerUserRequest API call
     */
    public void insertAllDataIntoGroupTable(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            contentValue.put(DatabaseHelper.GROUPID, data.getId());
            contentValue.put(DatabaseHelper.GROUP_NAME, data.getGroupName());
            contentValue.put(DatabaseHelper.STATUS, data.getStatus());
            mDatabase.replace(DatabaseHelper.TABLE_GROUP, null, contentValue);
        }
    }


    /**
     * Returns Group Data
     */
    public List<HomeActivityListData> getAllGroupDetail() {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.GROUPID, DatabaseHelper.GROUP_NAME, DatabaseHelper.STATUS};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HomeActivityListData data = new HomeActivityListData();
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)) != null && cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)).equalsIgnoreCase(Constant.ACTIVE)) {
                    data.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_NAME)));
                    data.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                    data.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    mlist.add(data);
                }
            }
        }
        cursor.close();
        return mlist;
    }


    /**
     * Update Group Name in Database
     *
     * @param priviousName
     * @param newName
     * @param groupId
     */
    public void updateGroupName(String priviousName, String newName, String groupId) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!priviousName.equals(newName)) {
            values.put(DatabaseHelper.GROUP_NAME, newName);
        }
        mDatabase.update(DatabaseHelper.TABLE_GROUP, values, DatabaseHelper.GROUPID + "= '" + groupId + "';", null);
    }

    /**
     * Delete the Selected group from Database
     */
    public void deleteSelectedDataFromGroup(String groupId) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_GROUP, DatabaseHelper.GROUPID + "= '" + groupId + "';", null);
    }

    /**
     * Insert into Group Member Table
     */
    public void insertGroupMemberDataInTable(GroupMemberResponse groupMemberResponse, String name) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        if (name != null) {
            for (GroupMemberResponse.Data data : groupMemberResponse.getData()) {
                contentValue.put(DatabaseHelper.GROUPID, data.getGroupId());
                contentValue.put(DatabaseHelper.NAME, name);
                contentValue.put(DatabaseHelper.STATUS, data.getStatus());
                contentValue.put(DatabaseHelper.DEVICE_NUM, data.getPhone());
                contentValue.put(DatabaseHelper.CONSENT_ID, data.getConsentId());
                contentValue.put(DatabaseHelper.USER_ID, data.getUserId());
                contentValue.put(DatabaseHelper.DEVICE_ID, data.getDeviceId());
                mDatabase.replace(DatabaseHelper.TABLE_GROUP_MEMBER, null, contentValue);
            }
        } else {
            List<GroupMemberDataList> mGroupMemberData = getAllGroupMemberData();
            for(GroupMemberDataList groupMemberDataList : mGroupMemberData) {
                for (GroupMemberResponse.Data data : groupMemberResponse.getData()) {
                    contentValue.put(DatabaseHelper.GROUPID, data.getGroupId());
                    contentValue.put(DatabaseHelper.STATUS, data.getStatus());
                    contentValue.put(DatabaseHelper.DEVICE_NUM, data.getPhone());
                    contentValue.put(DatabaseHelper.CONSENT_ID, data.getConsentId());
                    contentValue.put(DatabaseHelper.USER_ID, data.getUserId());
                    contentValue.put(DatabaseHelper.DEVICE_ID, data.getDeviceId());
                    if(groupMemberDataList.getConsentId().equalsIgnoreCase(data.getConsentId())) {
                        contentValue.put(DatabaseHelper.NAME, groupMemberDataList.getName());
                    } else {
                        contentValue.put(DatabaseHelper.NAME, "");
                    }
                    mDatabase.replace(DatabaseHelper.TABLE_GROUP_MEMBER, null, contentValue);
                }
            }
        }
    }

    /**
     * Get all group member of a group based on group id
     */
    public List<GroupMemberDataList> getAllGroupMemberDataBasedOnGroupId(String groupId) {
        List<GroupMemberDataList> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.STATUS, DatabaseHelper.GROUPID, DatabaseHelper.CONSENT_ID};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_MEMBER, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                GroupMemberDataList data = new GroupMemberDataList();
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)).equalsIgnoreCase(groupId)) {
                    data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    data.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setConsentStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    data.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                    mlist.add(data);
                }
            }
        }
        cursor.close();
        return mlist;
    }

    /**
     *
     */
    /**
     * Get all group member of a group
     */
    public List<GroupMemberDataList> getAllGroupMemberData() {
        List<GroupMemberDataList> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.STATUS, DatabaseHelper.GROUPID, DatabaseHelper.CONSENT_ID};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_MEMBER, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                GroupMemberDataList data = new GroupMemberDataList();
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setConsentStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                data.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                mlist.add(data);
            }
        }
        cursor.close();
        return mlist;
    }
}

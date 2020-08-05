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

import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.AlertHistoryData;
import com.jio.devicetracker.database.pojo.ConsentTimeupdateData;
import com.jio.devicetracker.database.pojo.DeviceTableData;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.NotificationData;
import com.jio.devicetracker.database.pojo.SOSContactData;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.util.Constant;

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
     * Inserts Login data into the login(TABLE_USER_LOGIN) table
     *
     * @param data
     * @return
     */
    public long insertLoginData(LogindetailResponse data) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.USER_TOKEN, data.getData().getUgsToken());
        contentValue.put(DatabaseHelper.USER_ID, data.getData().getId());
        contentValue.put(DatabaseHelper.TOKEN_EXPIRY_TIME, "");
        contentValue.put(DatabaseHelper.USER_NAME, data.getData().getName());
        contentValue.put(DatabaseHelper.PHONE_COUNTRY_CODE, data.getData().getPhoneCountryCode());
        contentValue.put(DatabaseHelper.DEVICE_NUM, data.getData().getPhone());
        return mDatabase.insert(DatabaseHelper.TABLE_USER_LOGIN, null, contentValue);
    }

    /**
     * Update lat and long in to the TABLE_NAME_BORQS table
     *
     * @param latLngNew
     * @param radius
     */
    public int updateGeofenceDetailInGeofenceTable(LatLng latLngNew, int radius, String deviceNumber, LatLng latLngOld) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (latLngNew != null) {
            values.put(DatabaseHelper.LAT, latLngNew.latitude);
            values.put(DatabaseHelper.LON, latLngNew.longitude);
        }
        values.put(DatabaseHelper.RADIUS, radius);

        return mDatabase.update(DatabaseHelper.TABLE_GEOFENCE, values, DatabaseHelper.LAT + "= " + latLngOld.latitude + " AND " + DatabaseHelper.LON + "= " + latLngOld.longitude, null);
    }

    /**
     * Insert lat and long in to the TABLE_GEOFENCE table
     *
     * @param latLng
     * @param radius
     */
    public void insertGeofenceDetailInGeofenceTable(LatLng latLng, int radius, String deviceNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.LAT, latLng.latitude);
        values.put(DatabaseHelper.LON, latLng.longitude);
        values.put(DatabaseHelper.RADIUS, radius);
        values.put(DatabaseHelper.DEVICE_NUM, deviceNumber);
        mDatabase.insert(DatabaseHelper.TABLE_GEOFENCE, null, values);
    }


    /**
     * Update User details in Login table
     *
     * @param phoneNumber
     * @param emailId
     * @param name
     */

    public void updateAdminLoginTable(String phoneNumber, String emailId, String name) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER_NAME, name);
        values.put(DatabaseHelper.EMAIL, emailId);
        mDatabase.update(DatabaseHelper.TABLE_USER_LOGIN, values, DatabaseHelper.DEVICE_NUM + "= " + phoneNumber, null);
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
     * Returns user Login detail
     *
     * @return user Login data
     */
    public AdminLoginData getAdminLoginDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        AdminLoginData adminData = null;
        String[] column = {DatabaseHelper.USER_TOKEN, DatabaseHelper.USER_ID, DatabaseHelper.TOKEN_EXPIRY_TIME, DatabaseHelper.USER_NAME, DatabaseHelper.PHONE_COUNTRY_CODE, DatabaseHelper.DEVICE_NUM, DatabaseHelper.EMAIL};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_USER_LOGIN, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                adminData = new AdminLoginData();
                adminData.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                adminData.setEmailId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMAIL)));
                adminData.setUserToken(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_TOKEN)));
                adminData.setTokenExpirytime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOKEN_EXPIRY_TIME)));
                adminData.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_NAME)));
                adminData.setPhoneCountryCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHONE_COUNTRY_CODE)));
                adminData.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
            }
            cursor.close();
        }
        return adminData;
    }

    /**
     * Returns geofence detail
     *
     * @return geofence details
     */
    public GeofenceDetails getGeofenceDetails(String deviceNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        GeofenceDetails geofenceDetails = null;
        String[] column = {DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.RADIUS};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GEOFENCE, column, DatabaseHelper.DEVICE_NUM + " = " + deviceNumber, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                geofenceDetails = new GeofenceDetails();
                geofenceDetails.setLat(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)));
                geofenceDetails.setLng(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LON)));
                geofenceDetails.setRadius(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RADIUS)));

            }
            cursor.close();
        }
        return geofenceDetails;
    }

    /**
     * Returns geofence detail list
     *
     * @return geofence details
     */
    public List<GeofenceDetails> getGeofenceDetailsList(String deviceNumber) {
        List<GeofenceDetails> geofenceList = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        GeofenceDetails geofenceDetails = null;
        String[] column = {DatabaseHelper.LAT, DatabaseHelper.LON, DatabaseHelper.RADIUS};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GEOFENCE, column, DatabaseHelper.DEVICE_NUM + " = " + deviceNumber, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                geofenceDetails = new GeofenceDetails();
                geofenceDetails.setLat(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)));
                geofenceDetails.setLng(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LON)));
                geofenceDetails.setRadius(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RADIUS)));
                geofenceList.add(geofenceDetails);

            }
            cursor.close();
        }
        return geofenceList;
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
     * Insert into the Group Table
     */
    public long insertIntoGroupTable(CreateGroupResponse createGroupResponse) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.GROUPID, createGroupResponse.getData().getId());
        contentValue.put(DatabaseHelper.GROUP_NAME, createGroupResponse.getData().getName());
        contentValue.put(DatabaseHelper.STATUS, createGroupResponse.getData().getStatus());
        contentValue.put(DatabaseHelper.CREATED_BY, createGroupResponse.getData().getCreatedBy());
        contentValue.put(DatabaseHelper.UPDATED_BY, createGroupResponse.getData().getUpdatedBy());
        contentValue.put(DatabaseHelper.TIME_FROM, createGroupResponse.getData().getSession().getFrom());
        contentValue.put(DatabaseHelper.TIME_TO, createGroupResponse.getData().getSession().getTo());
        if (createGroupResponse.getData().getName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
            contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_user);
        } else {
            contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_group_button);
        }
        return mDatabase.insert(DatabaseHelper.TABLE_GROUP, null, contentValue);
    }

    /**
     * insert into the Group table after fetching data through GetGroupInfoPerUserRequest API call
     *
     * @param groupList
     */
    public void insertAllDataIntoGroupTable(List<HomeActivityListData> groupList) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        for (HomeActivityListData data : groupList) {
            contentValue.put(DatabaseHelper.GROUPID, data.getGroupId());
            contentValue.put(DatabaseHelper.GROUP_NAME, data.getGroupName());
            contentValue.put(DatabaseHelper.STATUS, data.getStatus());
            contentValue.put(DatabaseHelper.CREATED_BY, data.getCreatedBy());
            contentValue.put(DatabaseHelper.UPDATED_BY, data.getUpdatedBy());
            contentValue.put(DatabaseHelper.TIME_FROM, data.getFrom());
            contentValue.put(DatabaseHelper.TIME_TO, data.getTo());
            contentValue.put(DatabaseHelper.GROUP_OWNER_NAME, data.getGroupOwnerName());
            contentValue.put(DatabaseHelper.GROUP_OWNER_PHONE_NUMBER, data.getGroupOwnerPhoneNumber());
            contentValue.put(DatabaseHelper.GROUP_OWNER_USER_ID, data.getGroupOwnerUserId());
            contentValue.put(DatabaseHelper.CONSENTS_COUNT, data.getConsentsCount());
            if (data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME)) {
                contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_user);
            } else {
                contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_group_button);
            }
            mDatabase.replace(DatabaseHelper.TABLE_GROUP, null, contentValue);
        }
    }

    /**
     * Inserts group icon into the table along with groupid
     *
     * @param groupId
     * @param groupIcon
     */
    public void insertInToGroupIconTable(String groupId, String groupIcon) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.GROUPID, groupId);
        contentValue.put(DatabaseHelper.GROUP_ICON, groupIcon);
        mDatabase.replace(DatabaseHelper.TABLE_GROUP_ICON, null, contentValue);
    }

    public List<HomeActivityListData> getAllGroupIconTableData() {
        List<HomeActivityListData> mList = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.GROUPID, DatabaseHelper.GROUP_ICON};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_ICON, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HomeActivityListData homeActivityListData = new HomeActivityListData();
                homeActivityListData.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                homeActivityListData.setGroupIcon(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_ICON)));
                mList.add(homeActivityListData);
            }
        }
        return mList;
    }

    /**
     * Returns Group Data
     *
     * @return List of HomeActivityListData
     */
    public List<HomeActivityListData> getAllGroupDetail() {
        List<HomeActivityListData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.GROUPID, DatabaseHelper.GROUP_NAME, DatabaseHelper.STATUS, DatabaseHelper.CREATED_BY, DatabaseHelper.UPDATED_BY, DatabaseHelper.PROFILE_IMAGE,
                DatabaseHelper.TIME_FROM, DatabaseHelper.TIME_TO, DatabaseHelper.GROUP_OWNER_NAME, DatabaseHelper.GROUP_OWNER_USER_ID, DatabaseHelper.GROUP_OWNER_PHONE_NUMBER, DatabaseHelper.CONSENTS_COUNT};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HomeActivityListData data = new HomeActivityListData();
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)) != null && cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)).equalsIgnoreCase(Constant.ACTIVE) || cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)).equalsIgnoreCase(Constant.SCHEDULED) || cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)).equalsIgnoreCase(Constant.COMPLETED)) {
                    data.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_NAME)));
                    data.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                    data.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    data.setCreatedBy(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CREATED_BY)));
                    data.setUpdatedBy(cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_BY)));
                    data.setProfileImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PROFILE_IMAGE)));
                    data.setFrom(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_FROM)));
                    data.setTo(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_TO)));
                    data.setGroupOwnerName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_NAME)));
                    data.setGroupOwnerPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_PHONE_NUMBER)));
                    data.setGroupOwnerUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_USER_ID)));
                    data.setConsentsCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSENTS_COUNT)));
                    mlist.add(data);
                }
            }
        }
        cursor.close();
        return mlist;
    }

    /**
     * Get group detail based on group id from Group Table
     *
     * @param groupId
     * @return Entire group detail
     */
    public HomeActivityListData getGroupDetail(String groupId) {
        mDatabase = mDBHelper.getWritableDatabase();
        HomeActivityListData data = new HomeActivityListData();
        if (groupId != null) {
            String[] column = {DatabaseHelper.GROUP_NAME, DatabaseHelper.GROUPID, DatabaseHelper.STATUS, DatabaseHelper.CREATED_BY, DatabaseHelper.UPDATED_BY,
                    DatabaseHelper.PROFILE_IMAGE, DatabaseHelper.TIME_FROM, DatabaseHelper.TIME_TO, DatabaseHelper.GROUP_OWNER_NAME,
                    DatabaseHelper.GROUP_OWNER_PHONE_NUMBER, DatabaseHelper.GROUP_OWNER_USER_ID, DatabaseHelper.CONSENTS_COUNT};
            String[] arg = {groupId};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP, column, DatabaseHelper.GROUPID + " = ? ", arg, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    data.setGroupName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_NAME)));
                    data.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                    data.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    data.setCreatedBy(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CREATED_BY)));
                    data.setUpdatedBy(cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_BY)));
                    data.setProfileImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PROFILE_IMAGE)));
                    data.setFrom(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_FROM)));
                    data.setTo(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_TO)));
                    data.setGroupOwnerUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_USER_ID)));
                    data.setGroupOwnerName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_NAME)));
                    data.setGroupOwnerPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUP_OWNER_PHONE_NUMBER)));
                    data.setConsentsCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CONSENTS_COUNT)));
                }
                cursor.close();
            }
        }
        return data;
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
     * Delete the Selected data from group table
     */
    public void deleteSelectedDataFromGroup(String groupId) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_GROUP, DatabaseHelper.GROUPID + "= '" + groupId + "';", null);
    }

    /**
     * Delete the geofence data from geofence table
     */
    public void deleteGeofenceData(double latitude, double longnitude) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_GEOFENCE, DatabaseHelper.LAT + "= " + latitude + " AND " + DatabaseHelper.LON + "= " + longnitude, null);
    }

    /**
     * Insert into Group Member Table
     *
     * @param groupMemberResponse
     */
    public void insertGroupMemberDataInTable(GroupMemberResponse groupMemberResponse) {
        mDatabase = mDBHelper.getWritableDatabase();
        for (GroupMemberResponse.Data data : groupMemberResponse.getData()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.GROUPID, data.getGroupId());
            contentValue.put(DatabaseHelper.NAME, data.getName());
            contentValue.put(DatabaseHelper.STATUS, data.getStatus());
            contentValue.put(DatabaseHelper.DEVICE_NUM, data.getPhone());
            contentValue.put(DatabaseHelper.CONSENT_ID, data.getConsentId());
            contentValue.put(DatabaseHelper.USER_ID, data.getUserId());
            contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_user);
            contentValue.put(DatabaseHelper.DEVICE_ID, data.getDeviceId());
            if (data.isGroupAdmin()) {
                contentValue.put(DatabaseHelper.IS_GROUP_ADMIN, 1);
            } else {
                contentValue.put(DatabaseHelper.IS_GROUP_ADMIN, 0);
            }
            mDatabase.replace(DatabaseHelper.TABLE_GROUP_MEMBER, null, contentValue);
        }
    }

    /**
     * Insert Into the Group Member table when parameter is list
     */
    public void insertGroupMemberDataInListFormat(List<GroupMemberDataList> mGroupMemberDataLists) {
        mDatabase = mDBHelper.getWritableDatabase();
        List<GroupMemberDataList> mList = getAllGroupMemberData();
        for (GroupMemberDataList responseData : mGroupMemberDataLists) {
            ContentValues contentValue = new ContentValues();
            if (!mList.isEmpty() && responseData.getDeviceId() == null) {
                for (GroupMemberDataList groupMemberDataList : mList) {
                    if (groupMemberDataList.getConsentId() != null && responseData.getConsentId() != null && groupMemberDataList.getConsentId().equalsIgnoreCase(responseData.getConsentId())) {
                        contentValue.put(DatabaseHelper.DEVICE_ID, groupMemberDataList.getDeviceId());
                    }
                }
            } else {
                contentValue.put(DatabaseHelper.DEVICE_ID, responseData.getDeviceId());
            }
            if (!contentValue.containsKey(DatabaseHelper.DEVICE_ID)) {
                contentValue.put(DatabaseHelper.DEVICE_ID, responseData.getDeviceId());
            }
            contentValue.put(DatabaseHelper.CONSENT_ID, responseData.getConsentId());
            contentValue.put(DatabaseHelper.DEVICE_NUM, responseData.getNumber());
            contentValue.put(DatabaseHelper.IS_GROUP_ADMIN, responseData.isGroupAdmin());
            contentValue.put(DatabaseHelper.GROUPID, responseData.getGroupId());
            contentValue.put(DatabaseHelper.STATUS, responseData.getConsentStatus());
            contentValue.put(DatabaseHelper.USER_ID, responseData.getUserId());
            contentValue.put(DatabaseHelper.NAME, responseData.getName());
            contentValue.put(DatabaseHelper.PROFILE_IMAGE, R.drawable.ic_user);
            mDatabase.replace(DatabaseHelper.TABLE_GROUP_MEMBER, null, contentValue);
        }
    }

    /**
     * Get all group member of a group based on group id
     *
     * @param groupId
     */
    public List<GroupMemberDataList> getAllGroupMemberDataBasedOnGroupId(String groupId) {
        List<GroupMemberDataList> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.STATUS, DatabaseHelper.GROUPID, DatabaseHelper.CONSENT_ID, DatabaseHelper.IS_GROUP_ADMIN,
                DatabaseHelper.PROFILE_IMAGE, DatabaseHelper.GROUPID, DatabaseHelper.DEVICE_ID, DatabaseHelper.USER_ID};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_MEMBER, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                GroupMemberDataList data = new GroupMemberDataList();
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)).equalsIgnoreCase(groupId)) {
                    data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    data.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    data.setConsentStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    data.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                    data.setProfileImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PROFILE_IMAGE)));
                    data.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                    data.setDeviceId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_ID)));
                    data.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                    if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IS_GROUP_ADMIN)) == 1) {
                        data.setGroupAdmin(true);
                    } else {
                        data.setGroupAdmin(false);
                    }
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
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.STATUS, DatabaseHelper.GROUPID, DatabaseHelper.CONSENT_ID, DatabaseHelper.IS_GROUP_ADMIN, DatabaseHelper.USER_ID, DatabaseHelper.DEVICE_ID, DatabaseHelper.GROUPID, DatabaseHelper.PROFILE_IMAGE};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_MEMBER, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                GroupMemberDataList data = new GroupMemberDataList();
                data.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                data.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setConsentStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                data.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                data.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                data.setDeviceId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_ID)));
                data.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                data.setProfileImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PROFILE_IMAGE)));
                if (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IS_GROUP_ADMIN)) == 1) {
                    data.setGroupAdmin(true);
                } else {
                    data.setGroupAdmin(false);
                }
                mlist.add(data);
            }
        }
        cursor.close();
        return mlist;
    }

    /**
     * Delete the Selected data from group member table
     */
    public void deleteSelectedDataFromGroupMember(String consentId) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_GROUP_MEMBER, DatabaseHelper.CONSENT_ID + "= '" + consentId + "';", null);
    }

    /**
     * Update consent in TABLE_NAME_BORQS table
     *
     * @param consentId
     * @param message
     */
    public void updateConsentInGroupMemberTable(String consentId, String message) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.STATUS, message);
        mDatabase.update(DatabaseHelper.TABLE_GROUP_MEMBER, values, DatabaseHelper.CONSENT_ID + "= '" + consentId + "';", null);
    }

    /**
     * @param consentId
     * @return Group Member details
     */
    public GroupMemberDataList getGroupMemberDetailByConsentId(String consentId) {
        mDatabase = mDBHelper.getWritableDatabase();
        GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
        if (consentId != null) {
            String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.STATUS,
                    DatabaseHelper.CONSENT_ID, DatabaseHelper.USER_ID, DatabaseHelper.DEVICE_ID, DatabaseHelper.GROUPID, DatabaseHelper.PROFILE_IMAGE};
            String[] arg = {consentId};
            Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_GROUP_MEMBER, column, DatabaseHelper.CONSENT_ID + " = ? ", arg, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    groupMemberDataList.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    groupMemberDataList.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    groupMemberDataList.setConsentStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
                    groupMemberDataList.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                    groupMemberDataList.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
                    groupMemberDataList.setDeviceId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_ID)));
                    groupMemberDataList.setGroupId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROUPID)));
                    groupMemberDataList.setProfileImage(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PROFILE_IMAGE)));
                    return groupMemberDataList;
                }
            }
        }
        return groupMemberDataList;
    }

    public void deleteAllPreviousData() {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_USER_LOGIN, null, null);
        mDatabase.delete(DatabaseHelper.TABLE_GROUP_MEMBER, null, null);
        mDatabase.delete(DatabaseHelper.TABLE_GROUP, null, null);
        mDatabase.delete(DatabaseHelper.TABLE_SOS, null, null);
    }

    public void insertIntoAlertHistoryTable(AlertHistoryData alertHistoryData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, alertHistoryData.getName());
        contentValue.put(DatabaseHelper.DEVICE_NUM, alertHistoryData.getNumber());
        contentValue.put(DatabaseHelper.CONSENT_ID, alertHistoryData.getConsentId());
        contentValue.put(DatabaseHelper.ALERT_TIME, alertHistoryData.getDate());
        contentValue.put(DatabaseHelper.ADDRESS, alertHistoryData.getAddress());
        contentValue.put(DatabaseHelper.STATE, alertHistoryData.getState());
        mDatabase.replace(DatabaseHelper.TABLE_ALERTS_HOSTORY, null, contentValue);
    }

    public List<AlertHistoryData> getHistoryTableData(String consentId) {
        List<AlertHistoryData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.NAME, DatabaseHelper.DEVICE_NUM, DatabaseHelper.CONSENT_ID, DatabaseHelper.ALERT_TIME, DatabaseHelper.ADDRESS, DatabaseHelper.STATE};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_ALERTS_HOSTORY, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)).equalsIgnoreCase(consentId)) {
                    AlertHistoryData mAlertHistoryData = new AlertHistoryData();
                    mAlertHistoryData.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
                    mAlertHistoryData.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    mAlertHistoryData.setConsentId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONSENT_ID)));
                    mAlertHistoryData.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALERT_TIME)));
                    mAlertHistoryData.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS)));
                    mAlertHistoryData.setState(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATE)));
                    mlist.add(mAlertHistoryData);
                }
            }
        }
        cursor.close();
        return mlist;
    }

    // Inserts data into the Device table
    public void insertIntoDeviceTable(DeviceTableData deviceTableData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.IMEI_NUM, deviceTableData.getImeiNumber());
        contentValue.put(DatabaseHelper.DEVICE_NUM, deviceTableData.getPhoneNumber());
        contentValue.put(DatabaseHelper.COUNT, deviceTableData.getAdditionCount());
        mDatabase.replace(DatabaseHelper.TABLE_DEVICE, null, contentValue);
    }

    // Update device table
    public void updateIntoDeviceTable(DeviceTableData deviceTableData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT, deviceTableData.getAdditionCount());
        mDatabase.update(DatabaseHelper.TABLE_DEVICE, values, DatabaseHelper.DEVICE_NUM + "= " + deviceTableData.getPhoneNumber(), null);
    }

    // Delete from Device table
    public void deleteFromDeviceTable(String imeiNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_DEVICE, DatabaseHelper.IMEI_NUM + "= '" + imeiNumber + "';", null);
    }

    // Returns Device Table data which matches with the phoneNumber
    public DeviceTableData getDeviceTableData(String phoneNumber) {
        mDatabase = mDBHelper.getWritableDatabase();
        DeviceTableData deviceTableData = null;
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_DEVICE + " WHERE " + DatabaseHelper.DEVICE_NUM + " = ?";
        if (phoneNumber != null) {
            Cursor cursor = mDatabase.rawQuery(query, new String[]{phoneNumber});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    deviceTableData = new DeviceTableData();
                    deviceTableData.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                    deviceTableData.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                    deviceTableData.setAdditionCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COUNT)));
                }
                cursor.close();
            }
        }
        return deviceTableData;
    }

    // Returns all device table data
    public List<DeviceTableData> getAllDeviceTableData() {
        List<DeviceTableData> mlist = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.IMEI_NUM, DatabaseHelper.DEVICE_NUM, DatabaseHelper.COUNT};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_DEVICE, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DeviceTableData data = new DeviceTableData();
                data.setImeiNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUM)));
                data.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                data.setAdditionCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COUNT)));
                mlist.add(data);
            }
        }
        cursor.close();
        return mlist;
    }

    /**
     * Inserts data into the SOS table
     *
     * @param mList
     */
    public void insertIntoSOSTable(List<SOSContactData> mList) {
        mDatabase = mDBHelper.getWritableDatabase();
        for (SOSContactData sosContactData : mList) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.PHONEBOOK_ID, sosContactData.getPhonebookId());
            contentValue.put(DatabaseHelper.PRIORITY, sosContactData.getPriority());
            contentValue.put(DatabaseHelper.DEVICE_NUM, sosContactData.getNumber());
            mDatabase.replace(DatabaseHelper.TABLE_SOS, null, contentValue);
        }
    }

    //Returns all SOS table data
    public List<SOSContactData> getAllSOStableData() {
        List<SOSContactData> mList = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.PHONEBOOK_ID, DatabaseHelper.PRIORITY, DatabaseHelper.DEVICE_NUM};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_SOS, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                SOSContactData sosContactData = new SOSContactData();
                sosContactData.setPhonebookId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHONEBOOK_ID)));
                sosContactData.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_NUM)));
                sosContactData.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PRIORITY)));
                mList.add(sosContactData);
            }
        }
        cursor.close();
        return mList;
    }

    /**
     * Delete the SOS contact based on PhoneId
     *
     * @param phonebookId
     */
    public void deleteSOSDetail(String phonebookId) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.TABLE_SOS, DatabaseHelper.PHONEBOOK_ID + "= '" + phonebookId + "';", null);
    }

    // Update SOS database
    public void updateSOSDatabase(List<SOSContactData> sosContactDataList) {
        mDatabase = mDBHelper.getWritableDatabase();
        for (SOSContactData sosContactData : sosContactDataList) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.PRIORITY, sosContactData.getPriority());
            values.put(DatabaseHelper.DEVICE_NUM, sosContactData.getNumber());
            mDatabase.update(DatabaseHelper.TABLE_SOS, values, DatabaseHelper.PHONEBOOK_ID + "= '" + sosContactData.getPhonebookId() + "';", null);
        }
    }

    /**
     * @param notificationData
     */
    public void insertIntoNotificationTable(NotificationData notificationData) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NOTIFICATION_TITLE, notificationData.getNotificationTitle());
        contentValue.put(DatabaseHelper.NOTIFICATION_MESSAGE, notificationData.getNotificationMessage());
        contentValue.put(DatabaseHelper.NOTIFICATION_TIME, notificationData.getNotificationDate());
        mDatabase.replace(DatabaseHelper.TABLE_NOTIFICATION, null, contentValue);
    }

    // Returns all Notification data from Notification table
    public List<NotificationData> getAllNotificationData() {
        List<NotificationData> mList = new ArrayList<>();
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.NOTIFICATION_TITLE, DatabaseHelper.NOTIFICATION_MESSAGE, DatabaseHelper.NOTIFICATION_TIME};
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_NOTIFICATION, column, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                NotificationData notificationData = new NotificationData();
                notificationData.setNotificationTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTIFICATION_TITLE)));
                notificationData.setNotificationMessage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTIFICATION_MESSAGE)));
                notificationData.setNotificationDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTIFICATION_TIME)));
                mList.add(notificationData);
            }
        }
        cursor.close();
        return mList;
    }

}

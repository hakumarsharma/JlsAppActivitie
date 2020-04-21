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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Implementation of database helper class to create and update the table for People Tracker application application.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Table Name
    public static final String TABLE_NAME_BORQS = "BorqsDevicedata";
    public static final String TABLE_NAME_DEVICE = "BorqsAddDevicedata";
    public static final String TABLE_NAME_FMS = "FmsDevicedata";
    public static final String TABLE_NAME_USER = "UserData";
    public static final String TABLE_USER_LOGIN = "UserloginData";
    public static final String TABLE_GROUP = "GroupData";
    public static final String TABLE_GROUP_MEMBER = "GroupMemberData";

    //Table Columns
    public static final String IMEI_NUM = "imei";
    public static final String DEVICE_NUM = "deviceNumber";
    public static final String NAME = "name";
    public static final String RELATION = "relation";
    public static  final String CONSENT_STATUS = "consentStatus";
    public static final String GROUP_NAME = "groupName";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String LAT = "latitude";
    public static final String LON = "longnitude";
    public static final String EMAIL = "email";
    public static final String DOB = "dob";
    public static final String PASS = "password";
    public static final String USER_ID = "userid";
    public static final String USER_TOKEN = "userToken";
    public static final String TOKEN_EXPIRY_TIME = "Tokenexpirytime";
    public static final String USER_NAME = "name";
    public static final String CONSENT_TIME = "ConsentTime";
    public static final String CONSENT_TIME_APPROVAL_LIMIT = "ConsentApprovalTime";
    public static final String IS_GROUP_MEMBER = "isGroupMember";
    public static final String IS_CREATED = "isCreated";
    public static final String DEVICE_ID = "deviceId";
    public static final String GROUPID = "groupId";
    public static final String STATUS = "status";
    public static final String PHONE_COUNTRY_CODE = "phoneCountryCode";
    public static final String CONSENT_ID = "consentId";

    //DB Information
    public static final String DB_NAME = "AddDevice.db";
    public static final int DB_VERSION = 1;


    private static final String CREATE_TABLE_BORQS = "create table " + TABLE_NAME_BORQS + "(" + IMEI_NUM
            + " TEXT, " + DEVICE_NUM + " TEXT, " + NAME + " TEXT, " + EMAIL + " TEXT, " + CONSENT_STATUS + " TEXT, " + DEVICE_ID + " TEXT ," + GROUP_NAME + " TEXT, " + DEVICE_TYPE + " TEXT, " + IS_GROUP_MEMBER + " INTEGER ," + IS_CREATED + " INTEGER ," + LAT + " DOUBLE ," + LON + " DOUBLE ,"  + CONSENT_TIME + " TEXT ," + CONSENT_TIME_APPROVAL_LIMIT + " INTEGER ,"  +"PRIMARY KEY" +"("+DEVICE_NUM +"))";

    private static final String CREATE_TABLE_DEVICE = "create table " + TABLE_NAME_DEVICE + "(" + IMEI_NUM
            + " TEXT, " + DEVICE_NUM + " TEXT, " + NAME + " TEXT, " + EMAIL + " TEXT, " +CONSENT_STATUS+ " TEXT, "+GROUP_NAME+ " TEXT," + LAT + " DOUBLE ," + LON + " DOUBLE ,"  + CONSENT_TIME + " TEXT ," + CONSENT_TIME_APPROVAL_LIMIT + " INTEGER ," + IS_GROUP_MEMBER + " INTEGER ,"  +"PRIMARY KEY" +"("+DEVICE_NUM +"))";


    private static final String CREATE_TABLE_FMS = "create table " + TABLE_NAME_FMS + "(" + IMEI_NUM
            + " TEXT, " + DEVICE_NUM + " TEXT, " + NAME + " TEXT, " + RELATION + " TEXT, " +CONSENT_STATUS+ " TEXT, " + LAT + " DOUBLE ," + LON + " DOUBLE ," +"PRIMARY KEY" +"(" +DEVICE_NUM +" ," +IMEI_NUM +"))";

    private static final String CREATE_TABLE_USER = "create table " + TABLE_NAME_USER + "("+ NAME + " TEXT, " + EMAIL + " TEXT, " + DEVICE_NUM + " TEXT ," + DOB + " TEXT ," + PASS + " TEXT ," + USER_ID + " TEXT ,"+"PRIMARY KEY" +"("+DEVICE_NUM +" ," +EMAIL +"))";

    private static final String CREATE_TABLE_USER_LOGIN = "create table " + TABLE_USER_LOGIN + "("+ USER_TOKEN + " TEXT, " + TOKEN_EXPIRY_TIME + " TEXT, " + USER_NAME + " TEXT," + USER_ID + " TEXT, " + PHONE_COUNTRY_CODE + " TEXT, " + EMAIL + " TEXT, "+ "PRIMARY KEY" +"(" + USER_ID+"))";

    private static final String CREATE_TABLE_GROUP = "create table " + TABLE_GROUP + "(" + GROUPID + " TEXT, " + GROUP_NAME + " TEXT, " + STATUS + " TEXT, " + "PRIMARY KEY" + "(" + GROUPID +"))";

    private static final String CREATE_TABLE_GROUP_MEMBER = "create table " + TABLE_GROUP_MEMBER + "(" + CONSENT_ID + " TEXT, " + NAME + " TEXT, " + GROUPID + " TEXT, " + DEVICE_NUM + " TEXT ," + USER_ID + " TEXT ," + DEVICE_ID + " TEXT ," + STATUS + " TEXT, " + "PRIMARY KEY" +"("+ DEVICE_ID +"))";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called automatically when we install the application
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BORQS);
        db.execSQL(CREATE_TABLE_DEVICE);
        db.execSQL(CREATE_TABLE_FMS);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_USER_LOGIN);
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(CREATE_TABLE_GROUP_MEMBER);
    }

    /**
     * Called automatically when we install the application
     * @param db
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BORQS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBER);
        onCreate(db);
    }
}
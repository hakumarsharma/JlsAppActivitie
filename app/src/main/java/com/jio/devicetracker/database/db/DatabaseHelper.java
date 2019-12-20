// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Table Name
    public static final String TABLE_NAME_BORQS = "BorqsDevicedata";
    public static final String TABLE_NAME_FMS = "FmsDevicedata";
    public static final String TABLE_NAME_USER = "UserData";

    //Table Columns
    public static final String IMEI_NUM = "imei";
    public static final String DEVICE_NUM = "deviceNumber";
    public static final String NAME = "name";
    public static final String RELATION = "relation";
    public static  final String CONSENT_STATUS = "consentStatus";
    public static final String LAT = "latitude";
    public static final String LON = "longnitude";

    //DB Information
    public static final String DB_NAME = "AddDevice.db";
    public static final int DB_VERSION = 1;


    private static final String CREATE_TABLE_BORQS = "create table " + TABLE_NAME_BORQS + "(" + IMEI_NUM
            + " TEXT, " + DEVICE_NUM + " TEXT, " + NAME + " TEXT, " + RELATION + " TEXT, " +CONSENT_STATUS+ " TEXT, " + LAT + " DOUBLE ," + LON + " DOUBLE ," +"PRIMARY KEY" +"("+DEVICE_NUM +"))";

    private static final String CREATE_TABLE_FMS = "create table " + TABLE_NAME_FMS + "(" + IMEI_NUM
            + " TEXT, " + DEVICE_NUM + " TEXT, " + NAME + " TEXT, " + RELATION + " TEXT, " +CONSENT_STATUS+ " TEXT, " + LAT + " DOUBLE ," + LON + " DOUBLE ," +"PRIMARY KEY" +"(" +DEVICE_NUM +"))";

    private static final String CREATE_TABLE_USER = "create table " + TABLE_NAME_USER + "("+ DEVICE_NUM + " TEXT, " + NAME + " TEXT, " +"PRIMARY KEY" +"("+DEVICE_NUM +"))";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BORQS);
        db.execSQL(CREATE_TABLE_FMS);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BORQS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        onCreate(db);
    }
}
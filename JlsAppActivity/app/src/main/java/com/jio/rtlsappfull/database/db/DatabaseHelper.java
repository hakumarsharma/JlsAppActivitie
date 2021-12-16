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

package com.jio.rtlsappfull.database.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Implementation of database helper class to create and update the table for Jio Tracker application application.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Table Name
    public static final String GOOGLE_LOCATION_DETAIL = "google_location_detail";
    public static final String JIO_LOCATION_DETAIL = "jio_location_detail";
    public static final String CELL_INFO = "cell_info";

    //Table Columns
    public static final String LAT = "latitude";
    public static final String LNG = "longitude";
    public static final String TIME_STAMP = "time_stamp";
    public static final String JIO_LAT = "jio_latitude";
    public static final String JIO_LNG = "jio_longitude";
    public static final String JIO_TIME_STAMP = "jio_time_stamp";
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";
    public static final String TAC = "tac";
    public static final String RSSI = "rssi";
    public static final String CELL_ID = "cell_id";
    public static final String FREQUENCY = "frequency";

    //DB Information
    public static final String DB_NAME = "rtls.db";
    public static final int DB_VERSION = 1;


    private static final String CREATE_TABLE_GOOGLE_LOCATION = "create table " + GOOGLE_LOCATION_DETAIL + "(" + TIME_STAMP
            + " LONG, " + LAT + " DOUBLE, " + LNG + " DOUBLE, " +"PRIMARY KEY" +"("+TIME_STAMP +"))";
    private static final String CREATE_TABLE_JIO_LOCATION_DETAIL = "create table " + JIO_LOCATION_DETAIL + "(" + JIO_TIME_STAMP
            + " LONG, " + JIO_LAT + " DOUBLE, " + JIO_LNG + " DOUBLE, " +"PRIMARY KEY" +"("+JIO_TIME_STAMP +"))";
    private static final String CREATE_CELL_INFO_TABLE = "create table " + CELL_INFO + "(" + TIME_STAMP
            + " LONG, " + LAT + " DOUBLE, " + LNG + " DOUBLE, " + MCC + " TEXT, " + MNC + " TEXT, "
            + TAC + " TEXT, " + CELL_ID + " TEXT, " + RSSI + " TEXT, " + FREQUENCY + " TEXT, " + "PRIMARY KEY" +"("+ CELL_ID +"))";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called automatically when we install the application
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GOOGLE_LOCATION);
        db.execSQL(CREATE_TABLE_JIO_LOCATION_DETAIL);
        db.execSQL(CREATE_CELL_INFO_TABLE);
    }

    /**
     * Called automatically when we install the application
     * @param db
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GOOGLE_LOCATION_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + JIO_LOCATION_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + CELL_INFO);
        onCreate(db);
    }
}
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.jio.rtlsappfull.model.CellLocationData;
import com.jio.rtlsappfull.model.MarkerDetail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DataBase manager class to manage all operation like insert,delete,update and fetch in database.
 */
public class DBManager {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public DBManager(Context context) {
        mDBHelper = new DatabaseHelper(context);
        mContext = context;
    }

    /**
     * Inserts Location detail into the google_location_detail table
     *
     * @param markerDetail
     * @return
     */
    public long insertLocationDetailInDatabase(MarkerDetail markerDetail) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TIME_STAMP, markerDetail.getTimeStamp());
        contentValue.put(DatabaseHelper.LAT, markerDetail.getLat());
        contentValue.put(DatabaseHelper.LNG, markerDetail.getLng());
        return mDatabase.insert(DatabaseHelper.GOOGLE_LOCATION_DETAIL, null, contentValue);
    }

    /**
     * Returns all data from google_location_detail table in the form of list
     *
     * @return all the data available inside the table google_location_detail
     */
    public List<MarkerDetail> getAllLocationdata() {
        mDatabase = mDBHelper.getWritableDatabase();
        List<MarkerDetail> mList = new ArrayList<>();
        String[] column = {DatabaseHelper.TIME_STAMP, DatabaseHelper.LAT, DatabaseHelper.LNG};
        Cursor cursor = mDatabase.query(DatabaseHelper.GOOGLE_LOCATION_DETAIL, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MarkerDetail markerDetail = new MarkerDetail();
                markerDetail.setTimeStamp(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_STAMP)));
                markerDetail.setLat(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)));
                markerDetail.setLng(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LNG)));
                mList.add(markerDetail);
            }
            cursor.close();
        }
        return mList;
    }

    public Long rowCountInTable() {
        mDatabase = mDBHelper.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(mDatabase, "google_location_detail");
    }

    public void deleteLastLocationDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.TIME_STAMP};
        Cursor cursor = mDatabase.query(DatabaseHelper.GOOGLE_LOCATION_DETAIL, column, null, null, null, null, DatabaseHelper.TIME_STAMP + " ASC", "1");
        cursor.moveToNext();
        Long timeStamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TIME_STAMP));
        mDatabase.delete(DatabaseHelper.GOOGLE_LOCATION_DETAIL, DatabaseHelper.TIME_STAMP + "= '" + timeStamp + "';", null);
    }

    /**
     * Inserts Location detail into the jio_location_detail table
     *
     * @param markerDetail
     * @return
     */
    public long insertJioLocationDetailInDatabase(MarkerDetail markerDetail) {
        mDatabase = mDBHelper.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.JIO_TIME_STAMP, markerDetail.getTimeStamp());
        contentValue.put(DatabaseHelper.JIO_LAT, markerDetail.getLat());
        contentValue.put(DatabaseHelper.JIO_LNG, markerDetail.getLng());
        return mDatabase.insert(DatabaseHelper.JIO_LOCATION_DETAIL, null, contentValue);
    }

    /**
     * Inserts Cell Info in cell_info table, when cellId is not found on server
     *
     * @param jsonObject
     */
    public void insertCellInfoInDB(JSONObject jsonObject) {
        mDatabase = mDBHelper.getWritableDatabase();
        try {
            JSONObject gps = jsonObject.getJSONObject("gps");
            JSONArray letCells = jsonObject.getJSONArray("ltecells");
            Double lat = gps.getDouble("lat");
            Double lng = gps.getDouble("lng");
            if (lat != 0.0 && lng != 0.0) {
                for (int i = 0; i < letCells.length(); i++) {
                    JSONObject lteCellsObject = letCells.getJSONObject(i);
                    ContentValues contentValue = new ContentValues();
                    contentValue.put(DatabaseHelper.TIME_STAMP, System.currentTimeMillis());
                    contentValue.put(DatabaseHelper.LAT, lat);
                    contentValue.put(DatabaseHelper.LNG, lng);
                    contentValue.put(DatabaseHelper.MCC, lteCellsObject.getInt("mcc"));
                    contentValue.put(DatabaseHelper.MNC, lteCellsObject.getInt("mnc"));
                    contentValue.put(DatabaseHelper.TAC, lteCellsObject.getInt("tac"));
                    contentValue.put(DatabaseHelper.CELL_ID, lteCellsObject.getInt("cellId"));
                    contentValue.put(DatabaseHelper.FREQUENCY, lteCellsObject.getInt("frequency"));
                    contentValue.put(DatabaseHelper.RSSI, lteCellsObject.getInt("rssi"));
                    mDatabase.insert(DatabaseHelper.CELL_INFO, null, contentValue);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all cell_info data in the form of list
     *
     * @return all the data available inside the table cell_info
     */
    public List<CellLocationData> getAllCellInfoata() {
        mDatabase = mDBHelper.getWritableDatabase();
        List<CellLocationData> mList = new ArrayList<>();
        String[] column = {DatabaseHelper.TIME_STAMP, DatabaseHelper.LAT,
                DatabaseHelper.LNG, DatabaseHelper.MCC, DatabaseHelper.MNC,
                DatabaseHelper.TAC, DatabaseHelper.CELL_ID, DatabaseHelper.FREQUENCY, DatabaseHelper.RSSI};
        Cursor cursor = mDatabase.query(DatabaseHelper.CELL_INFO, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CellLocationData cellLocationData = new CellLocationData();
                cellLocationData.setCellId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CELL_ID)));
                cellLocationData.setTimestamp(System.currentTimeMillis());
                cellLocationData.setLat(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LAT)));
                cellLocationData.setLng(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LNG)));
                cellLocationData.setFrequency(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FREQUENCY)));
                cellLocationData.setRssi(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RSSI)));
                cellLocationData.setMcc(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MCC)));
                cellLocationData.setMnc(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MNC)));
                cellLocationData.setTac(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TAC)));
                cellLocationData.setRssi(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RSSI)));
                mList.add(cellLocationData);
            }
            cursor.close();
        }
        return mList;
    }

    /**
     * Returns all data from jio_location_detail table in the form of list
     *
     * @return all the data available inside the table jio_location_detail
     */
    public List<MarkerDetail> getAllJioLocationdata() {
        mDatabase = mDBHelper.getWritableDatabase();
        List<MarkerDetail> mList = new ArrayList<>();
        String[] column = {DatabaseHelper.JIO_TIME_STAMP, DatabaseHelper.JIO_LAT, DatabaseHelper.JIO_LNG};
        Cursor cursor = mDatabase.query(DatabaseHelper.JIO_LOCATION_DETAIL, column, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MarkerDetail markerDetail = new MarkerDetail();
                markerDetail.setTimeStamp(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.JIO_TIME_STAMP)));
                markerDetail.setLat(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.JIO_LAT)));
                markerDetail.setLng(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.JIO_LNG)));
                mList.add(markerDetail);
            }
            cursor.close();
        }
        return mList;
    }

    public Long rowCountInJioLocationTable() {
        mDatabase = mDBHelper.getWritableDatabase();
        return DatabaseUtils.queryNumEntries(mDatabase, "jio_location_detail");
    }

    public void deleteLastJioLocationDetail() {
        mDatabase = mDBHelper.getWritableDatabase();
        String[] column = {DatabaseHelper.JIO_TIME_STAMP};
        Cursor cursor = mDatabase.query(DatabaseHelper.JIO_LOCATION_DETAIL, column, null, null, null, null, DatabaseHelper.JIO_TIME_STAMP + " ASC", "1");
        cursor.moveToNext();
        Long timeStamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.JIO_TIME_STAMP));
        mDatabase.delete(DatabaseHelper.JIO_LOCATION_DETAIL, DatabaseHelper.JIO_TIME_STAMP + "= '" + timeStamp + "';", null);
    }

    public void deleteAllData() {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.GOOGLE_LOCATION_DETAIL, null, null);
        mDatabase.delete(DatabaseHelper.JIO_LOCATION_DETAIL, null, null);
    }

    public void deleteDataFromCellInfo(int cellId) {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.delete(DatabaseHelper.CELL_INFO, DatabaseHelper.CELL_ID + "= '" + cellId + "';", null);
    }

}


package com.jio.rtlsappfull.internal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.model.GetLocationAPIResponse;
import com.jio.rtlsappfull.model.JiotCustomCellData;
import com.jio.rtlsappfull.model.MarkerDetail;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class JiotFetchCustomLatLng {

    Context m_context;
    String m_url;
    private static List<JiotCustomCellData> m_CustomCellDataAll = new ArrayList<>();
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    long mRtlsFetchTimeStartInMs = 0;
    long mRtlsFetchTimeEndInMs = 0;
    long CELLID = 0;
    private int MCC;
    private int MNC;
    private int LAC;
    private HashMap<String, LatLng> m_servIdLocation;
    private HashMap<String, Double> m_servIdAccuracy;
    private static ScheduledExecutorService scheduler;
    private JiotCustomCellData localCellData;
    private DBManager mDbManager;
    private int markerNumber;

    public JiotFetchCustomLatLng(Context context, String url) {
        m_context = context;
        m_url = url;
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(m_context);
        m_servIdLocation = new HashMap();
        m_servIdAccuracy = new HashMap();
        mDbManager = new DBManager(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        markerNumber = sharedPreferences.getInt("marker_number", 1);
        getLatitudeLongitudeV3(m_url);
    }

    public JSONObject createV2JsonObject(List<JiotCustomCellData> m_cellDataList) {
        JSONObject jsonCellsBody = new JSONObject();
        try {
            JSONArray jsonGsmArray = new JSONArray();
            JSONArray jsonLteArray = new JSONArray();
            JSONArray jsonWcdmaArray = new JSONArray();

            for (JiotCustomCellData cellData : m_cellDataList) {
                String s = String.valueOf(cellData.getM_mobileCountryCode());
                if (null != s && s.length() > 3) {
                    Log.d("V2RTLS", "MCC greater than 3 digits " + s);
                    continue;
                }
                JSONObject jsonPlainCellTowerData = new JSONObject();
                // MCC
                int mcc = cellData.getM_mobileCountryCode();
                if (mcc > 9 & mcc < 1000)
                    jsonPlainCellTowerData.put("mcc", mcc);
                else
                    continue;
                // MNC
                int mnc = cellData.getM_mobileNetworkCode();
                if (mnc > 9 & mnc < 1000)
                    jsonPlainCellTowerData.put("mnc", mnc);
                else
                    continue;
                //TAC
                if (cellData.getM_radioType().equalsIgnoreCase("lte")) {
                    int tac = cellData.getM_locationAreaCode();
                    if (tac >= 0 && tac <= 65536) {
                        jsonPlainCellTowerData.put("tac", tac);
                    } else {
                        continue;
                    }
                }
                // CellID
                Long cellId = cellData.getM_cellId();
                CELLID = cellId;
                if (cellId > 0)
                    jsonPlainCellTowerData.put("cellId", cellId);
                else
                    continue;
                int rssi = cellData.getM_signalStrength();
                Log.d("JioFetchCustomLatLng", String.valueOf(rssi));
                if (rssi >= -150 && rssi <= 0)
                    jsonPlainCellTowerData.put("rssi", cellData.getM_signalStrength());
                else
                    continue;
                if (cellData.getM_radioType().equalsIgnoreCase("lte")) {
                    int frequency = cellData.getFrequency();
                    if (frequency >= 1 && frequency <= 99999999)
                        jsonPlainCellTowerData.put("frequency", frequency);
                    else
                        continue;
                }

                if (cellData.getM_radioType().equalsIgnoreCase("gsm")) {
                    jsonGsmArray.put(jsonPlainCellTowerData);
                } else if (cellData.getM_radioType().equalsIgnoreCase("wcdma")) {
                    jsonWcdmaArray.put(jsonPlainCellTowerData);
                } else if (cellData.getM_radioType().equalsIgnoreCase("lte")) {
                    jsonLteArray.put(jsonPlainCellTowerData);
                }

                JSONObject testLocation = new JSONObject();
                testLocation.put("lat", JiotUtils.sLang);
                testLocation.put("lng", JiotUtils.slon);
                Log.i("Sent Lat/Lng", JiotUtils.sLang + ", " + JiotUtils.slon);
                jsonCellsBody.put("gps", testLocation);
            }
            jsonCellsBody.put("ltecells", jsonLteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("V2RTLS", jsonCellsBody.toString());
        return jsonCellsBody;
    }

    private void saveMarkerDetail() {
        MarkerDetail markerDetail = new MarkerDetail();
        if (JiotUtils.slon == 0.0 && JiotUtils.sLang == 0.0)
            return;
        markerDetail.setLat(JiotUtils.sLang);
        markerDetail.setLng(JiotUtils.slon);
        markerDetail.setTimeStamp(System.currentTimeMillis());
        if (checkIfDataReachedUpperLimitInDB())
            mDbManager.insertLocationDetailInDatabase(markerDetail);
        else {
            deleteLastInsertedDataAndInsert(markerDetail);
        }
    }

    private boolean checkIfDataReachedUpperLimitInDB() {
        Long rowCount = mDbManager.rowCountInTable();
        if (rowCount > markerNumber - 1)
            return false;
        else
            return true;
    }

    private boolean checkIfDataReachedUpperLimitInJioLocationDB() {
        Long rowCount = mDbManager.rowCountInJioLocationTable();
        if (rowCount > markerNumber - 1)
            return false;
        else
            return true;
    }

    private void deleteLastInsertedDataAndInsert(MarkerDetail markerDetail) {
        mDbManager.deleteLastLocationDetail();
        mDbManager.insertLocationDetailInDatabase(markerDetail);
    }

    private void deleteLastInsertedDataAndInsertInJioLocationTable(MarkerDetail markerDetail) {
        mDbManager.deleteLastJioLocationDetail();
        mDbManager.insertJioLocationDetailInDatabase(markerDetail);
    }

    private void saveJioMarkers(double lat, double lng) {
        if (lat == 0.0 && lng == 0.0)
            return;
        MarkerDetail markerDetail = new MarkerDetail();
        markerDetail.setLat(lat);
        markerDetail.setLng(lng);
        markerDetail.setTimeStamp(System.currentTimeMillis());
        if (checkIfDataReachedUpperLimitInJioLocationDB())
            mDbManager.insertJioLocationDetailInDatabase(markerDetail);
        else {
            deleteLastInsertedDataAndInsertInJioLocationTable(markerDetail);
        }
    }

    public void sendAllLocationsToMaps(JSONObject response) {
        try {
            Double lat = response.getDouble("lat");
            Double lng = response.getDouble("lng");
            Log.i("Received Location", lat + ", " + lng);
            saveJioMarkers(lat, lng);
            LatLng newLatLng = new LatLng(lat, lng);
            m_servIdLocation.put("test-104", newLatLng);
            Intent intent = new Intent();
            intent.setAction("com.rtls.location_all");
            intent.putExtra("CELLID", CELLID);
            intent.putExtra("ALLPINS", m_servIdLocation);
            m_context.sendBroadcast(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendEmptyLocationsToMaps() {
        m_servIdLocation.clear();
        m_servIdAccuracy.clear();
        Intent intent = new Intent();
        intent.setAction("com.rtls.location_all");
        intent.putExtra("ALLPINS", m_servIdLocation);
        intent.putExtra("ALLACC", m_servIdAccuracy);
        m_context.sendBroadcast(intent);
    }

    private void sendRefreshToken() {
        Intent intent = new Intent();
        intent.setAction("com.rtls.token_error");
        m_context.sendBroadcast(intent);
    }


    public void getLatitudeLongitudeV3(final String url) {
        getCellInfo();
        try {
            if (m_CustomCellDataAll == null || m_CustomCellDataAll.isEmpty()) {
                return;
            }
            final JSONObject jsonMainBody = createV2JsonObject(m_CustomCellDataAll);
//            saveMarkerDetail();
            if (!jsonMainBody.toString().equalsIgnoreCase("{}")) {
                RequestQueue queue = Volley.newRequestQueue(m_context);
                Log.d("URL --> ", url);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, response -> {
                    try {
                        mRtlsFetchTimeEndInMs = Calendar.getInstance().getTimeInMillis();
                        String message = response.optString("msg");
                        m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSAPILATENCY" + " $ " + JiotUtils.getDateTime() + " $ " + "Success response --> " + response.toString() + " ms ");
                        Log.d("Success Respnse --> ", message);
                        if (message != null && !message.isEmpty()) {
                            Log.d("Error ", message);
                            return;
                        }
                        sendAllLocationsToMaps(response);
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                        e.printStackTrace();
                    }
                }, error -> {
                    String errorMsg = JiotUtils.getVolleyError(error);
                    m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSAPILATENCY" + " $ " + JiotUtils.getDateTime() + " $ " + "Success response --> " + error.toString() + " ms ");
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401
                            && !JiotUtils.isTokenExpired) {
                        JiotUtils.isTokenExpired = true;
                        GetLocationAPIResponse getLocationAPIResponse = JiotUtils.getInstance().getPojoObject(errorMsg, GetLocationAPIResponse.class);
                        if (getLocationAPIResponse.getError() != null && getLocationAPIResponse.getError().getCode() == 404
                                && getLocationAPIResponse.getError().getMessage().equalsIgnoreCase("Location Not Found")) {
                            mDbManager.insertCellInfoInDB(jsonMainBody);
                        }
                        sendRefreshToken();
                    }
                    Log.e("MSGFROMSERVER", "FAILURE " + errorMsg);
                    sendEmptyLocationsToMaps();
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        headers.put("token", JiotUtils.jiotgetRtlsToken(m_context));
                        headers.put("msisdn", JiotUtils.getMobileNumber(m_context));
                        return headers;
                    }
                };
                mRtlsFetchTimeStartInMs = Calendar.getInstance().getTimeInMillis();
                queue.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCellInfo() {
        m_CustomCellDataAll.clear();
        TelephonyManager m_telephonyManager = (TelephonyManager) m_context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = m_telephonyManager.getNetworkOperator();
        getMccMnc(networkOperator);
        if (ActivityCompat.checkSelfPermission(m_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
        if (cellLocation != null) {
            for (CellInfo info : cellLocation) {
                if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    Log.d("Cell Info", String.valueOf(info));
                    boolean isLtePrim = info.isRegistered();
                    if (isLtePrim) {
                        JiotUtils.isLtePrimary = true;
                    }
                    setLteScannedCells(isLtePrim, lte, identityLte);
                }
            }
        }
    }

    public void getMccMnc(String networkOperator) {
        if (networkOperator != null) {
            if (networkOperator.length() > 3) {
                MCC = Integer.parseInt(networkOperator.substring(0, 3));
                MNC = Integer.parseInt(networkOperator.substring(3));
            }
            Log.d("MCCMNC", MCC + "::" + MNC);
        }
    }

    public void setLteScannedCells(boolean isLtePrim, final CellSignalStrengthLte lte,
                                   final CellIdentityLte identityLte) {
        localCellData = new JiotCustomCellData();
        localCellData.setM_isPrimary(isLtePrim);
        localCellData.setM_radioType("lte");
        localCellData.setM_signalStrength(lte.getDbm());
        localCellData.setM_cellId(identityLte.getCi());
        localCellData.setM_psc(identityLte.getPci());
        localCellData.setM_mobileCountryCode(identityLte.getMcc());
        localCellData.setM_mobileNetworkCode(identityLte.getMnc());
        localCellData.setM_locationAreaCode(identityLte.getTac());
        localCellData.setM_timingAdvance(lte.getTimingAdvance());
        localCellData.setM_frequency(identityLte.getEarfcn());
        Log.d("V2RTLS", "LTE MCC, MNC, cell id " + identityLte.getMcc() + " " + identityLte.getMnc() + " " + identityLte.getCi());
        m_CustomCellDataAll.add(localCellData);
    }

}

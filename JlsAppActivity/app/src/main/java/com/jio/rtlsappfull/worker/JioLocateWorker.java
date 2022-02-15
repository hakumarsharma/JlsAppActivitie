package com.jio.rtlsappfull.worker;

import static com.jio.rtlsappfull.config.Config.LOCATION_PREPROD_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_PREPROD_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_PRE_PROD;
import static com.jio.rtlsappfull.config.Config.SUBMIT_CELL_LOCATION_PREPROD;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.jio.rtlsappfull.config.Config;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.model.CellLocationData;
import com.jio.rtlsappfull.model.JiotCustomCellData;
import com.jio.rtlsappfull.model.SubmitAPIData;
import com.jio.rtlsappfull.model.SubmitApiDataResponse;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@RequiresApi(api = Build.VERSION_CODES.N)
public class JioLocateWorker extends Worker {

    private Context mContext;
    private List<JiotCustomCellData> m_CustomCellDataAll;
    private int MCC = 0;
    private int MNC = 0;
    private DBManager mDbManager;
    private HashMap<String, LatLng> m_servIdLocation;
    private long CELLID = 0;
    private String TAG = "Worker Thread";
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;

    public JioLocateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        m_CustomCellDataAll = new ArrayList<>();
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(context);
        mDbManager = new DBManager(mContext);
        m_servIdLocation = new HashMap<>();
    }

    @NonNull
    @Override
    public Result doWork() {
        makeLocationCall(LOCATION_PREPROD_URL);
        return Result.success();
    }

    private void makeLocationCall(String url) {
        getCurrentLocation();
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if(!powerManager.isDeviceIdleMode()) {
            JSONObject jsonMainBody = createV2JsonObject();
            RequestQueue queue = Volley.newRequestQueue(mContext);
            Log.d(TAG, "called " + url + "Body " + jsonMainBody);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, response -> {
                try {
                    String message = response.optString("msg");
                    Log.d(TAG, url + " called succesfully");
                    m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + url + " Success response --> " + response.toString());
                    if (message != null && !message.isEmpty()) {
                        Log.d("Error ", message);
                        return;
                    }
                    makeApiCall();
                    sendAllLocationsToMaps(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d(TAG, url + " call Failed");
                String errorMsg = JiotUtils.getVolleyError(error);
                m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + url + " API call Failed --> " + errorMsg);
                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    JiotUtils.isTokenExpired = true;
                    fetchRtlsKey(JiotUtils.getMobileNumber(mContext));
                }
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    mDbManager.insertCellInfoInDB(jsonMainBody);
                    makeSubmitCellLocationApiCall();
                }
                sendEmptyLocationsToMaps();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("token", JiotUtils.jiotgetRtlsToken(mContext));
                    headers.put("msisdn", JiotUtils.getMobileNumber(mContext));
                    return headers;
                }
            };
            queue.add(req);
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                JiotUtils.sLang = locationResult.getLastLocation().getLatitude();
                JiotUtils.slon = locationResult.getLastLocation().getLongitude();
                Intent intent = new Intent();
                intent.setAction("com.rtls.google_location");
                mContext.sendBroadcast(intent);
            }
        }
    };

    public JSONObject createV2JsonObject() {
        TelephonyManager m_telephonyManager = (TelephonyManager) mContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        JSONObject jsonCellsBody = new JSONObject();
        List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
        JSONArray jsonLteArray = new JSONArray();
        try {
            if (cellLocation != null) {
                for (CellInfo info : cellLocation) {
                    if (info instanceof CellInfoLte) {
                        final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                        JSONObject jsonPlainCellTowerData = new JSONObject();
                        if (identityLte.getCi() > 0) {
                            // MCC
                            int mcc = identityLte.getMcc();
                            if (mcc > 9 & mcc < 1000)
                                jsonPlainCellTowerData.put("mcc", mcc);
                            else
                                continue;
                            // MNC
                            int mnc = identityLte.getMnc();
                            if (mnc > 9 & mnc < 1000)
                                jsonPlainCellTowerData.put("mnc", mnc);
                            else
                                continue;
                            // Tac
                            int tac = identityLte.getTac();
                            if (tac >= 0 && tac <= 65536) {
                                jsonPlainCellTowerData.put("tac", tac);
                            } else {
                                continue;
                            }
                            // CellId
                            int cellId = identityLte.getCi();
                            CELLID = cellId;
                            jsonPlainCellTowerData.put("cellId", cellId);
                            // RSSI
                            int rssi = lte.getDbm();
                            if (rssi >= -150 && rssi <= 0)
                                jsonPlainCellTowerData.put("rssi", rssi);
                            else
                                continue;
                            // Frequency
                            int frequency = identityLte.getEarfcn();
                            if (frequency >= 1 && frequency <= 99999999)
                                jsonPlainCellTowerData.put("frequency", frequency);
                            else
                                continue;
                            jsonLteArray.put(jsonPlainCellTowerData);
                        }
                    }
                }
            }
            JSONObject testLocation = new JSONObject();
            testLocation.put("lat", JiotUtils.sLang);
            testLocation.put("lng", JiotUtils.slon);
            jsonCellsBody.put("gps", testLocation);
            jsonCellsBody.put("ltecells", jsonLteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonCellsBody;
    }

    private void makeSubmitCellLocationApiCall() {
        List<CellLocationData> cellLocationDataList = mDbManager.getAllCellInfoData();
        if (cellLocationDataList.size() > 0) {
            for (CellLocationData cellLocationData : cellLocationDataList) {
                String cellData = cellLocationData.getCellId()
                        + " " + cellLocationData.getLat()
                        + " " + cellLocationData.getLng()
                        + " " + cellLocationData.getTimestamp()
                        + " " + cellLocationData.getMcc()
                        + " " + cellLocationData.getMnc()
                        + " " + cellLocationData.getRssi()
                        + " " + cellLocationData.getTac();
                JSONObject jsonObject = makeJsonObject(cellData);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String[] arr = cellData.split(" ");
                int cellId = Integer.parseInt(arr[0]);
                Log.d(TAG, SUBMIT_CELL_LOCATION_PREPROD);
                Log.d(TAG, "request body " + SUBMIT_CELL_LOCATION_PREPROD);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SUBMIT_CELL_LOCATION_PREPROD, jsonObject, response -> {
                    try {
                        Log.d(TAG, SUBMIT_CELL_LOCATION_PREPROD + "Called successfully");
                        m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + SUBMIT_CELL_LOCATION_PREPROD + " Called successfully, response --> " + response.toString());
                        SubmitApiDataResponse submitCellDataResponse = JiotUtils.getInstance().getPojoObject(String.valueOf(response), SubmitApiDataResponse.class);
                        List<SubmitApiDataResponse.LteCellsInfo> ltecells = submitCellDataResponse.getLtecells();
                        if (ltecells.get(0) != null && ltecells.get(0).getMessage() != null
                                && ltecells.get(0).getMessage().getDetails().getSuccess().getCode() == 200
                                && (ltecells.get(0).getMessage().getDetails().getSuccess().getMessage().equalsIgnoreCase("A new cell tower location submitted")
                                || ltecells.get(0).getMessage().getDetails().getSuccess().getMessage().equalsIgnoreCase("Cell tower location updated"))) {
                            Toast.makeText(mContext, "Cell info " + cellId + "submitted/updated", Toast.LENGTH_SHORT).show();
                            mDbManager.deleteDataFromCellInfo(cellId);
                        }
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                        e.printStackTrace();
                    }
                }, error -> {
                    String errorMsg = JiotUtils.getVolleyError(error);
                    m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + SUBMIT_CELL_LOCATION_PREPROD + " Submit cell location API call failed --> " + errorMsg);
                    Log.d(TAG, SUBMIT_CELL_LOCATION_PREPROD + "Submit cell location API call failed" + errorMsg);
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        headers.put("token", JiotUtils.jiotgetRtlsToken(mContext));
                        return headers;
                    }
                };
                queue.add(req);
            }
        }
    }

    private JSONObject makeJsonObject(String cellData) {
        String[] markerDetailArray = cellData.split(" ");
        JSONObject lteCellsObject = new JSONObject();
        try {
            JSONArray lteCellsArray = new JSONArray();
            JSONObject gpsJsonObject = new JSONObject();
            JSONObject cellInfoObject = new JSONObject();
            cellInfoObject.put("mcc", Integer.parseInt(markerDetailArray[4]));
            cellInfoObject.put("mnc", Integer.parseInt(markerDetailArray[5]));
            cellInfoObject.put("tac", Integer.parseInt(markerDetailArray[7]));
            cellInfoObject.put("cellid", Integer.parseInt(markerDetailArray[0]));
            gpsJsonObject.put("lat", Double.valueOf(markerDetailArray[1]));
            gpsJsonObject.put("lng", Double.valueOf(markerDetailArray[2]));
            cellInfoObject.put("gpsloc", gpsJsonObject);
            lteCellsArray.put(cellInfoObject);
            lteCellsObject.put("ltecells", lteCellsArray);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return lteCellsObject;
    }

    public void sendAllLocationsToMaps(JSONObject response) {
        try {
            Double lat = response.getDouble("lat");
            Double lng = response.getDouble("lng");
            Log.i("Received Location", lat + ", " + lng);
            LatLng newLatLng = new LatLng(lat, lng);
            m_servIdLocation.put("test-104", newLatLng);
            Intent intent = new Intent();
            intent.setAction("com.rtls.location_all");
            intent.putExtra("CELLID", CELLID);
            intent.putExtra("ALLPINS", m_servIdLocation);
            mContext.sendBroadcast(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendEmptyLocationsToMaps() {
        m_servIdLocation.clear();
        Intent intent = new Intent();
        intent.setAction("com.rtls.location_all");
        intent.putExtra("ALLPINS", m_servIdLocation);
        mContext.sendBroadcast(intent);
    }

    private void makeApiCall() {
        try {
            SubmitAPIData submitAPIData = new SubmitAPIData();
            TelephonyManager m_telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            List<SubmitAPIData.LteCells> mList = new ArrayList();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
            List<Integer> mncList = new ArrayList<>();
            addMncList(mncList);
            for (CellInfo info : cellLocation) {
                if (info instanceof CellInfoLte) {
                    CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    int mnc = identityLte.getMnc();
                    int rssi = lte.getDbm();
                    int tac = identityLte.getTac();
                    int cellId = identityLte.getCi();
                    int mcc = identityLte.getMcc();
                    int frequency = identityLte.getEarfcn();
                    if ((rssi >= -150 && rssi <= 0)
                            && (mnc > 9 && mnc < 1000)
                            && (tac >= 0 && tac <= 65536)
                            && (cellId > 0)
                            && (mcc > 9 & mcc < 1000)
                            && (frequency >= 1 && frequency <= 99999999)) {
                        SubmitAPIData.LteCells cell = new SubmitAPIData().new LteCells();
                        cell.setCellid(cellId);
                        cell.setFrequency(frequency);
                        cell.setMcc(mcc);
                        cell.setRssi(rssi);
                        cell.setTac(tac);
                        cell.setMnc(mnc);
                        if (mcc == 405 && mncList.contains(mnc)) {
                            mList.add(cell);
                        }
                    }
                }
            }
            SubmitAPIData.GpsLoc gpsLoc = new SubmitAPIData().new GpsLoc();
            gpsLoc.setLat(JiotUtils.sLang);
            gpsLoc.setLng(JiotUtils.slon);
            submitAPIData.setLtecells(mList);
            submitAPIData.setGpsloc(gpsLoc);
            String jsonInString = new Gson().toJson(submitAPIData);
            JSONObject jsonMainBody = new JSONObject(jsonInString);
            String url = SUBMIT_API_URL_PRE_PROD;
            Log.i(TAG, " Submit API Url " + url);
            Log.i(TAG, " Submit API body " + jsonInString);
            if (!jsonMainBody.toString().equalsIgnoreCase("{}")) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, response -> {
                    try {
                        Log.i(TAG, url + " Called succesfully");
                        m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + url + "Submit API success response --> " + response.toString());
                        SubmitApiDataResponse submitCellDataResponse = JiotUtils.getInstance().getPojoObject(String.valueOf(response), SubmitApiDataResponse.class);
                        List<SubmitApiDataResponse.LteCellsInfo> ltecells = submitCellDataResponse.getLtecells();
                        if (ltecells.get(0) != null && ltecells.get(0).getMessage() != null
                                && ltecells.get(0).getMessage().getDetails().getSuccess().getCode() == 200
                                && (ltecells.get(0).getMessage().getDetails().getSuccess().getMessage().equalsIgnoreCase("A new cell tower location submitted")
                                || ltecells.get(0).getMessage().getDetails().getSuccess().getMessage().equalsIgnoreCase("Cell tower location updated"))) {
                            Toast.makeText(mContext, "Cell Info submitted", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                        e.printStackTrace();
                    }
                }, error -> {
                    String errorMsg = JiotUtils.getVolleyError(error);
                    m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + url + " Submit API failed" + errorMsg);
                    Log.i(TAG, url + " Submit API failed" + errorMsg);
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        headers.put("token", JiotUtils.jiotgetRtlsToken(mContext));
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        String number = sharedPreferences.getString("mob", null);
                        if (number != null) {
                            headers.put("msisdn", number);
                        }
                        return headers;
                    }
                };
                queue.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMncList(List<Integer> mncList) {
        mncList.add(840);
        for (int i = 854; i <= 874; i++) {
            mncList.add(i);
        }
    }

    public void fetchRtlsKey(String publickey) {
        if (JiotUtils.jiotisLocationEnabled(mContext) == true && JiotUtils.jiotisNetworkEnabled(mContext)) {
            JSONObject jsonMainBody = createV2UserIdObject(publickey);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            Log.d(TAG, SERVER_PREPROD_API_KEY_URL);
            Log.d(TAG, jsonMainBody.toString());
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SERVER_PREPROD_API_KEY_URL, jsonMainBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(SERVER_PREPROD_API_KEY_URL, "API called successfully");
                        m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + SERVER_PREPROD_API_KEY_URL + " API called successfully");
                        if (response.has("data")) {
                            JSONObject dataObject = response.getJSONObject("data");
                            if (dataObject.has("apikey")) {
                                JiotUtils.jiotwriteRtlsToken(getApplicationContext(), dataObject.get("apikey").toString());
                                JiotUtils.isTokenExpired = false;
                                makeLocationCall(LOCATION_PREPROD_URL);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("RTLSTOKEN", "EXCEPTION");
                        e.printStackTrace();
                    }
                }
            }, error -> {
                String errorMsg = JiotUtils.getVolleyError(error);
                Log.d(SERVER_PREPROD_API_KEY_URL, "API call failed");
                m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile(JiotUtils.getDateTime() + SERVER_PREPROD_API_KEY_URL + " API call failed");
                Log.d(TAG, errorMsg);
                JiotUtils.isTokenExpired = true;
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(req);
        } else
            Toast.makeText(getApplicationContext(), "App requires location to be enabled and internet to be enabled", Toast.LENGTH_SHORT).show();
    }

    public JSONObject createV2UserIdObject(String name) {
        try {
            JSONObject jsonUserIdObj = new JSONObject();
            JSONObject jsonServiceId = new JSONObject();
            JSONObject jsonIDType = new JSONObject();
            String number = "00000" + name;
            char[] chars = new char[number.length()];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = toHex(
                        fromHex(number.charAt(i)) ^
                                fromHex(Config.PREPROD_ID1.charAt(i)));
            }
            String encode_msisdn = String.valueOf(chars);

            jsonIDType.put("value", Config.PREPROD_ID2 + encode_msisdn);
            jsonIDType.put("type", "100");
            jsonUserIdObj.put("id", jsonIDType);

            jsonServiceId.put("id", "101");
            jsonServiceId.put("name", "rtls");
            jsonUserIdObj.put("service", jsonServiceId);

            Log.d("USERIDJSON", jsonUserIdObj.toString());
            return jsonUserIdObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException();
    }

    public static char toHex(int nybble) {
        if (nybble < 0 || nybble > 15) {
            throw new IllegalArgumentException();
        }
        return "0123456789abcdef".charAt(nybble);
    }

}

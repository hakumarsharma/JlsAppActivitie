package com.jio.rtlsappfull.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.model.JiotRtlsLocationRecord;
import com.jio.rtlsappfull.model.MarkerDetail;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    public static LatLng m_rtlsMlsMozLatLng = null;
    public static LatLng m_rtlsCombainLatLng = null;
    public static LatLng m_rtlsSkyLatLng = null;
    public static LatLng m_rtlsTriLatLng = null;

    public static Double m_rtlsMlsMozAcc = 0.0;
    public static Double m_rtlsMlsJioAcc = 0.0;
    public static Double m_rtlsCombainAcc = 0.0;
    public static Double m_rtlsSkyAcc = 0.0;
    public static Double m_rtlsTriAcc = 0.0;

    public static LatLng m_currentLatLng = null;
    public static LatLng m_mozillaLatLng;
    public static Double m_RtlsLatitude = 12.8;
    public static Double m_RtlsLongitude = 77.4;
    public static Double m_RtlsAccuracy = 55.2;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInst = null;
    public static Circle m_accuracyCircle = null;
    public static long m_primaryCellId = 0;
    public static Context m_context = null;
    public static JSONObject m_jsonRequest = null;
    public static JiotRtlsLocationRecord m_currentLocationRecord;
    public static Handler m_readLocHandler = null;
    HashMap<String, LatLng> m_serviceIdLocation;
    HashMap<String, Double> m_serviceIdAcc;

    private static boolean m_rtlsMlsMozLatLngFlag = false;
    private static boolean m_rtlsMlsJioLatLngFlag = false;
    private static boolean m_rtlsCombainLatLngFlag = false;
    private static boolean m_rtlsSkyLatLngFlag = false;
    private static boolean m_rtlsTriLatLngFlag = false;

    private static DBManager mDbManager;

    public static void sendRecordsRefreshIntent() {
        Intent intent = new Intent();
        intent.setAction("com.jio.refreshRtls");
        m_context.sendBroadcast(intent);
    }

    public static Marker getRtlsSkyMarker() {
        Marker samMarker = mMap.addMarker(new MarkerOptions().position(m_rtlsSkyLatLng).title("Jio latest location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        return samMarker;
    }

    public static Marker getRtlsTriMarker() {
        Marker samMarker = mMap.addMarker(new MarkerOptions().position(m_rtlsTriLatLng).title("Jio latest location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        Log.i("Printed Lat/Lng", m_rtlsTriLatLng.toString());
        return samMarker;
    }

    public static Marker getRtlsMlsMozMarker() {
        Marker samMarker = mMap.addMarker(new MarkerOptions().position(m_rtlsMlsMozLatLng).title("Jio latest location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        return samMarker;
    }

    public static void setRtlsLatestMarkers() {
        if (m_rtlsSkyLatLngFlag)
            getRtlsSkyMarker();
        else if (m_rtlsTriLatLngFlag)
            getRtlsTriMarker();
        else if (m_rtlsMlsMozLatLngFlag)
            getRtlsMlsMozMarker();
        float zoom = 15;
        if (m_rtlsSkyLatLngFlag)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m_rtlsSkyLatLng, zoom));
        else if (m_rtlsTriLatLngFlag)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m_rtlsTriLatLng, zoom));
        else if (m_rtlsMlsMozLatLngFlag)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m_rtlsMlsMozLatLng, zoom));
        sendRecordsRefreshIntent();
    }

    public static void setGoogleLatestMarker(LatLng googleLatLng) {
        mMap.addMarker(new MarkerOptions().position(googleLatLng).title("Google latest location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(googleLatLng, 15));
    }

    public void parseLatLngAll() {
        m_rtlsMlsMozLatLngFlag = false;
        m_rtlsMlsJioLatLngFlag = false;
        m_rtlsCombainLatLngFlag = false;
        m_rtlsSkyLatLngFlag = false;
        m_rtlsTriLatLngFlag = false;
        if (m_serviceIdLocation != null) {
            if (!m_serviceIdLocation.isEmpty()) {
                for (Map.Entry mapElement : m_serviceIdLocation.entrySet()) {
                    String serviceId = (String) mapElement.getKey();
                    LatLng latLngValue = (LatLng) mapElement.getValue();
                    switch (serviceId) {
                        case JiotUtils.RTLS_SERVICE_ID_MLS_MOZ:
                            Log.d("parseLatLngAll = ", " " + JiotUtils.RTLS_SERVICE_ID_MLS_MOZ);
                            m_rtlsMlsMozLatLngFlag = true;
                            m_rtlsMlsMozLatLng = latLngValue;
                            if (m_rtlsMlsJioLatLngFlag == false) {
                                m_RtlsLatitude = m_rtlsMlsMozLatLng.latitude;
                                m_RtlsLongitude = m_rtlsMlsMozLatLng.longitude;
                                m_mozillaLatLng = new LatLng(m_RtlsLatitude, m_RtlsLongitude);
                            }
                            break;
                        case JiotUtils.RTLS_SERVICE_ID_MLS_JIO:
                            break;
                        case JiotUtils.RTLS_SERVICE_ID_COMBAIN:
                            Log.d("parseLatLngAll = ", " " + JiotUtils.RTLS_SERVICE_ID_COMBAIN);
                            m_rtlsCombainLatLngFlag = true;
                            m_rtlsCombainLatLng = latLngValue;
                            break;
                        case JiotUtils.RTLS_SERVICE_ID_SKY:
                            Log.d("parseLatLngAll = ", " " + JiotUtils.RTLS_SERVICE_ID_SKY);
                            m_rtlsSkyLatLngFlag = true;
                            m_rtlsSkyLatLng = latLngValue;
                            break;
                        case JiotUtils.RTLS_SERVICE_ID_TRI:
                            Log.d("parseLatLngAll = ", " " + JiotUtils.RTLS_SERVICE_ID_TRI);
                            m_rtlsTriLatLngFlag = true;
                            m_rtlsTriLatLng = latLngValue;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void parseAllLocationPins(Intent intent) {
        m_primaryCellId = intent.getLongExtra("CELLID", 0);
        m_serviceIdLocation = (HashMap<String, LatLng>) intent.getSerializableExtra("ALLPINS");
        parseLatLngAll();
        Runnable m_showMaps = () -> jiotreadLocationDetails();
        m_readLocHandler.postDelayed(m_showMaps, 100);
    }

    public BroadcastReceiver m_MozillaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MAPSRE", "onReceive MAPS");
            if (intent.getAction().equalsIgnoreCase("com.rtls.location_all")) {
                m_primaryCellId = intent.getLongExtra("CELLID", 0);
                Log.d("m_primaryCellId = ", m_primaryCellId + "");
                parseAllLocationPins(intent);
            }
        }
    };

    public MapFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter intentFilterLocation = new IntentFilter("com.rtls.location");
        intentFilterLocation.addAction("com.rtls.location_all");
        getActivity().registerReceiver(m_MozillaReceiver, intentFilterLocation);
        m_context = getContext();
        m_currentLocationRecord = new JiotRtlsLocationRecord();
        m_readLocHandler = new Handler();
        mDbManager = new DBManager(getActivity());
        mDbManager.deleteAllData();
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        m_jiotSdkFileLoggerInst = JiotSdkFileLogger.JiotGetFileLoggerInstance(getContext());
    }

    public static LatLng jiotreadLocationDetails() {
        mMap.clear();
        m_currentLatLng = new LatLng(JiotUtils.sLang, JiotUtils.slon);
        setGoogleLatestMarker(m_currentLatLng);
        setRtlsLatestMarkers();
        setGoogleMarkers();
        setJioMarkers();
        return m_currentLatLng;
    }

    private static void setGoogleMarkers() {
        List<MarkerDetail> mList = mDbManager.getAllLocationdata();
        ArrayList<LatLng> locationArrayList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            LatLng latLng = new LatLng(mList.get(i).getLat(), mList.get(i).getLng());
            locationArrayList.add(latLng);
        }
        for (int i = 0; i < locationArrayList.size() - 1; i++)
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Google historical location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(0.7f));
    }

    private static void setJioMarkers() {
        List<MarkerDetail> mList = mDbManager.getAllJioLocationdata();
        ArrayList<LatLng> locationArrayList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            LatLng latLng = new LatLng(mList.get(i).getLat(), mList.get(i).getLng());
            locationArrayList.add(latLng);
        }
        for (int i = 0; i < locationArrayList.size() - 1; i++)
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Jio historical location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).alpha(0.7f));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(m_MozillaReceiver);
            Log.d("UNREG", "onDestory maps");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UNREGEX", "Exception onDestory maps");
        }
        m_jiotSdkFileLoggerInst = null;
    }
}

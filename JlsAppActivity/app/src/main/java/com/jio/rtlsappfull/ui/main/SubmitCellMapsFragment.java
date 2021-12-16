package com.jio.rtlsappfull.ui.main;

import static com.jio.rtlsappfull.config.Config.LOCATION_DEV_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_PREPROD_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_PROD_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_SIT_URL;
import static com.jio.rtlsappfull.config.Config.SUBMIT_CELL_LOCATION_DEV;
import static com.jio.rtlsappfull.config.Config.SUBMIT_CELL_LOCATION_PREPROD;
import static com.jio.rtlsappfull.config.Config.SUBMIT_CELL_LOCATION_PROD;
import static com.jio.rtlsappfull.config.Config.SUBMIT_CELL_LOCATION_SIT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.model.CellLocationData;
import com.jio.rtlsappfull.model.GetLocationAPIResponse;
import com.jio.rtlsappfull.model.SubmitApiDataResponse;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitCellMapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private DBManager mDbManager;

    private OnMapReadyCallback callback = googleMap -> {
        List<CellLocationData> allCellInfoata = mDbManager.getAllCellInfoata();
        googleMap.setOnMarkerClickListener(this);
        showMarkersOnMap(allCellInfoata, googleMap);
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_submit_cell_maps, container, false);
        mDbManager = new DBManager(getActivity());
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void showMarkersOnMap(List<CellLocationData> cellLocationDataList, GoogleMap googleMap) {
        for (CellLocationData cellLocationData : cellLocationDataList) {
            LatLng latLng = new LatLng(cellLocationData.getLat(), cellLocationData.getLng());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(cellLocationData.getCellId()
                    + " " + cellLocationData.getLat()
                    + " " + cellLocationData.getLng()
                    + " " + cellLocationData.getTimestamp()
                    + " " + cellLocationData.getMcc()
                    + " " + cellLocationData.getMnc()
                    + " " + cellLocationData.getRssi()
                    + " " + cellLocationData.getTac()
                    + " " + cellLocationData.getFrequency()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String markerDetail = marker.getTitle();
        makeSubmitCellLocation(markerDetail);
        Toast.makeText(getActivity(), markerDetail, Toast.LENGTH_SHORT).show();
        return true;
    }

    private void makeSubmitCellLocation(String markerDetail) {
        final JSONObject jsonMainBody = makeJsonObject(markerDetail);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String [] arr = markerDetail.split("");
        int cellId = Integer.parseInt(arr[0]);
        String url = "";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String serverName = sharedPreferences.getString("server_name", null);
        if (serverName.equalsIgnoreCase("Prod")) {
            url = SUBMIT_CELL_LOCATION_PROD;
        } else if (serverName.equalsIgnoreCase("Sit")) {
            url = SUBMIT_CELL_LOCATION_SIT;
        } else if (serverName.equalsIgnoreCase("Dev")) {
            url = SUBMIT_CELL_LOCATION_DEV;
        } else if (serverName.equalsIgnoreCase("Preprod")) {
            url = SUBMIT_CELL_LOCATION_PREPROD;
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, response -> {
            try {
                SubmitApiDataResponse submitApiDataResponse = JiotUtils.getInstance().getPojoObject(String.valueOf(response), SubmitApiDataResponse.class);
                if (submitApiDataResponse.getDetails().getSuccess().getCode() == 200
                        && (submitApiDataResponse.getDetails().getSuccess().getMessage().equalsIgnoreCase("A new cell tower location submitted"))
                        || (submitApiDataResponse.getDetails().getSuccess().getMessage().equalsIgnoreCase("Cell tower location updated"))) {
                    Toast.makeText(getActivity(), "Cell info " + cellId + "submitted/updated", Toast.LENGTH_SHORT).show();
                    mDbManager.deleteDataFromCellInfo(cellId);
                }
            } catch (Exception e) {
                Log.d("EXCEPTION", "exce");
                e.printStackTrace();
            }
        }, (Response.ErrorListener) error -> {
            String errorMsg = JiotUtils.getVolleyError(error);
            Toast.makeText(getActivity(), "Cell info submission failed " + errorMsg, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("token", JiotUtils.jiotgetRtlsToken(getActivity()));
                return headers;
            }
        };
        queue.add(req);
    }

    private JSONObject makeJsonObject(String markerDetail) {
        String[] markerDetailArray = markerDetail.split(" ");
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

}
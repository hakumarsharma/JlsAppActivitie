package com.jio.rtlsappfull.internal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.jio.rtlsappfull.config.Config.JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL;
import static com.jio.rtlsappfull.config.Config.isJioVm;

public class WifiDetails extends AppCompatActivity {

    private TextView mHeaderText;
    private TextView mSkipText;
    private EditText mHouseNumber;
    private EditText mFloor;
    private EditText mStreet;
    private EditText mCity;
    private EditText mState;
    private EditText mPinCode;
    private TextView mWifiUsage;
    private RadioGroup mWifiUsageRadioGrp;
    private RadioButton mWifiCommercial;
    private RadioButton mWifiRes;
    private EditText mWifiOwner;
    private Button mNextButton;
    private String mBssid;
    private String mSsid;
    private int mRssi;
    private int mFreq;
    private EditText mLandmark;
    private EditText mRoomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);

        mHeaderText = (TextView) findViewById(R.id.detail_hdr_text);
        mHeaderText.setTypeface(JiotUtils.mTypeface(this,5 ));

        mBssid = getIntent().getStringExtra("wbs");
        mSsid = getIntent().getStringExtra("ws");
        mRssi = getIntent().getIntExtra("rs", 0);
        mFreq = getIntent().getIntExtra("fr", 0);

        mSkipText = (TextView) findViewById(R.id.SkipBtn);
        mSkipText.setTypeface(JiotUtils.mTypeface(this, 5));
        mSkipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendJson();
            }
        });

        mHouseNumber = (EditText) findViewById(R.id.house_num);
        mFloor = (EditText) findViewById(R.id.floor_num);
        mStreet = (EditText) findViewById(R.id.street);
        mCity = (EditText) findViewById(R.id.city);
        mState = (EditText) findViewById(R.id.state);
        mPinCode = (EditText) findViewById(R.id.pincode);
        mRoomNum = (EditText) findViewById(R.id.room_num);
        mLandmark = (EditText) findViewById(R.id.landmark);
        mWifiUsage = (TextView) findViewById(R.id.wifi_usage_text);
        mWifiUsage.setTypeface(JiotUtils.mTypeface(this, 5));
        mWifiCommercial = (RadioButton) findViewById(R.id.commercial_btn);
        mWifiRes = (RadioButton) findViewById(R.id.residence_btn);
        mWifiOwner = (EditText) findViewById(R.id.owner);
        mWifiUsageRadioGrp = (RadioGroup) findViewById(R.id.wifi_usage_grp);
        mNextButton = (Button) findViewById(R.id.next_btn);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendJson();
            }
        });
    }

    private JSONObject buildSubmitJson() {
        try {
            JSONObject jsonToSend = new JSONObject();
            JSONArray items = new JSONArray();
            JSONArray lteCells = new JSONArray();
            JSONArray wcdmaCells = new JSONArray();
            JSONArray gsmCells = new JSONArray();
            JSONArray wifiAps = new JSONArray();
            JSONObject locationObj = new JSONObject();
            locationObj.put("lat", JiotUtils.sLang);
            locationObj.put("lng", JiotUtils.slon);
            JSONObject itemObject = new JSONObject();
            itemObject.put("position", locationObj);

            for (int i = 0; i < 2; i ++) {
                JSONObject object = new JSONObject();
                object.put("macAddress", mBssid);
                if (null != mSsid) {
                    object.put("ssid", mSsid);
                }
                object.put("rssi", mRssi);
                object.put("frequency", mFreq);
                object.put("serving", true);
                JSONObject geoAddress = new JSONObject();
                if (mStreet.getText()!= null && mStreet.getText().toString() != null
                    && !mStreet.getText().toString().isEmpty()) {
                    geoAddress.put("street", mStreet.getText().toString());
                }
                if (mCity.getText() != null && mCity.getText().toString() != null
                    && !mCity.getText().toString().isEmpty())
                    geoAddress.put("city", mCity.getText().toString());
                if (mPinCode.getText() != null && mPinCode.getText().toString() != null
                    && !mPinCode.getText().toString().isEmpty())
                    geoAddress.put("pincode", Integer.parseInt(mPinCode.getText().toString()));
                if (mState.getText() != null && mState.getText().toString() != null
                    && !mState.getText().toString().isEmpty())
                    geoAddress.put("state", mState.getText().toString());
                if (mHouseNumber.getText() != null && mHouseNumber.getText().toString() != null
                    && !mHouseNumber.getText().toString().isEmpty())
                    geoAddress.put("houseNumber", mHouseNumber.getText().toString());
                if (mFloor.getText() != null && mFloor.getText().toString() != null
                    && !mFloor.getText().toString().isEmpty())
                    geoAddress.put("floorNumber", Integer.parseInt(mFloor.getText().toString()));
                if (mRoomNum.getText() != null && mRoomNum.getText().toString() != null
                    && !mRoomNum.getText().toString().isEmpty()) {
                    geoAddress.put("roomNumber", mRoomNum.getText().toString());
                }
                if (mLandmark.getText() != null && mLandmark.getText().toString() != null
                    && !mLandmark.getText().toString().isEmpty())
                    geoAddress.put("landmark", mLandmark.getText().toString());
                if (geoAddress.length() > 0) {
                    object.put("geo_address", geoAddress);
                }
                int selectedId = mWifiUsageRadioGrp.getCheckedRadioButtonId();
                if (selectedId == R.id.residence_btn) {
                    object.put("typeofusage", "Residential");
                } else if (selectedId == R.id.commercial_btn) {
                    object.put("typeofusage", "Commercial");
                }
                if (mWifiOwner.getText() != null && mWifiOwner.getText().toString() != null
                    && !mWifiOwner.getText().toString().isEmpty())
                    object.put("owner", mWifiOwner.getText().toString());
                wifiAps.put(object);
            }

            itemObject.put("ltecells", lteCells);
            itemObject.put("wcdmacells", wcdmaCells);
            itemObject.put("gsmcells", gsmCells);
            itemObject.put("wifiaps", wifiAps);
            items.put(itemObject);
            jsonToSend.put("items", items);
            return jsonToSend;

        } catch (Exception e) {
            return null;
        }


    }

    private void sendJson() {
        JSONObject submitJson = buildSubmitJson();
        SharedPreferences preferences = getSharedPreferences("Shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("bss", mBssid);
        editor.putString("ss", mSsid);
        editor.putInt("rs", mRssi);
        editor.commit();

        RequestQueue queue = Volley.newRequestQueue(WifiDetails.this);
        String url = "";
        if(isJioVm){
            url = JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL;
            //url = JIOVM_GEOLOCATE_API_URI_TRI + JiotUtils.jiotgetRtlsToken(getContext());
        }else{
            url = JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL + JiotUtils.jiotgetRtlsToken(getApplicationContext());
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, submitJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RTLS", "WiFi Submit response" + response.toString());
                try {
                    Toast.makeText(WifiDetails.this, "WiFi Submit Success", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Log.d("EXCEPTION", "exce");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg=JiotUtils.getVolleyError(error);
//                createDialogue(errorMsg);
                JiotUtils.showErrorToast(WifiDetails.this, errorMsg);
//                Toast.makeText(WifiDetails.this, errorMsg, Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(req);
    }

    private void createDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WifiDetails.this);
        builder.setTitle("Error").setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        alert.show();
    }
}
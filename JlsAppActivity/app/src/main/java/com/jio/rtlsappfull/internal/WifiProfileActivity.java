package com.jio.rtlsappfull.internal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.utils.JiotUtils;

import androidx.appcompat.app.AppCompatActivity;

public class WifiProfileActivity extends AppCompatActivity {

    private Button mNextBtn;
    private EditText mBssid;
    private EditText mSsid;
    private EditText mRssi;
    private Button mGetRssi;
    private TextView mHeaderText;
    private int mFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_profile);
        mBssid = (EditText) findViewById(R.id.wifi_bssid);
        mSsid = (EditText) findViewById(R.id.wifi_ssid);
        mRssi = (EditText) findViewById(R.id.rssi);
        mGetRssi = (Button) findViewById(R.id.get_rssi);
        mGetRssi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mHeaderText = (TextView) findViewById(R.id.wifi_hdr_text);
        mNextBtn = (Button) findViewById(R.id.btn_next);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String bssid = mBssid.getText().toString();
                    String ssid = mSsid.getText().toString();

                    if (null == bssid || bssid.isEmpty()) {
                        Toast.makeText(WifiProfileActivity.this, "Required fields are empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int rssi = Integer.parseInt(mRssi.getText().toString());
                    Intent intent = new Intent(WifiProfileActivity.this, WifiDetails.class);
                    intent.putExtra("wbs", bssid);
                    intent.putExtra("ws", ssid);
                    intent.putExtra("rs", rssi);
                    intent.putExtra("fr", mFrequency);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WifiProfileActivity.this, "Required fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        mHeaderText.setTypeface(JiotUtils.mTypeface(this, 5));
        mGetRssi.setTypeface(JiotUtils.mTypeface(this, 5));
        mNextBtn.setTypeface(JiotUtils.mTypeface(this, 5));

        /*WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo.getNetworkId() != -1) {
                mBssid.setText(wifiInfo.getBSSID());
                mSsid.setText(wifiInfo.getSSID());
                mRssi.setText(String.valueOf(wifiInfo.getRssi()));
                mFrequency = wifiInfo.getFrequency();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(WifiProfileActivity.this).setMessage("Please connect to WiFi and try again")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        })
                        .setTitle("WiFi Alert")
                        .show();
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(WifiProfileActivity.this).setMessage("Please Enable WiFi and try again")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setTitle("WiFi Alert")
                    .show();
        }*/
    }
}

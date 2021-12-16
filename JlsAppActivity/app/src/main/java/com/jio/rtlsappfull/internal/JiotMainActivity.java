package com.jio.rtlsappfull.internal;

import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_DEV;
import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_PRE_PROD;
import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_PROD;
import static com.jio.rtlsappfull.config.Config.SUBMIT_API_URL_SIT;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.model.SubmitAPIData;
import com.jio.rtlsappfull.model.SubmitApiDataResponse;
import com.jio.rtlsappfull.ui.main.SectionsPagerAdapter;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.R)
public class JiotMainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    public static boolean m_first_fetch = true;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private DBManager mDbManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroyMain", "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbManager = new DBManager(this);
        checkLocationPermission();
        Log.d("MAINACTONCREATE", "onCreate Main activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.WiFiProfile:
                startActivity(new Intent(this, WifiProfileActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionAlreadyGrantedInMain() == false) {
                Log.d("ENTERPERM", "checkLocationPermission");
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                setupView();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("onRequest", "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.d("INRESULT", "MY_PERMISSIONS_REQUEST_LOCATION" + permissions.toString());
                // If request is cancelled, the result arrays are empty.
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Log.d("NOPERM", "NOT Granted");
                        Toast.makeText(this, getResources().getString(R.string.perm_error), Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    }
                }
                Log.d("INRESULT", "MY_PERMISSIONS_REQUEST_LOCATION GRANTED");
                setupView();
            }
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }
        }

    }

    public boolean isPermissionAlreadyGrantedInMain() {
        Log.d("ENTERP", "isPermissionAlreadyGrantedInMain");
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if ((coarseLocation != PackageManager.PERMISSION_GRANTED) || (fineLocation != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    PendingIntent m_workPendingIntent = null;
    AlarmManager m_alarmManager = null;

    public void startFetchAlarmMain() {
        Intent intent = new Intent(this, JiotFetchReceiver.class);
        m_workPendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT); //FLAG_CANCEL_CURRENT seems to be required to prevent a bug where the intent doesn't fire after app reinstall in KitKat
        m_alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        m_alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + JiotUtils.FLOAT_REFRESH_100MILI, m_workPendingIntent);
    }

    public void setupView() {
        if (JiotUtils.jiotisLocationEnabled(this) == true && JiotUtils.jiotisNetworkEnabled(this)) {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (i == 0) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_info);
                } else if (i == 1) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_map);
                } else if (i == 2) {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_map);
                } else {
                    tabs.getTabAt(i).setIcon(R.drawable.cell_acc);
                }
            }

            Button m_refresh = (Button) findViewById(R.id.refresh_maps);
            m_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFetchAlarmMain();
                }
            });

            FloatingActionButton m_refresh_float = findViewById(R.id.float_refresh);
            m_refresh_float.setOnClickListener(v -> {
                startFetchAlarmMain();
                JiotUtils.isRefreshed = true;
            });

            FloatingActionButton mSubmitApi = findViewById(R.id.float_submit);
            mSubmitApi.setOnClickListener(v -> {
                makeAPICall();
            });

            stopService();
            bindService(this);
        } else {
            Toast.makeText(this, getResources().getString(R.string.start_msg), Toast.LENGTH_LONG).show();
            finishAffinity();
            return;
        }
    }

    public static void bindService(Context context) {
        Log.d("JLS", "service status=" + isMyServiceRunning(LocationFetchService.class, context));
        if (!isMyServiceRunning(LocationFetchService.class, context)) {
            Log.d("JLS", "Service  started");
            Intent startIntent = new Intent(context, LocationFetchService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, startIntent);
            } else {
                context.startService(startIntent);
            }
        }
    }

    public void stopService() {
        if (isMyServiceRunning(LocationFetchService.class, this)) {
            Intent myService = new Intent(this, LocationFetchService.class);
            stopService(myService);
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void makeAPICall() {
        try {
            SubmitAPIData submitAPIData = new SubmitAPIData();
            TelephonyManager m_telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            List<SubmitAPIData.LteCells> mList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
                return;
            }
            List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
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
                        mList.add(cell);
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
            Log.i("Submit API body", jsonInString);
            String url = "";
            SharedPreferences sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String serverName = sharedPreferences.getString("server_name", null);
            if (serverName.equalsIgnoreCase("Prod")) {
                url = SUBMIT_API_URL_PROD;
            } else if (serverName.equalsIgnoreCase("Sit")) {
                url = SUBMIT_API_URL_SIT;
            } else if (serverName.equalsIgnoreCase("Dev")) {
                url = SUBMIT_API_URL_DEV;
            } else if (serverName.equalsIgnoreCase("Preprod")) {
                url = SUBMIT_API_URL_PRE_PROD;
            }
            Log.i("Submit API Url ", url);
            if (!jsonMainBody.toString().equalsIgnoreCase("{}")) {
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, response -> {
                    try {
                        SubmitApiDataResponse submitApiDataResponse = JiotUtils.getInstance().getPojoObject(String.valueOf(response), SubmitApiDataResponse.class);
//                        SubmitApiDataResponse generateLoginTokenResponse = new Gson().fromJson(String.valueOf(response), SubmitApiDataResponse.class);
                        if (submitApiDataResponse.getDetails() != null && submitApiDataResponse.getDetails().getSuccess() != null && submitApiDataResponse.getDetails().getSuccess().getCode() == 200)
                            Toast.makeText(JiotMainActivity.this, "Submit API called succesfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = JiotUtils.getVolleyError(error);
                        Log.i("Submit API failed", errorMsg);
                        Toast.makeText(JiotMainActivity.this, "Submit API call failed " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        headers.put("token", JiotUtils.jiotgetRtlsToken(JiotMainActivity.this));
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, JioPermissions.class));
    }
}
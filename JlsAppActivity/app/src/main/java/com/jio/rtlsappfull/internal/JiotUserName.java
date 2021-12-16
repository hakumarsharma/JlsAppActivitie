package com.jio.rtlsappfull.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.config.Config;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.jio.rtlsappfull.config.Config.SERVER_DEV_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_GET_TOKEN_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_PREPROD_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_PROD_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.SERVER_SIT_API_KEY_URL;
import static com.jio.rtlsappfull.config.Config.isJioVm;

public class JiotUserName extends AppCompatActivity {

    EditText m_userid_edit_number;
    EditText m_userid_edit_email_number;
    EditText m_userid_edit_imei_number;
    Button m_user_id_submit;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    public String m_api_key = "";
    long mRtlsFetchTimeStartInMs = 0;
    long mRtlsFetchTimeEndInMs = 0;
    String m_userPhone = "";
    String m_userImei = "";
    private String id1;
    private String id2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id);
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(this);
        m_userid_edit_number = (EditText) findViewById(R.id.userid_edit_number);
        m_userid_edit_email_number = (EditText) findViewById(R.id.userid_edit_email_number);
        m_userid_edit_imei_number = (EditText) findViewById(R.id.userid_edit_imei_number);

        m_user_id_submit = (Button) findViewById(R.id.user_id_submit);

//        fillImei();

        m_user_id_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUBMIT", "submit button clicked");
//                String userName = m_userid_edit_number.getText().toString();
                String userName = JiotUtils.IMEI_NUMBER;
                m_userPhone = m_userid_edit_email_number.getText().toString();
                m_userImei = m_userid_edit_imei_number.getText().toString();
                Log.d("User phone", m_userPhone);

                /*if (userName.isEmpty() || userName.length() < 15) {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.userid_length), Toast.LENGTH_SHORT).show();
                }*/
                if (m_userPhone == null || m_userPhone.isEmpty() || m_userPhone.length() > 10) {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.userid_length), Toast.LENGTH_SHORT).show();
                } else {
                    JiotUtils.jiotwriteRtlsDid(JiotUserName.this, userName);
                    SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                    editor.putString("mob", m_userPhone);
                    editor.commit();
                    fetchRtlsKey(userName);
                }
            }
        });
    }

    /*private void fillImei() {
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    try {
                        imei = telephonyManager.getImei();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ActivityCompat.requestPermissions(JiotUserName.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1010);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(JiotUserName.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    imei = telephonyManager.getDeviceId();
                }
            } else {
                ActivityCompat.requestPermissions(JiotUserName.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1010);
            }
        }
        m_userid_edit_number.setText(imei);
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1010) {
            fillImei();
        }
    }*/

    public JSONObject createV2UserIdObject(String name) {
        try {
            JSONObject jsonUserIdObj = new JSONObject();
            JSONObject jsonServiceId = new JSONObject();
            JSONObject jsonIDType = new JSONObject();
            SharedPreferences sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String serverName = sharedPreferences.getString("server_name", null);
            if (serverName.equalsIgnoreCase("Prod")) {
                id1 = Config.PROD_ID1;
                id2 = Config.PROD_ID2;
            } else if (serverName.equalsIgnoreCase("Sit")) {
                id1 = Config.SIT_ID1;
                id2 = Config.SIT_ID2;
            } else if (serverName.equalsIgnoreCase("Dev")) {
                id1 = Config.DEV_ID1;
                id2 = Config.DEV_ID2;
            } else if (serverName.equalsIgnoreCase("Preprod")) {
                id1 = Config.PREPROD_ID1;
                id2 = Config.PREPROD_ID2;
            }
            StringBuffer buffer = new StringBuffer();
            if (id1 != null && id2 != null) {
                for (int index = 0; index < name.length(); index++) {
                    int id1char = id1.charAt(index) - '0';
                    int uniqueidchar = 0;
                    if (name.charAt(index) >= '0' && name.charAt(index) <= '9') {
                        uniqueidchar = name.charAt(index) - '0';
                    } else {
                        if (name.charAt(index) == 'a' || name.charAt(index) == 'A') {
                            uniqueidchar = 0xA;
                        } else if (name.charAt(index) == 'b' || name.charAt(index) == 'B') {
                            uniqueidchar = 0xb;
                        } else if (name.charAt(index) == 'c' || name.charAt(index) == 'C') {
                            uniqueidchar = 0xc;
                        } else if (name.charAt(index) == 'd' || name.charAt(index) == 'D') {
                            uniqueidchar = 0xd;
                        } else if (name.charAt(index) == 'e' || name.charAt(index) == 'E') {
                            uniqueidchar = 0xe;
                        } else if (name.charAt(index) == 'f' || name.charAt(index) == 'F') {
                            uniqueidchar = 0xf;
                        }
                    }
                    buffer.append(Integer.toHexString(id1char ^ uniqueidchar));
                }
                String didVal = buffer.toString();
                jsonIDType.put("value", id2 + didVal);
                jsonIDType.put("type", "100");
                jsonUserIdObj.put("id", jsonIDType);
                jsonServiceId.put("id", "100");
                jsonServiceId.put("name", "rtls");
                jsonUserIdObj.put("service", jsonServiceId);
                Log.d("USERIDJSON", jsonUserIdObj.toString());
                return jsonUserIdObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fetchRtlsKey(String publickey) {
        if (JiotUtils.jiotisLocationEnabled(this) == true && JiotUtils.jiotisNetworkEnabled(this)) {
            Log.d("RTLSKEY", "Request Key From server::" + publickey);
            String url = "";
            try {
                if (isJioVm) {
                    SharedPreferences sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    String serverName = sharedPreferences.getString("server_name", null);
                    if (serverName.equalsIgnoreCase("Prod")) {
                        url = SERVER_PROD_API_KEY_URL;
                    } else if (serverName.equalsIgnoreCase("Sit")) {
                        url = SERVER_SIT_API_KEY_URL;
                    } else if (serverName.equalsIgnoreCase("Dev")) {
                        url = SERVER_DEV_API_KEY_URL;
                    } else if (serverName.equalsIgnoreCase("Preprod")) {
                        url = SERVER_PREPROD_API_KEY_URL;
                    }
                } else {
                    url = SERVER_GET_TOKEN_URL;
                }
                Log.d("GETTOKEN = ", url);
                JSONObject jsonMainBody = createV2UserIdObject(publickey);
                Log.d("TOKENREQ", jsonMainBody.toString());
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RTLSTOKEN", "RTLSTOKEN FETCHED SUCCESSFULLY" + response.toString());
                        try {
                            mRtlsFetchTimeEndInMs = Calendar.getInstance().getTimeInMillis();
                            long apiLatency = (mRtlsFetchTimeEndInMs - mRtlsFetchTimeStartInMs);//Latency in milli seconds
//                            m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSTOKENLATENCY" + " $ " + JiotUtils.getDateTime() + " $ " + apiLatency + " ms ");
                            Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCHED Successfully!!!! ", Toast.LENGTH_SHORT).show();
//                            m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSTOKEN" + " $ " + JiotUtils.getDateTime() + " $ " + response.toString());
                            if (response.has("data")) {
                                JSONObject dataObject = response.getJSONObject("data");
                                if (dataObject.has("apikey")) {
                                    m_api_key = dataObject.get("apikey").toString();
                                    JiotUtils.jiotwriteRtlsToken(getApplicationContext(), m_api_key);
                                    Log.d("RTLSTOKEN", m_api_key);
                                    Intent startMain = new Intent(getApplicationContext(), JiotMainActivity.class);
                                    startActivity(startMain);
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("RTLSTOKEN", "EXCEPTION");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = JiotUtils.getVolleyError(error);
                        Log.e("RTLSTOKEN", "FAILURE " + errorMsg);
//                        createDialogue(errorMsg);
                        JiotUtils.showErrorToast(getApplicationContext(), errorMsg);
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        if (error != null) {
//                            m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSTOKENFAILURE" + " $ " + JiotUtils.getDateTime() + " $ " + errorMsg);
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                mRtlsFetchTimeStartInMs = Calendar.getInstance().getTimeInMillis();
                queue.add(req);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "RTLS token request Failed!!!! ", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please Enable Network And Location For App to proceed!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void createDialogue(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

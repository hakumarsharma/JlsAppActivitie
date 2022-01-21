package com.jio.rtlsappfull.internal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.config.Config;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.utils.JiotUtils;

import org.json.JSONObject;

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
    Button m_user_id_submit;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id);
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(this);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.mobile_number);
        m_userid_edit_number = (EditText) findViewById(R.id.userid_edit_email_number);
        m_user_id_submit = (Button) findViewById(R.id.user_id_submit);
        m_user_id_submit.setOnClickListener(v -> {
            if(JiotUtils.jiotisNetworkEnabled(this)) {
                Log.d("SUBMIT", "submit button clicked");
                String userNumber = m_userid_edit_number.getText().toString();
                Log.d("User phone", userNumber);
                if (userNumber == null || userNumber.isEmpty() || userNumber.length() != 10) {
                    Toast.makeText(this, getResources().getString(R.string.mobile_number_length), Toast.LENGTH_SHORT).show();
                } else {
                    JiotUtils.jiotwriteRtlsDid(JiotUserName.this, userNumber);
                    JiotUtils.saveMobileNumber(JiotUserName.this, userNumber);
                    fetchRtlsKey(JiotUtils.IMEI_NUMBER);
                }
            }  else {
                Toast.makeText(this, "Please enable your internet connection, before proceeding", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public JSONObject createV2UserIdObject(String name) {
        try {
            JSONObject jsonUserIdObj = new JSONObject();
            JSONObject jsonServiceId = new JSONObject();
            JSONObject jsonIDType = new JSONObject();
            String id1 = Config.PREPROD_ID1;
            String id2 = Config.PREPROD_ID2;
            StringBuffer buffer = new StringBuffer();

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

    public void fetchRtlsKey(String publickey) {
            JSONObject jsonMainBody = createV2UserIdObject(publickey);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SERVER_PREPROD_API_KEY_URL, jsonMainBody, response -> {
                try {
                    Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCHED Successfully!!!! ", Toast.LENGTH_SHORT).show();
                    if (response.has("data")) {
                        JSONObject dataObject = response.getJSONObject("data");
                        if (dataObject.has("apikey")) {
                            String m_api_key = dataObject.get("apikey").toString();
                            JiotUtils.jiotwriteRtlsToken(getApplicationContext(), m_api_key);
                            Log.d("RTLSTOKEN", m_api_key);
                            JiotUtils.isTokenExpired = false;
                            SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
                            editor.putString("fetch_rtls_key", "success");
                            editor.commit();
                            Intent i = new Intent(JiotUserName.this, JiotMainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    Log.d("RTLSTOKEN", "EXCEPTION");
                    e.printStackTrace();
                }
            }, error -> {
                String errorMsg = JiotUtils.getVolleyError(error);
                Log.e("RTLSTOKEN", "FAILURE " + errorMsg);
                JiotUtils.isTokenExpired = true;
                Toast.makeText(getApplicationContext(), "RTLS TOKEN FETCH Failed!!!! ", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(req);
        }
}

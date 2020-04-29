package com.example.nutapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpWaitScreen extends AppCompatActivity {
    EditText login_edit_number;
    Button login_btn_otp;
    Handler m_handler;
    String m_url = "https://stg.borqs.io/accounts/api/users/tokens/verify";
    String m_publickey = "";
    String m_token = "";
    String m_self_generated_otp = "";

    int OTP_MIN = 10000;
    int OTP_MAX = 99999;
    int m_self_otp = 10000;
    String sms_otp = "Dear Customer,your JioTags OTP is : " + m_self_otp + " .Use this password to validate your login.";


    AlertDialog.Builder builder;
    AlertDialog m_disconnectAlert;
    public void showOtpAlreadyGenerated() {
        //builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog ));
        builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_Holo_Dialog_Alert));
        builder.setMessage("SMS OTP ALREADY GENERATED").setTitle("Alert")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.ic_action_settings_remote);

        m_disconnectAlert = builder.create();
        //m_disconnectAlert.setTitle(bleDevice.name + " Disconnected");
        m_disconnectAlert.setTitle("Alert");
        //m_disconnectAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.asset_dialog_round_bkgd));
        m_disconnectAlert.show();
        Button bNegative = m_disconnectAlert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bNegative.setBackgroundColor(0xFFCCF2FD);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroyOTP", "onDestroy");
        try {
            if (m_smsConnectionReceiver != null) {
                Log.d("SMSRCRV", "UNREGISTER SMS RCVR");
                unregisterReceiver(m_smsConnectionReceiver);
            }
        }catch(Exception e){
            Log.d("SMSRCRV","SMS RCVR NOT REGISTRED");
        }
    }

    public void validateSelfOtp() {
        if (m_self_generated_otp.equalsIgnoreCase(m_token)) {
            Toast.makeText(getApplicationContext(), "OTP Validated Successfully!!!! ", Toast.LENGTH_SHORT).show();
            Intent startMain = new Intent(getApplicationContext(), JioAddFinder.class);
            startActivity(startMain);
        }else{
            Toast.makeText(getApplicationContext(), "Invalid OTP !!!! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void validateOtp(final String url, String publickey) {
        Log.d("SERVER OTP", "Request OTP From server::" + publickey);

        try {
            JSONObject jsonMainBody = new JSONObject();
            jsonMainBody.put("token", m_token);
            jsonMainBody.put("phone", m_publickey);
            jsonMainBody.put("type", "registration");

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("MSGFROMSERVER", "SUCCESS" + response.toString());
                    try {
                        Log.d("MSGFROMSERVER", "SUCCESS" + response.get("code"));
                        if (response.get("code").toString().equalsIgnoreCase("200")) {
                            Toast.makeText(getApplicationContext(), "OTP Validated Successfully!!!! ", Toast.LENGTH_SHORT).show();
                            Intent startMain = new Intent(getApplicationContext(), JioAddFinder.class);
                            startActivity(startMain);
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid OTP!!!! ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "exce");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MSGFROMSERVER", "FAILURE");
                    Toast.makeText(getApplicationContext(), "Invalid OTP!!!! ", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {

        }
    }


    public BroadcastReceiver m_smsConnectionReceiver = new BroadcastReceiver() {
        String nut_device_address = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NUTRECEIVER", intent.getAction().toString());
            //Toast.makeText(context, "NUTRECEIVER AT POS: " + intent.getAction().toString(), Toast.LENGTH_SHORT).show();
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    String sender = smsMessage.getServiceCenterAddress();
                    //Toast.makeText(context, "SENDER SMS: " + sender, Toast.LENGTH_SHORT).show();
                    Log.d("SENDERSS", sender);
                    Log.d("MSGBODY", messageBody);
                    if (messageBody.contains("Dear Customer,your JioTags OTP is")) {
                        //if(sender.equalsIgnoreCase("+919845087001")) {
                        //if(sender.equalsIgnoreCase("+917021075049")) {
                        //Toast.makeText(context, "SMS RECEIVED: " + messageBody, Toast.LENGTH_SHORT).show();
                        String otpNumber = messageBody.split(":")[1].split(" ")[1].trim();
                        Log.d("OTPSMS",otpNumber);
                        //Toast.makeText(context, "OTP RECEIVED: " + otp_number, Toast.LENGTH_SHORT).show();
                        login_edit_number.setText(otpNumber);
                        login_btn_otp.setEnabled(true);
                        login_edit_number.setEnabled(false);
                        login_btn_otp.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                        m_token = otpNumber;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_wait);
        Intent receiveIntent = this.getIntent();
        m_publickey = receiveIntent.getStringExtra("PHNUM");
        m_self_generated_otp = receiveIntent.getStringExtra("SELFOTP");

        login_btn_otp = (Button) findViewById(R.id.login_btn_otp);
        login_btn_otp.setEnabled(true);
        login_btn_otp.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
        
        /*login_btn_otp.setEnabled(false);
        login_btn_otp.setBackground(getResources().getDrawable(R.drawable.disabled_button));*/
        Log.d("BUTTON::", "DISABLED" + "");
        login_btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent startMain = new Intent(getApplicationContext(), JioAddFinder.class);
                startActivity(startMain);*/
                if (login_edit_number.getText().toString().isEmpty()) {
                    Toast.makeText(v.getContext(), "OTP field is empty!!!! ", Toast.LENGTH_SHORT).show();
                } else {
                    //validateOtp(m_url, m_publickey);
                    validateSelfOtp();
                }
            }
        });

        IntentFilter intFilterSms = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(m_smsConnectionReceiver, intFilterSms);

        login_edit_number = (EditText) findViewById(R.id.login_edit_number);
        m_handler = new Handler();
       /* Runnable startNext = new Runnable() {
            public void run() {
                login_edit_number.setText("No SMS!!! proceed");
                login_btn_otp.setEnabled(true);
                login_btn_otp.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
            }
        };*/
        //m_handler.postDelayed(startNext, 60000);
        TextView loginJioTagsEnterNumberOtp = (TextView) findViewById(R.id.login_jioTags_enter_number_otp);
        loginJioTagsEnterNumberOtp.setText("to +91 " + m_publickey);

        TextView loginOtpTextid =  findViewById(R.id.login_otp_textid);
        loginOtpTextid.setTypeface(JioUtils.mTypeface(this, 3));

        TextView loginResendOtp =  findViewById(R.id.login_resend_otp);

        loginResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(true){
                if (login_edit_number.getText().toString().trim().isEmpty()) {
                    Log.d("RESEND","OTP RESENT");
                    Toast.makeText(getApplicationContext(),"SMS OTP RESENT",Toast.LENGTH_SHORT).show();
                    sendSelfOtpWait();
                }else{
                    //Toast.makeText(getApplicationContext(),"SMS OTP ALREADY GENERATED",Toast.LENGTH_SHORT).show();
                    showOtpAlreadyGenerated();
                }
            }
        });

        TextView loginJioTagsRegisteredOtp=(TextView)findViewById(R.id.login_jioTags_registered_otp);
        loginJioTagsRegisteredOtp.setTypeface(JioUtils.mTypeface(this, 2));

        loginResendOtp.setTypeface(JioUtils.mTypeface(this,2));
        loginJioTagsEnterNumberOtp.setTypeface(JioUtils.mTypeface(this,3));
    }

    public int generateSelfOtpWait() {
        Random rand = new Random();
        m_self_otp = rand.nextInt((OTP_MAX - OTP_MIN) + 1) + OTP_MIN;
        Log.d("SELFOTP", m_self_otp + "");
        return m_self_otp;
    }

    public void sendSelfOtpWait() {
        generateSelfOtpWait();
        sms_otp = "Dear Customer,your JioTags OTP is : " + m_self_otp + " .Use this password to validate your login.";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(m_publickey, null, sms_otp, null, null);
        m_self_generated_otp=Integer.toString(m_self_otp);
    }

}

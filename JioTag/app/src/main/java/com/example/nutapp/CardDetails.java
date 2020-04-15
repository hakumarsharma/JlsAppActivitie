package com.example.nutapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class CardDetails extends AppCompatActivity {
    int position_nut_val = 0;
    String m_assetName = "";
    boolean isAlarmOn = false;
    boolean isConnected = false;
    String ADDRRESS = "";
    String LOCADDRRESS = "";
    String ICON = "others";
    String STATUS = "";
    public static TextView rel_detail_device_status;
    public static Button alarm;
    public ProgressDialog m_connectDisconnectDialogp;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Context m_context_main;
    Switch rel_detail_phone_alert_switch;
    Switch rel_detail_device_alert_switch;
    //public static final int MAIN_BLUE        = 0x3DB5EA;
    Handler m_handler;

    AlertDialog.Builder builder;
    AlertDialog m_disconnectAlert;

    boolean m_isPleaseWaitOn = false;
    //String to show next to connected/disconnected.
    String farnear_val = "Far";
    boolean m_firstTimePhoneSwitch = true;
    boolean m_firstTimeDeviceSwitch = true;
    boolean is_auto_reconnect=true;


    ///////////////////////////////////////////////////////////
    MediaPlayer m_player;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.d("DESTROYCARD","onDestroyCard");
            unregisterReceiver(m_nutConnectionReceiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void playSound() {
        try {
            m_player = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_NOTIFICATION_URI);
            m_player.start();
        } catch (Exception e) {
            Log.d("MEDIAEX", e.getMessage());
        }
    }

    public void stopSound() {
        m_player.stop();
        Log.d("STOPSOUND", "STOpSOUND CALLED");
    }

    @Override
    public void onBackPressed() {
        unregisterReceiver(m_nutConnectionReceiver);
        //super.onBackPressed();
        finish();
    }

    public boolean getDeviceDisconnectionReminder(String DEVICE_HEX_DEVICE_ADDRESS) {
        boolean connectionReminder = Boolean.valueOf(preferences.getString(DEVICE_HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REMINDER", true + ""));
        return connectionReminder;
    }

    public static boolean m_alertDialogActive = false;

    public void createDisconnectAlertDialog(String nut_dev_address) {
        boolean disconnectAlertVal = JioUtils.getDisconnectionAlertSetting(this);
        if (disconnectAlertVal) {
            if (m_alertDialogActive == true) {
                m_alertDialogActive = false;
                m_disconnectAlert.dismiss();
            }
            m_alertDialogActive = true;
            //builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog ));
            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_Holo_Dialog_Alert));
            builder.setMessage("Your attached device was disconnected from your phone").setTitle(m_assetName)
                    .setCancelable(false)
                    .setPositiveButton("View Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            m_alertDialogActive = false;
                            Intent inte = new Intent();
                            inte.setAction("com.nutapp.notifications_showlocation");
                            sendBroadcast(inte);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_alertDialogActive = false;
                            dialog.dismiss();
                        }
                    }).setIcon(R.drawable.ic_action_settings_remote);

            m_disconnectAlert = builder.create();
            //m_disconnectAlert.setTitle(bleDevice.name + " Disconnected");
            m_disconnectAlert.setTitle(m_assetName);
            //m_disconnectAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.asset_dialog_round_bkgd));
            m_disconnectAlert.show();
            Button b_negative = m_disconnectAlert.getButton(DialogInterface.BUTTON_NEGATIVE);
            Button b_positive = m_disconnectAlert.getButton(DialogInterface.BUTTON_POSITIVE);
            b_negative.setBackgroundColor(0xFFCCF2FD);
            b_positive.setBackgroundColor(0xFF00BFF2);
            b_positive.setTextColor(Color.WHITE);
        }
    }

    public void showAlertOnReconnection(String device_address) {
        Log.d("JIO", "RECONNECT ALERT");
        String receivedData = m_assetName + "ReConnect success Alert";
        Context context = m_context_main;
        Intent intent = new Intent(context, CardDetails.class);
        // Send data to NotificationView Class
        intent.putExtra("title", m_assetName + " ReConnected,successfully");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final int notificationId = 3;
        String channelId = "channel-03";
        String channelName = "Connection";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(m_assetName + "  ReConnected successfully")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        nm.notify(notificationId, m_notif);
    }

    public void showConnectReminderNotification(String device_address) {
        Log.d("JIO", "CONNECTION REMINDER");
        String receivedData = m_assetName + " Reconnect Reminder";
        Context context = m_context_main;
        Intent intent = new Intent(context, CardDetails.class);
        // Send data to NotificationView Class
        intent.putExtra("title", m_assetName + " disconnected,Please reconnect");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final int notificationId = 2;
        String channelId = "channel-02";
        String channelName = "Reconnect Reminder";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(m_assetName + " Reconnect Reminder")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(m_assetName + " disconnected,Please reconnect");
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        nm.notify(notificationId, m_notif);
    }

    public void createSoundLessNotification() {
        Log.d("JIO", "NOTIFICATION PRINTED");
        String receivedData = m_assetName+ " Disconnected";
        Context context = m_context_main;
        Intent intent = new Intent(context, CardDetails.class);
        // Send data to NotificationView Class
        intent.putExtra("title", m_assetName+" Disconnected");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;


        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(m_assetName+ " Disconnected from Phone")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        nm.notify(notificationId, m_notif);
    }

    public void showJioDisconnectNotification(String device_address) {
        Log.d("JIO", "NOTIFICATION PRINTED");
        String receivedData = m_assetName+ " Disconnected";
        Context context = m_context_main;
        Intent intent = new Intent(context, CardDetails.class);
        // Send data to NotificationView Class
        intent.putExtra("title", m_assetName+" Disconnected");
        intent.putExtra("text", receivedData);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(m_assetName+" Disconnected")
                .setSmallIcon(R.drawable.ic_action_settings_remote)
                .setAutoCancel(false)
                .setColor(0x00b359)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(m_assetName + " Disconnected from Phone");
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            nm.createNotificationChannel(mChannel);
        }
        // NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Notification m_notif = nbuilder.build();
        m_notif.flags = Notification.FLAG_INSISTENT;
        nm.notify(notificationId, m_notif);


        final boolean connectionReminder = getDeviceDisconnectionReminder(device_address);
        final String addr_device = device_address;
        final Runnable sendConnectReminder = new Runnable() {
            public void run() {
                if (connectionReminder == true) {
                    showConnectReminderNotification(addr_device);
                }
            }
        };

        int duration_ms = JioUtils.getAlertDuration(m_context_main, device_address, true);
        Log.d("DURATIONN", "Notificationnn duration is" + duration_ms);
        final Runnable stopSoundR = new Runnable() {
            public void run() {
                nm.cancel(notificationId);
                createSoundLessNotification();
                m_handler.postDelayed(sendConnectReminder, 500);
            }
        };

        boolean phone_alert_repeat = JioUtils.getPhoneAlertRepeat(m_context_main, device_address);
        if (phone_alert_repeat == false) {
            m_handler.postDelayed(stopSoundR, duration_ms * 1000);
        }

        /*Log.d("DURATION", "PHONEALERT DURATION::" + duration_ms);
        playSound();
        final Runnable stopSoundR = new Runnable() {
            public void run() {
                stopSound();
            }
        };

        boolean repeat_phone_alert = Boolean.valueOf(preferences.getString(device_address + "PHONE_ALERT_REPEAT", true + ""));
        if (repeat_phone_alert == false) {
            m_handler.postDelayed(stopSoundR, duration_ms * 1000);
        } else {
            m_handler.postDelayed(stopSoundR, 20 * 1000);
        }*/
    }
    //////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, "PHONE AND DEVICE ALERT RESULT " + requestCode + "::"+resultCode, Toast.LENGTH_SHORT).show();
        if (resultCode == JioUtils.HOME_KEY) {
            unregisterReceiver(m_nutConnectionReceiver);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ONRESUME","RESUME CARDDETAILS");
        boolean oldPhoneVal = Boolean.valueOf(preferences.getString(ADDRRESS + "PHONEALERT", true + ""));
        rel_detail_phone_alert_switch.setChecked(oldPhoneVal);
        boolean oldDeviceVal = Boolean.valueOf(preferences.getString(ADDRRESS + "DEVICEALERT", true + ""));
        rel_detail_device_alert_switch.setChecked(oldDeviceVal);


    }

    public void turnOnOffDeviceAlert(boolean val) {
        // boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_DEVICE_ALERTS, false);
        boolean oldVal = Boolean.valueOf(preferences.getString(ADDRRESS + "DEVICEALERT", true + ""));
        Log.d("DEVICE_ALERT", "DEVICE_ALERT" + oldVal);
        prefEditor.putString(ADDRRESS + "DEVICEALERT", val + "");
        Log.d("DEVICE_ALERT", "DEVICE_ALERT ALERT VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffPhoneAlert(boolean val) {
        //boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_PHONE_ALERTS, false);
        boolean oldVal = Boolean.valueOf(preferences.getString(ADDRRESS + "PHONEALERT", true + ""));
        Log.d("PHONE_ALERT", "PHONE_ALERT" + oldVal);
        prefEditor.putString(ADDRRESS + "PHONEALERT", val + "");
        Log.d("PHONE_ALERT", "PHONE_ALERT VAL" + val);
        prefEditor.commit();
    }

    public void stopPleaseWaitDialogAFter1Minute(final boolean isForcedExit) {
        final Runnable stopPleaseWait = new Runnable() {
            public void run() {
                stopConnectProgressDialog(isForcedExit);
            }
        };
        m_handler.postDelayed(stopPleaseWait, 60 * 1000);
    }


    public void startConnectProgressDialog() {
        if (m_isPleaseWaitOn == false) {
            m_connectDisconnectDialogp.setMessage("Please wait...");
            m_connectDisconnectDialogp.setCancelable(false);
            m_connectDisconnectDialogp.show();
            m_isPleaseWaitOn = true;
        }
    }

    public void stopConnectProgressDialog(boolean isForcedExit) {
        if (m_isPleaseWaitOn == true) {
            m_connectDisconnectDialogp.dismiss();
            m_isPleaseWaitOn = false;
            if (isForcedExit) {
                Toast.makeText(this.getApplicationContext(), "Connect/Disconnect operation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public BroadcastReceiver m_nutConnectionReceiver = new BroadcastReceiver() {
        String nut_device_address = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NUTRECEIVER", intent.getAction().toString());
            // Toast.makeText(getContext(), "NUTRECEIVER AT POS: " + intent.getAction().toString(), Toast.LENGTH_SHORT).show();
            if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_connected_device")) {
                nut_device_address = intent.getStringExtra("DEV_ADDRESS");
                Log.d("NUTRECEIVER", "CONNECTEDMSG::" + nut_device_address);
                if (ADDRRESS.equalsIgnoreCase(nut_device_address)) {
                    rel_detail_device_status.setText("Connected" + " | " + farnear_val);
                    rel_detail_device_status.setTextColor(getResources().getColor(R.color.cardview_device_status_color));
                    alarm.setEnabled(true);
                    alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_frame_blue));
                    stopConnectProgressDialog(false);
                    isConnected = true;
                    rel_detail_device_alert_switch.setClickable(false);
                    rel_detail_phone_alert_switch.setClickable(false);
                    if (JioUtils.getDeviceAlertReconnectionValue(m_context_main, nut_device_address) && (is_auto_reconnect == true)) {
                        is_auto_reconnect=false;
                        showAlertOnReconnection(nut_device_address);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_disconnected_device")) {
                nut_device_address = intent.getStringExtra("DEV_ADDRESS");
                Log.d("NUTRECEIVER", "DISCONNECTEDMSG::" + nut_device_address);
                if (ADDRRESS.equalsIgnoreCase(nut_device_address) && (isConnected == true)) {
                    is_auto_reconnect=true;
                    rel_detail_device_status.setText("Disconnected" + " | " + farnear_val);
                    rel_detail_device_status.setTextColor(0xCCCCCCCC);
                    alarm.setEnabled(false);
                    alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.disabled_button));
                    stopConnectProgressDialog(false);
                    createDisconnectAlertDialog(nut_device_address);
                    isConnected = false;
                    rel_detail_device_alert_switch.setClickable(true);
                    rel_detail_phone_alert_switch.setClickable(true);
                    boolean phoneAlertSetting = JioUtils.getPhoneAlertSetting(getApplicationContext(), nut_device_address);
                    if (phoneAlertSetting) {
                        showJioDisconnectNotification(nut_device_address);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_alarm_disabled")) {
                nut_device_address = intent.getStringExtra("DEV_ADDRESS");
                Log.d("NUTRECEIVER", "DISCONNECTEDMSG::" + nut_device_address);
                if (ADDRRESS.equalsIgnoreCase(nut_device_address) && (isAlarmOn == true)) {
                    Runnable startScanr = new Runnable() {
                        public void run() {
                            isAlarmOn = false;
                            alarm.setText("Find");
                            alarm.setEnabled(true);
                            alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_frame_blue));
                        }
                    };
                    m_handler.postDelayed(startScanr, 2000);
                }
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_alarm_enabled")) {
                nut_device_address = intent.getStringExtra("DEV_ADDRESS");
                Log.d("NUTRECEIVER", "DISCONNECTEDMSG::" + nut_device_address);
                if (ADDRRESS.equalsIgnoreCase(nut_device_address) && (isAlarmOn == false)) {
                    Runnable startScanr = new Runnable() {
                        public void run() {
                            isAlarmOn = true;
                            alarm.setText("Stop");
                            alarm.setEnabled(true);
                            alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_frame_blue));
                        }
                    };
                    int device_alert_duration_ms = JioUtils.getAlertDuration(m_context_main, nut_device_address, false);
                    Log.d("DEVICEALERTDUR", device_alert_duration_ms + "");
                    m_handler.postDelayed(startScanr, device_alert_duration_ms * 1000);
                }
            } else if (intent.getAction().equalsIgnoreCase("com.nutapp.notifications_maps_home_key")) {
                Log.d("MAPSHOME", "HOME FROM MAPS");
                unregisterReceiver(m_nutConnectionReceiver);
                finish();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ONCREATE","CREATEEEEEE");
        setContentView(R.layout.card_details_view);
        m_firstTimeDeviceSwitch = true;
        m_firstTimePhoneSwitch = true;

        m_connectDisconnectDialogp = new ProgressDialog(this);
        m_handler = new Handler();
        Intent receiveIntent = this.getIntent();
        position_nut_val = receiveIntent.getIntExtra("POSITION_NUT", 0);
        ADDRRESS = receiveIntent.getStringExtra("ADDRRESS");
        LOCADDRRESS = receiveIntent.getStringExtra("LOCATION");
        STATUS = receiveIntent.getStringExtra("STATUS");
        Log.d("STATUSS", STATUS);
        String status_val = "";


        if (STATUS.toLowerCase().contains("|")) {
            status_val = STATUS.split("\\|")[0];
            farnear_val = STATUS.split("\\|")[1];
        } else {
            status_val = STATUS;
        }
        //Log.d("STATUSVAL",status_val+":::Hello");
        //Toast.makeText(this.getApplicationContext(), "RENTRY"+STATUS, Toast.LENGTH_LONG).show();

        if (status_val.trim().equalsIgnoreCase("connected")) {
            isConnected = true;
            //Log.d("RENTRY", "reentry connected");
            //Toast.makeText(this.getApplicationContext(), "RENTRY CONNECTED "+status_val, Toast.LENGTH_LONG).show();
        } else {
            isConnected = false;
            //Log.d("RENTRY", "reentry disconnected");
            //Toast.makeText(this.getApplicationContext(), "RENTRY DISCONNECTED "+status_val, Toast.LENGTH_LONG).show();
        }
        ICON = receiveIntent.getStringExtra("ICON").split("\\.")[0];
        Log.d("ICONINCARD", ICON);

        IntentFilter intFilterConnected = new IntentFilter("com.nutapp.notifications_connected_device");
        intFilterConnected.addAction("com.nutapp.notifications_disconnected_device");
        intFilterConnected.addAction("com.nutapp.notifications_alarm_enabled");
        intFilterConnected.addAction("com.nutapp.notifications_alarm_disabled");
        intFilterConnected.addAction("com.nutapp.notifications_maps_home_key");
        registerReceiver(m_nutConnectionReceiver, intFilterConnected);

        ImageView rel_detail_lefticon = (ImageView) findViewById(R.id.rel_detail_lefticon);
        switch (ICON.toLowerCase()) {
            case "key":
                rel_detail_lefticon.setImageResource(R.drawable.key);
                break;
            case "wallet":
                rel_detail_lefticon.setImageResource(R.drawable.wallet);
                break;
            case "laptop":
                rel_detail_lefticon.setImageResource(R.drawable.laptop_home);
                break;
            case "suitcase":
                rel_detail_lefticon.setImageResource(R.drawable.suitcase);
                break;
            case "purse":
                rel_detail_lefticon.setImageResource(R.drawable.purse);
                break;
            default:
                rel_detail_lefticon.setImageResource(R.drawable.others);
                break;
        }

        rel_detail_device_status = (TextView) findViewById(R.id.rel_detail_device_status);
        rel_detail_device_status.setText(STATUS);
        if (isConnected == false) {
            rel_detail_device_status.setTextColor(0xCCCCCCCC);
        } else {
            rel_detail_device_status.setTextColor(getResources().getColor(R.color.cardview_device_status_color));
        }

        TextView Location_text = (TextView) findViewById(R.id.rel_detail_device_distance);
        Location_text.setText(LOCADDRRESS);

        preferences = this.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        TextView deviceAddr = (TextView) findViewById(R.id.rel_detail_device_details);
        //deviceAddr.setText(ADDRRESS.toString());
        //deviceAddr.setText("JioTag." + position_nut_val);
        //Added for custom Name Support
        m_assetName = receiveIntent.getStringExtra("ICON");
        deviceAddr.setText(receiveIntent.getStringExtra("ICON"));
        alarm = (Button) findViewById(R.id.rel_detail_btn_search_devices);

        if (isConnected == false && status_val.trim().toLowerCase().equalsIgnoreCase("disconnected")) {
            alarm.setEnabled(false);
            alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.disabled_button));
        }
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ALARM ", "ALARM DEVICE:" + position_nut_val);
                //Toast.makeText(v.getContext(), "ALARM AT POS: " + position_nut_val, Toast.LENGTH_SHORT).show();
                boolean deviceAlertValue = JioUtils.getDeviceAlertSetting(m_context_main, ADDRRESS);
                Intent inte = new Intent();
                if (isAlarmOn == false) {
                    //Toast.makeText(v.getContext(), "ALARM AT POS: " + position_nut_val, Toast.LENGTH_SHORT).show();
                    if (deviceAlertValue) {
                        inte.setAction("com.nutapp.notifications_alarm_start");
                        inte.putExtra("DEVICE_ADDRESS", ADDRRESS);
                        //isAlarmOn = true;
                        //alarm.setText("Stop");
                        alarm.setEnabled(false);
                        alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.disabled_button));
                    } else {
                        Toast.makeText(m_context_main, "DEVICE ALERT SETTING DISABLED: PLEASE ENABLE IT. ", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    inte.setAction("com.nutapp.notifications_alarm_stop");
                    inte.putExtra("DEVICE_ADDRESS", ADDRRESS);
                    //isAlarmOn = false;
                    //alarm.setText("Find");
                    alarm.setEnabled(false);
                    alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.disabled_button));
                }
                inte.putExtra("POSITION_NUT", position_nut_val);
                sendBroadcast(inte);
            }
        });

        final RelativeLayout rel_detail_header_item_phone_alert = (RelativeLayout) findViewById(R.id.rel_detail_header_item_phone_alert);
        rel_detail_header_item_phone_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected == false) {
                    Intent hello = new Intent(v.getContext(), PhoneAlertSetting.class);
                    hello.putExtra("HEXADDR", ADDRRESS);
                    startActivityForResult(hello, JioUtils.LAUNCH_PHONE_ALERT_SETTING);
                    rel_detail_header_item_phone_alert.setPressed(true);
                } else {
                    Toast.makeText(v.getContext(), "Device is connected, Please disconnect and Change the setting!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final RelativeLayout rel_detail_header_item_device_alert = (RelativeLayout) findViewById(R.id.rel_detail_header_item_device_alert);
        rel_detail_header_item_device_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel_detail_header_item_device_alert.setPressed(true);
                if (isConnected == false) {
                    Intent hello = new Intent(v.getContext(), DeviceAlertSetting.class);
                    hello.putExtra("HEXADDR", ADDRRESS);
                    startActivityForResult(hello, JioUtils.LAUNCH_DEVICE_ALERT_SETTING);
                } else {
                    Toast.makeText(v.getContext(), "Device is connected, Please disconnect and Change the setting!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final RelativeLayout rel_detail_header_item_pair = (RelativeLayout) findViewById(R.id.rel_detail_header_item_pair);
        rel_detail_header_item_pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CONNECT", "CONNECT DEVICE:" + position_nut_val);
                rel_detail_header_item_pair.setPressed(true);
                if (isConnected == false) {
                    is_auto_reconnect=false;
                    Intent inte = new Intent();
                    inte.setAction("com.nutapp.notifications_connect");
                    inte.putExtra("POSITION_NUT", position_nut_val);
                    inte.putExtra("DEV_ADDRESS", ADDRRESS);
                    sendBroadcast(inte);
                    startConnectProgressDialog();
                    stopPleaseWaitDialogAFter1Minute(true);
                } else {
                    Toast.makeText(v.getContext(), "Device Already Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final RelativeLayout rel_detail_header_item_unpair = (RelativeLayout) findViewById(R.id.rel_detail_header_item_unpair);
        rel_detail_header_item_unpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DISCONNECT", "DISCONNECT DEVICE:" + position_nut_val);
                rel_detail_header_item_unpair.setPressed(true);
                if (isConnected == true) {
                    if (isAlarmOn == true) {
                        Toast.makeText(v.getContext(), "Please Stop the alarm before disconnecting...", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent inte = new Intent();
                        inte.setAction("com.nutapp.notifications_disconnect");
                        inte.putExtra("POSITION_NUT", position_nut_val);
                        sendBroadcast(inte);
                        startConnectProgressDialog();
                        stopPleaseWaitDialogAFter1Minute(true);
                    }
                } else {
                    Toast.makeText(v.getContext(), "Device Already Disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView rel_detail_unpair_text = (TextView) findViewById(R.id.rel_detail_unpair_text);


        TextView rel_detail_tView = (TextView) findViewById(R.id.rel_detail_tView);
        rel_detail_tView.setText(getTime());


        rel_detail_phone_alert_switch = (Switch) findViewById(R.id.rel_detail_phone_alert_switch);
        rel_detail_phone_alert_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Toast.makeText(buttonView.getContext(), "PHONE CHANGE!!!!!!!!", Toast.LENGTH_SHORT).show();
                        turnOnOffPhoneAlert(isChecked);
                }
        });

        rel_detail_device_alert_switch = (Switch) findViewById(R.id.rel_detail_device_alert_switch);
        rel_detail_device_alert_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Toast.makeText(buttonView.getContext(), "DEVICE CHANGE!!!!!!!!", Toast.LENGTH_SHORT).show();
                        turnOnOffDeviceAlert(isChecked);
            }
        });
        //boolean oldDeviceVal = preferences.getBoolean(JioUtils.MYPREFERENCES_DEVICE_ALERTS, false);

        boolean oldDeviceVal = Boolean.valueOf(preferences.getString(ADDRRESS + "DEVICEALERT", true + ""));
        rel_detail_device_alert_switch.setChecked(oldDeviceVal);

            //boolean oldPhoneVal = preferences.getBoolean(JioUtils.MYPREFERENCES_PHONE_ALERTS,false);
        boolean oldPhoneVal = Boolean.valueOf(preferences.getString(ADDRRESS + "PHONEALERT", true + ""));
        rel_detail_phone_alert_switch.setChecked(oldPhoneVal);
        if(isConnected == false){
            rel_detail_device_alert_switch.setClickable(true);
            rel_detail_phone_alert_switch.setClickable(true);
        }else{
            rel_detail_device_alert_switch.setClickable(false);
            rel_detail_phone_alert_switch.setClickable(false);
        }


        ImageButton attach_back = (ImageButton) findViewById(R.id.card_attach_back);
        attach_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                unregisterReceiver(m_nutConnectionReceiver);
                finish();
            }
        });
        ImageButton device_home = (ImageButton) findViewById(R.id.card_attach_tick);
        device_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                unregisterReceiver(m_nutConnectionReceiver);
                finish();
            }
        });

        final RelativeLayout rel_detail_header_item_jio_comm_find = (RelativeLayout) findViewById(R.id.rel_detail_header_item_jio_comm_find);
        rel_detail_header_item_jio_comm_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JIOCOMMFIND", "rel_detail_header_item_jio_comm_find clicked:");
                rel_detail_header_item_jio_comm_find.setPressed(true);
                Toast.makeText(v.getContext(), "Jio Community Finder Coming Soon!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        final RelativeLayout rel_detail_header_item_software_update = (RelativeLayout) findViewById(R.id.rel_detail_header_item_software_update);
        rel_detail_header_item_software_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUPDATE", "rel_detail_header_item_software_update clicked:");
                rel_detail_header_item_software_update.setPressed(true);
                Toast.makeText(v.getContext(), "Software Update Feature Coming Soon!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_location_card:
                        Intent inte = new Intent();
                        inte.setAction("com.nutapp.notifications_showlocation");
                        sendBroadcast(inte);
                        break;
                }
                return false;
            }
        });

        m_context_main = this;
    }

    public String getTime() {
        Calendar now;
        now = Calendar.getInstance();
        String am = "AM";
        String time_str = "10.00 AM";
        try {
            int hour = now.get(Calendar.HOUR);
            if (hour == 0) {
                hour = 12;
            }
            int minute = now.get(Calendar.MINUTE);
            Log.d("HOUR AND MIN", hour + ":" + minute);
            int ampm = now.get(Calendar.AM_PM);
            if (ampm == Calendar.AM) {
                am = "  AM...";
            } else {
                am = "  PM...";
            }
            if (minute < 10) {
                time_str = hour + ":" + "0" + minute + am;
            } else {
                time_str = hour + ":" + minute + am;
            }
        } catch (Exception e) {
            Log.d("TIME EXCEPTION", "NO TIME");
        }
        return time_str;
    }
}

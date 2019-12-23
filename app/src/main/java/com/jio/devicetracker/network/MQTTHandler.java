package com.jio.devicetracker.network;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.jio.devicetracker.util.Constant;
import com.jio.mqttclient.JiotMqttCallback;
import com.jio.mqttclient.JiotMqttClient;
import com.jio.mqttclient.JiotMqttConnectOptions;
import com.jio.mqttclient.JiotMqttCreateOptions;
import com.jio.mqttclient.JiotMqttDeliveryToken;
import com.jio.mqttclient.JiotMqttToken;

import java.util.UUID;

public class MQTTHandler extends AppCompatActivity {

    private JiotMqttClient jiotMqttClient = null;
    private JiotMqttConnectOptions options = null;
    private String sessionID = null;

    public JiotMqttClient getMQTTClient(Context context) {
        if (jiotMqttClient == null) {
            JiotMqttCreateOptions jiotMqttCreateOptions = new JiotMqttCreateOptions(sessionID, Constant.MQTT_USER_NAME, Constant.MQTT_PASSWORD, Constant.MQTT_URL);
            options = new JiotMqttConnectOptions();
            options.setAutoReconnect(false);
            jiotMqttClient = new JiotMqttClient(context, new JiotMqttCallback() {
                @Override
                public void onConnectComplete(JiotMqttToken jiotMqttToken) {
                    Log.d("TAG --> ", "Connection completed");
                }

                @Override
                public void onConnectFailed(JiotMqttToken jiotMqttToken, Throwable throwable) {
                    Log.d("TAG --> ", "Connection failed");
                }

                @Override
                public void onMessageDelivered(JiotMqttDeliveryToken jiotMqttDeliveryToken) {
                    Log.d("TAG --> ", "Message delivered");
                }

                @Override
                public void onSubscribeCompleted(JiotMqttToken jiotMqttToken) {
                    Log.d("TAG --> ", "Subscription completed");
                }

                @Override
                public void onSubscribeFailed(JiotMqttToken jiotMqttToken, Throwable throwable) {
                    Log.d("TAG --> ", "Subscription failed");
                }

                @Override
                public void onUnsubscribeCompleted(JiotMqttToken jiotMqttToken) {

                }

                @Override
                public void onUnsubscribeFailed(JiotMqttToken jiotMqttToken, Throwable throwable) {

                }

                @Override
                public void onDisconnectCompleted(JiotMqttToken jiotMqttToken) {
                    Log.d("TAG --> ", "Disconnection completed");
                }

                @Override
                public void onDisconnectFailed(JiotMqttToken jiotMqttToken, Throwable throwable) {
                    Log.d("TAG --> ", "Disconnection failed");
                }

                @Override
                public void onMessageArrived(String s, String s1) {

                }

                @Override
                public void onConnectionLost(Throwable throwable) {

                }
            }, jiotMqttCreateOptions);
            return jiotMqttClient;
        }
        return jiotMqttClient;
    }

    public void connetMQTT() {
        if(jiotMqttClient != null && options != null) {
            jiotMqttClient.connect(options);
        }
    }

    public void disconnectMQTT() {
        if(jiotMqttClient != null) {
            jiotMqttClient.disconnect();
        }
    }

    public void publishMessage(String topic, String message) {
        if(jiotMqttClient != null) {
            jiotMqttClient.publish(topic, message);
        }
    }

    public void subscribe(String topic) {
        if(jiotMqttClient != null) {
            jiotMqttClient.subscribe(topic);
        }
    }

    public String getSessionId() {
        if(sessionID == null) {
            sessionID = UUID.randomUUID().toString();
            Log.d("Session Id --> ", sessionID);
            return sessionID;
        }
        Log.d("Session Id --> ", sessionID);
        return sessionID;
    }
}

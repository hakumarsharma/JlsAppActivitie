/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.network;

import android.content.Context;
import android.util.Log;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.util.Constant;
import com.jio.mqttclient.JiotMqttCallback;
import com.jio.mqttclient.JiotMqttClient;
import com.jio.mqttclient.JiotMqttConnectOptions;
import com.jio.mqttclient.JiotMqttCreateOptions;
import com.jio.mqttclient.JiotMqttDeliveryToken;
import com.jio.mqttclient.JiotMqttSSLOptions;
import com.jio.mqttclient.JiotMqttToken;

/**
 * MQTT Manager class, It is a main class related to MQTT which is responsible for MQTT connection, publishing of messages and disconnection.
 */
public class MQTTManager {

    public static JiotMqttClient jiotMqttClient = null;
    private static JiotMqttConnectOptions options = null;

    /**
     * It takes MQTT URL, MQTT User name, MQTT Password and IMEI number as a parameter
     * @param context
     * @return the MQTTClient object
     */
    public JiotMqttClient getMQTTClient(Context context) {
        if (jiotMqttClient == null) {
            JiotMqttSSLOptions jiotMqttSSLOptions = new JiotMqttSSLOptions(R.raw.ca, "ril@1234");
            JiotMqttCreateOptions jiotMqttCreateOptions = new JiotMqttCreateOptions(new DBManager(context).getAdminLoginDetail().getPhoneNumber(), Constant.MQTT_USER_NAME, Constant.MQTT_PASSWORD, Constant.MQTT_STG_URL, jiotMqttSSLOptions);
            options = new JiotMqttConnectOptions();
            options.setAutoReconnect(false);
            if (context != null) {
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
                        Log.d("TAG --> ", "On Unsubscribe completion");
                    }

                    @Override
                    public void onUnsubscribeFailed(JiotMqttToken jiotMqttToken, Throwable throwable) {
                        Log.d("TAG --> ", "On Unsubscribe failure");
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
                        Log.d("TAG --> ", "On message arrival");
                    }

                    @Override
                    public void onConnectionLost(Throwable throwable) {
                        Log.d("TAG --> ", "On Connection lost");
                    }
                }, jiotMqttCreateOptions);
                return jiotMqttClient;
            }
        }
        return jiotMqttClient;
    }

    /**
     * Connects to the MQTT server
     */
    public void connetMQTT() {
        if (jiotMqttClient != null && options != null && jiotMqttClient.isConnected() != true) {
            jiotMqttClient.connect(options);
        }
    }

    /**
     * Disconnects to the MQTT server
     */
    public void disconnectMQTT() {
        if (jiotMqttClient != null) {
            jiotMqttClient.disconnect();
        }
    }

    /**
     * Publishes the message with topic
     * @param topic
     */
    public void publishMessage(String topic, String message) {
        if (jiotMqttClient != null) {
            jiotMqttClient.publish(topic, message);
        }
    }

    /**
     * For the subscription to the topic
     * @param topic
     */
    public void subscribe(String topic) {
        if (jiotMqttClient != null) {
            jiotMqttClient.subscribe(topic);
        }
    }
}

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

package com.example.nutapp;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;



public class CameraAccessibilityService  extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
       Log.d("AccessibilityEvent",event.getPackageName().toString());
        Toast.makeText(getApplicationContext(), "Got event from: onAccessibilityEvent", Toast.LENGTH_LONG).show();
        AccessibilityNodeInfo interactedNodeInfo =
                event.getSource();
        final List<AccessibilityNodeInfo> list = interactedNodeInfo.findAccessibilityNodeInfosByText("Take photo");
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (AccessibilityNodeInfo node : list) {
                    Log.i("ACCESSCAM", "ACC::onAccessibilityEvent: click " + node);
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        },500);
    }

    @Override
    public void onInterrupt() {
        Log.d("onInterrupt","interrupt called");
    }

    @Override
    protected void onServiceConnected() {
        //super.onServiceConnected();
        Toast.makeText(getApplicationContext(), "Got event from: SERVICE", Toast.LENGTH_LONG).show();
        Log.d("onServiceConnected","onServiceConnectedddddddddddddddddddddddddddddddddd");
    }
}

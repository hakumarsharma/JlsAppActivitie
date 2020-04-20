package com.example.nutapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.media.AudioManager;
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

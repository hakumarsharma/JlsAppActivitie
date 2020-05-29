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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutapp.ui.home.HomeFragment;
import com.nutspace.nut.api.model.BleDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BleDetailsAdapter extends RecyclerView.Adapter<BleDetailsAdapter.BleViewHolder> implements PopupMenu.OnMenuItemClickListener {

    public BluetoothAdapter m_bluetoothAdpater;
    String addr_val = "";
    TextView m_deviceStatus;
    ImageButton m_connectButton;
    String m_globalAddr;
    public static final int REQUEST_ATTACH_ASSET = 123;

    public BluetoothGattCallback m_BluetoothGattCallbacks;
    public HomeFragment m_fragment;
    public BleViewHolder m_BleViewHolder;
    RecyclerView m_rv;
    BluetoothGatt gatt;
    Map<String, BleViewHolder> m_listProgressBar;

    Calendar now;
    int m_pos = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_connect:
                Log.d("MENUUUUUU", "CONNECT MENU");
                //m_fragment.startConnecting(m_pos);
                break;
            case R.id.devices_details:
                Intent startMain = new Intent(m_fragment.getActivity(), CardDetails.class);
                startMain.putExtra("POSITION_NUT", m_pos);
                startMain.putExtra("ADDRRESS", BleDetails.get(m_pos).deviceAddress);
                startMain.putExtra("LOCATION", m_globalAddr);
                Log.d("ICONVAL", m_listProgressBar.get(BleDetails.get(m_pos).deviceAddress).deviceDetails.getText().toString());
                startMain.putExtra("ICON", m_listProgressBar.get(BleDetails.get(m_pos).deviceAddress).deviceDetails.getText().toString());
                startMain.putExtra("STATUS", m_listProgressBar.get(BleDetails.get(m_pos).deviceAddress).deviceStatus.getText().toString());
                m_fragment.startActivity(startMain);
                break;
            case R.id.navigation_location:
                m_fragment.readLocationDetails();
                break;
            case R.id.devices_rename:
                Intent startAttach = new Intent(m_fragment.getActivity(), JioAttachAssets.class);
                startAttach.putExtra("POSITION_NUT", m_pos);
                startAttach.putExtra("POSITION_ADDR", BleDetails.get(m_pos).deviceAddress);
                m_fragment.startActivityForResult(startAttach, REQUEST_ATTACH_ASSET);
                break;

            default:
                break;

        }
        return false;
    }

    public void setFragment(HomeFragment frag) {
        m_fragment = frag;
    }

    List<BleDetails> BleDetails = new ArrayList<BleDetails>();

    public BleDetailsAdapter(List<BleDetails> bledeviceDetails, HomeFragment galleryObj, RecyclerView rv) {
        this.BleDetails = bledeviceDetails;
        this.m_fragment = galleryObj;
        this.m_rv = rv;
        m_listProgressBar = new HashMap<String, BleViewHolder>();
    }


    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewlayout, parent, false);
        BleViewHolder pvh = new BleViewHolder(v);
        m_BleViewHolder = pvh;

        preferences = parent.getContext().getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        return pvh;

    }

    @Override
    public int getItemCount() {
        Log.d("SIZE", this.BleDetails.size() + "");
        return this.BleDetails.size();
    }

    public void setCameraGalleryImage(String deviceAddress, Uri mImageCamera) {
        Log.d("SETCAMIMAGE", mImageCamera.toString());
        if (!mImageCamera.equals(Uri.EMPTY)) {
            m_listProgressBar.get(deviceAddress).m_lefticon.setImageURI(mImageCamera);
        }
    }

    public void setCustomImage(String customName, ImageView leftIcon) {
        String prefix = customName.split("\\.")[0].trim().toLowerCase(Locale.ROOT);
        switch (prefix) {
            case "key":
                leftIcon.setImageResource(R.drawable.key_purple);
                break;
            case "wallet":
                leftIcon.setImageResource(R.drawable.wallert_purple);
                break;
            case "laptop":
                leftIcon.setImageResource(R.drawable.olaptop_purple);
                break;
            case "suitcase":
                leftIcon.setImageResource(R.drawable.suitcase_purple);
                break;
            case "purse":
                leftIcon.setImageResource(R.drawable.purse_purple);
                break;
            case "others":
                leftIcon.setImageResource(R.drawable.other_purple);
                break;
            default:
                leftIcon.setImageResource(R.drawable.other_purple);
                break;
        }
    }

    public void setImageAsset(String deviceAddress, String friendlyName, int pos, String customName) {
        Log.d("CHANGEASSET", friendlyName.toLowerCase(Locale.ROOT) + "$" + deviceAddress + "$" + pos);
        switch (friendlyName.toLowerCase(Locale.ROOT)) {
            case "key":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.key_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Key." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Key." + customName);
                }
                break;
            case "wallet":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.wallert_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Wallet." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Wallet." + customName);
                }
                break;
            case "laptop":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.olaptop_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Laptop." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Laptop." + customName);
                }
                break;
            case "suitcase":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.suitcase_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Suitcase." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Suitcase." + customName);
                }
                break;
            case "purse":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.purse_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Purse." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Purse." + customName);
                }
                break;
            case "others":
                m_listProgressBar.get(deviceAddress).m_lefticon.setImageResource(R.drawable.other_purple);
                if (customName.isEmpty()) {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Others." + pos);
                } else {
                    m_listProgressBar.get(deviceAddress).deviceDetails.setText("Others." + customName);
                }
                break;

            default:
                break;
        }
    }

    public void setFriendlyName(String deviceAddress, String friendlyName) {
        m_listProgressBar.get(deviceAddress).deviceDetails.setText(friendlyName);
    }

    public void setProgressBarForItem(String deviceAddress, int rssi) {
        Log.d("RSSI", deviceAddress + ":::" + rssi);
    }


    public void showPopupMenu(View view, int position) {
        // inflate menu
        m_pos = position;
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.SampleMenu);
        PopupMenu popup = new PopupMenu(wrapper, view, Gravity.NO_GRAVITY);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.devices_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void setLocationDetails(String addr) {
        Log.d("SIZEOFHOLDER", m_listProgressBar.size() + "");
        if (addr.isEmpty()) {
            addr = "Location not known";
        }
        m_globalAddr = addr;
        for (String str : m_listProgressBar.keySet()) {
            Log.d("OBJECTS", m_listProgressBar.get(str) + "");
            m_listProgressBar.get(str).device_distance.setTypeface(JioUtils.mTypeface(m_fragment.getContext(), 3));
            m_listProgressBar.get(str).device_distance.setText(addr);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final BleViewHolder holder, final int position) {

        Log.d("ONBIND", "ONBIND CALLED");

        m_listProgressBar.put(BleDetails.get(position).deviceAddress, holder);
        holder.m_dev_address = BleDetails.get(position).deviceAddress;
        setProgressBarForItem(BleDetails.get(position).deviceAddress, 100 + Integer.parseInt(BleDetails.get(position).deviceRssi));
        //holder.deviceDetails.setText(BleDetails.get(position).deviceName + "-" + BleDetails.get(position).deviceAddress);

        //holder.deviceDetails.setText(BleDetails.get(position).deviceName + "." + position);
        String customName = preferences.getString(holder.m_dev_address + "CUSTOMNAME", "JioTag" + "." + position);
        //holder.deviceDetails.setText("JioTag" + "." + position);
        holder.deviceDetails.setText(customName);
        setCustomImage(customName,holder.m_lefticon);

        holder.deviceDetails.setTypeface(JioUtils.mTypeface(m_fragment.getContext(), 5));
        now = Calendar.getInstance();
        String am = "AM";
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
                holder.m_awayTowards.setText(hour + ":" + "0" + minute + am);
            } else {
                holder.m_awayTowards.setText(hour + ":" + minute + am);
            }
            holder.m_awayTowards.setTypeface(JioUtils.mTypeface(m_fragment.getContext(), 3));

        } catch (Exception e) {
            Log.d("TIME EXCEPTION", "NO TIME");
        }

        String isFar = "Far";
        if (BleDetails.get(position).mDistance > 5) {
            isFar = "Far";
        } else {
            isFar = "Nearby";
        }
        //holder.device_distance.setText("Device Distance: " + String.format("%.2f", BleDetails.get(position).m_distance) + " m");
        String status = "";
        Log.d("DEV_CON", holder.deviceStatus.getText().toString());
        if (holder.deviceStatus.getText().toString().contains("|")) {
            status = holder.deviceStatus.getText().toString().split("|")[0];
        } else {
            status = holder.deviceStatus.getText().toString();
        }
        if (status.isEmpty()) {
            status = "Disconnected";
        }
        holder.deviceStatus.setText(status + " | " + isFar);
        holder.deviceStatus.setTypeface(JioUtils.mTypeface(m_fragment.getContext(), 5));
        m_deviceStatus = holder.deviceStatus;
/*        if(addr_val.isEmpty() || addr_val.equalsIgnoreCase("Location Not Known")){
            addr_val=m_fragment.getLocationDetails();
        }*/
        // holder.device_distance.setText(addr_val);
        //holder.device_distance.setTypeface(JioUtils.mTypeface(m_fragment.getContext(), 3));
        holder.m_lefticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.m_lefticon.setPressed(true);
                Intent startAttach = new Intent(m_fragment.getActivity(), JioAttachAssets.class);
                startAttach.putExtra("POSITION_NUT", position);
                startAttach.putExtra("POSITION_ADDR", BleDetails.get(position).deviceAddress);
                String dev=BleDetails.get(position).deviceAddress;
                Log.d("CUSTOMO",m_listProgressBar.get(dev).deviceDetails.getText().toString());
                startAttach.putExtra("POSITION_NAME",m_listProgressBar.get(dev).deviceDetails.getText().toString()+"" );
                m_fragment.startActivityForResult(startAttach, REQUEST_ATTACH_ASSET);
            }
        });


        holder.device_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BLETEXT", "BLE TEXT AT POSITION" + position);
                //holder.deviceStatus.setPressed(true);
                holder.itemView.setPressed(true);
                //Toast.makeText(v.getContext(), "TEXT AT POS: " + position, Toast.LENGTH_SHORT).show();
                Intent startMain = new Intent(m_fragment.getActivity(), CardDetails.class);
                startMain.putExtra("POSITION_NUT", position);
                startMain.putExtra("ADDRRESS", BleDetails.get(position).deviceAddress);
                startMain.putExtra("LOCATION", m_globalAddr);
                Log.d("ICONVAL", holder.deviceDetails.getText().toString());
                startMain.putExtra("ICON", holder.deviceDetails.getText().toString());
                startMain.putExtra("STATUS", holder.deviceStatus.getText().toString());
                m_fragment.startActivity(startMain);
            }
        });

        holder.deviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BLETEXT", "BLE TEXT AT POSITION" + position);
                //holder.deviceStatus.setPressed(true);
                holder.itemView.setPressed(true);
                //Toast.makeText(v.getContext(), "TEXT AT POS: " + position, Toast.LENGTH_SHORT).show();
                Intent startMain = new Intent(m_fragment.getActivity(), CardDetails.class);
                startMain.putExtra("POSITION_NUT", position);
                startMain.putExtra("ADDRRESS", BleDetails.get(position).deviceAddress);
                startMain.putExtra("LOCATION", m_globalAddr);
                Log.d("ICONVAL", holder.deviceDetails.getText().toString());
                startMain.putExtra("ICON", holder.deviceDetails.getText().toString());
                startMain.putExtra("STATUS", holder.deviceStatus.getText().toString());
                m_fragment.startActivity(startMain);
            }
        });

        holder.deviceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.deviceDetails.setPressed(true);
                holder.itemView.setPressed(true);
                Log.d("BLETEXT", "BLE TEXT AT POSITION" + position);
                //Toast.makeText(v.getContext(), "TEXT AT POS: " + position, Toast.LENGTH_SHORT).show();
                Intent startMain = new Intent(m_fragment.getActivity(), CardDetails.class);
                startMain.putExtra("POSITION_NUT", position);
                startMain.putExtra("ADDRRESS", BleDetails.get(position).deviceAddress);
                startMain.putExtra("LOCATION", m_globalAddr);
                Log.d("ICONVAL", holder.deviceDetails.getText().toString());
                startMain.putExtra("ICON", holder.deviceDetails.getText().toString());
                startMain.putExtra("STATUS", holder.deviceStatus.getText().toString());
                m_fragment.startActivity(startMain);
            }
        });

        holder.connect_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CONNECTBUTTON", "CONNECT AT POSITION" + position);
                //Toast.makeText(v.getContext(), "CONNECT AT POS: " + position, Toast.LENGTH_SHORT).show();
                m_deviceStatus = holder.deviceStatus;
                m_connectButton = holder.connect_device;
                m_fragment.setItemPosition(position);
                showPopupMenu(holder.connect_device, position);
            }
        });

    }

    /*public void setAlarmStatus(boolean status, int position) {
        Log.d("DEVSTATINADAPT", status + "");
        final boolean stat = status;
    }*/

    public void setDistanceOfNut(final BleDevice dev, final String isDist) {
        Log.d("DISTANCE", isDist);
        m_fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("DISTANCE", "addr" + dev.address + ":" + isDist);
                if (m_listProgressBar.get(dev.address).deviceStatus.getText().toString().toLowerCase(Locale.ROOT).contains("connected")) {
                    m_listProgressBar.get(dev.address).deviceStatus.setText("Connected | " + isDist);
                } else {
                    m_listProgressBar.get(dev.address).deviceStatus.setText("Disconnected | " + isDist);
                }
            }
        });
    }

    public void setDeviceStatus(boolean status, int position, final BleDevice dev) {
        Log.d("DEVSTATINADAPT", status + "");
        final boolean stat = status;
        if(m_fragment != null && m_fragment.getActivity() != null) {
            m_fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String isDistance = "";
                    if (m_listProgressBar.get(dev.address) != null) {
                        if (m_listProgressBar.get(dev.address).deviceStatus.toString().toLowerCase(Locale.ROOT).contains("far")) {
                            isDistance = "Far";
                        } else {
                            isDistance = "Nearby";
                        }
                        if (stat == true) {
                            Log.d("CONNECTED", "ADAPTER CONN");
                            m_listProgressBar.get(dev.address).deviceStatus.setText("Connected | " + isDistance);
                            m_listProgressBar.get(dev.address).deviceStatus.setTextColor(0xFF1A8905);
                            //m_deviceStatus.setText("Connected");
                        } else {
                            Log.d("DISCONNECTED", "ADAPTER DISC");
                            //m_deviceStatus.setText("Disconnected");
                            m_listProgressBar.get(dev.address).deviceStatus.setText("Disconnected | " + isDistance);
                            m_listProgressBar.get(dev.address).deviceStatus.setTextColor(Color.GRAY);
                        }
                    }
                }
            });
        }
    }

    public void setGattCallback(BluetoothGattCallback gattCallback) {
        m_BluetoothGattCallbacks = gattCallback;
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView deviceDetails;
        ImageButton connect_device;
        TextView deviceStatus;
        TextView device_distance;
        TextView m_awayTowards;
        ImageView m_lefticon;
        String m_dev_address;

        BleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);

            //Text
            deviceDetails = (TextView) itemView.findViewById(R.id.device_details);
            deviceStatus = (TextView) itemView.findViewById(R.id.device_status);
            device_distance = (TextView) itemView.findViewById(R.id.device_distance);
            //Buttons
            connect_device = (ImageButton) itemView.findViewById(R.id.connect_devices);
            m_awayTowards = (TextView) itemView.findViewById(R.id.tView);
            m_lefticon = (ImageView) itemView.findViewById(R.id.lefticon);
        }
    }
}


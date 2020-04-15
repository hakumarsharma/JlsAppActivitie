package com.example.nutapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceAlertAdapter extends RecyclerView.Adapter<DeviceAlertAdapter.BleViewHolder> {

    public DeviceAlertAdapter.BleViewHolder m_BleViewHolder;
    RecyclerView m_rv;
    Map<String, BleViewHolder> m_listProgressBar;
    List<SettingsDetails> BleDetails = new ArrayList<SettingsDetails>();
    DeviceAlertSetting m_activity;
    Context m_context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String HEX_DEVICE_ADDRESS;

    public DeviceAlertAdapter(List<SettingsDetails> bledeviceDetails, DeviceAlertSetting currentAct, RecyclerView rv) {
        this.BleDetails = bledeviceDetails;
        this.m_rv = rv;
        this.m_activity = currentAct;
        HEX_DEVICE_ADDRESS = this.m_activity.getHexAddress();
        m_listProgressBar = new HashMap<String, BleViewHolder>();
    }


    @Override
    public int getItemCount() {
        Log.d("SIZE", this.BleDetails.size() + "");
        return this.BleDetails.size();
    }

    public void turnOnOffDeviceAlertReconnection(boolean val) {
        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_RECONNECTION", true + ""));
        Log.d("DEVICE_RECONNECTION", "DEVICE_ALERT_RECONNECTION" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_RECONNECTION", val + "");
        Log.d("DEVICE_RECONNECTION", "DEVICE_ALERT_RECONNECTION VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffDeviceAlertReminder(boolean val) {
        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REMINDER", true + ""));
        Log.d("DEVICE_ALERT_REMINDER", "DEVICE_ALERT_REMINDER" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REMINDER", val + "");
        Log.d("DEVICE_ALERT_REMINDER", "DEVICE_ALERT_REMINDER VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffDeviceAlertMain(boolean val) {
        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICEALERT", true + ""));
        Log.d("DEVICEALERT", "DEVICEALERT" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "DEVICEALERT", val + "");
        Log.d("DEVICEALERT", "DEVICEALERT VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffDeviceAlertRepeat(boolean val) {

        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REPEAT", false + ""));
        Log.d("DEVICE_ALERT_REPEAT", "DEVICE_ALERT_REPEAT" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REPEAT", val + "");
        Log.d("DEVICE_ALERT_REPEAT", "DEVICE_ALERT_REPEAT" + val);
        prefEditor.commit();
    }

    public void turnOnOffDeviceAlertDuration(Spinner spin, String selectedItem) {

        String devicealert_duration_switch = preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_DURATION", "5sec");
        Log.d("DEVICE_ALERT_DURATION", "DEVICE_ALERT_DURATION OLD" + devicealert_duration_switch);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_DURATION", selectedItem);
        Log.d("DEVICE_ALERT_DURATION", "DEVICE_ALERT_DURATION NEW" + selectedItem);
        prefEditor.commit();
    }

    private int getSpinnerIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull BleViewHolder holder, int position) {
        final int pos = position;
        final Spinner m_spinner;
        if (position == 0) {
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_main_header));
            holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.attach_asset_round));
            holder.cv.setBackground(m_context.getResources().getDrawable(R.drawable.attach_asset_round));
            holder.devicealert_header.setTextColor(Color.WHITE);
            boolean oldPhoneVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICEALERT", true + ""));
            holder.devicealert_switch.setChecked(oldPhoneVal);
            /*ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                    *//*width*//* ViewGroup.LayoutParams.MATCH_PARENT,
                    *//*height*//* ViewGroup.LayoutParams.WRAP_CONTENT
            );
            holder.itemView.setLayoutParams(param);*/
            holder.devicealert_duration_switch.setVisibility(View.GONE);
            holder.devicealert_details.setVisibility(View.GONE);
        }
        if (position == 1) {
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_alerts_header));
            holder.devicealert_header.setTypeface(JioUtils.mTypeface(m_context, 3));
        }
        if (position == 4) {
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_notifications_header));
            holder.devicealert_header.setTypeface(JioUtils.mTypeface(m_context, 3));
        }
        if (position == 1 || position == 4) {
            holder.devicealert_duration_switch.setVisibility(View.GONE);
            holder.devicealert_switch.setVisibility(View.GONE);
            holder.devicealert_details.setVisibility(View.GONE);
/*            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                    *//*width*//* ViewGroup.LayoutParams.MATCH_PARENT,
                    *//*height*//* ViewGroup.LayoutParams.WRAP_CONTENT
            );
            holder.itemView.setLayoutParams(param);*/
            holder.devicealert_header.setTextColor(Color.BLUE);
            holder.devicealert_header.setTextSize(12);
        }
        if (position == 2) {
            holder.devicealert_duration_switch.setVisibility(View.VISIBLE);
            holder.devicealert_switch.setVisibility(View.INVISIBLE);
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_header_duration));
            String devicealert_duration_switch = preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_DURATION", "5sec");
            //String selectedItem = (String) holder.devicealert_duration_switch.getSelectedItem();
            int indexSpinner = getSpinnerIndex(holder.devicealert_duration_switch, devicealert_duration_switch);
            holder.devicealert_duration_switch.setSelection(indexSpinner);
            //turnOnOffDeviceAlertDuration(holder.devicealert_duration_switch, selectedItem);

            /*m_spinner = holder.devicealert_duration_switch;
            String sel = (String) holder.devicealert_duration_switch.getSelectedItem();
            int position_spin = getSpinnerIndex(m_spinner, sel);
            holder.devicealert_duration_switch.setSelection(position_spin);*/
            m_spinner = holder.devicealert_duration_switch;
            holder.devicealert_duration_switch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    turnOnOffDeviceAlertDuration(m_spinner, (String) m_spinner.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } else if (position == 3) {
            holder.devicealert_duration_switch.setVisibility(View.INVISIBLE);
            holder.devicealert_switch.setVisibility(View.VISIBLE);
            boolean devicealert_repeat = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REPEAT", false + ""));
            holder.devicealert_switch.setChecked(devicealert_repeat);
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_repeat));
        } else if (position == 5) {
            holder.devicealert_duration_switch.setVisibility(View.INVISIBLE);
            holder.devicealert_switch.setVisibility(View.VISIBLE);
            boolean devicealert_reminder = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_REMINDER", true + ""));
            holder.devicealert_switch.setChecked(devicealert_reminder);
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_reminder_for_connection));
        } else if (position == 6) {
            holder.devicealert_duration_switch.setVisibility(View.INVISIBLE);
            holder.devicealert_switch.setVisibility(View.VISIBLE);
            boolean devicealert_reconnection = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "DEVICE_ALERT_RECONNECTION", true + ""));
            holder.devicealert_switch.setChecked(devicealert_reconnection);
            holder.devicealert_header.setText(m_context.getResources().getString(R.string.devicealert_alert_on_reconnection));
        }
        if (position == 2) {
            holder.devicealert_details.setText(m_context.getResources().getString(R.string.devicealert_header_duration_details));
        } else if (position == 3) {
            holder.devicealert_details.setText(m_context.getResources().getString(R.string.devicealert_repeat_details));
        } else if (position == 5) {
            holder.devicealert_details.setText(m_context.getResources().getString(R.string.devicealert_reminder_for_connection_details));
        } else if (position == 6) {
            holder.devicealert_details.setText(m_context.getResources().getString(R.string.devicealert_alert_on_reconnection_details));
        }
        //holder.devicealert_duration_switch.setPopupBackgroundDrawable(holder.itemView.getContext().getDrawable(R.drawable.button_frame_blue));
        if(position!=0) {
            holder.devicealert_header.setTextColor(Color.BLACK);
            holder.devicealert_details.setTextColor(Color.BLACK);
            holder.devicealert_details.setTypeface(JioUtils.mTypeface(m_context, 3));
        }


        holder.devicealert_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (pos) {
                    case 0:
                        turnOnOffDeviceAlertMain(isChecked);
                        break;
                    case 3:
                        //repeat
                        turnOnOffDeviceAlertRepeat(isChecked);
                        break;
                    case 5:
                        //reminder
                        turnOnOffDeviceAlertReminder(isChecked);
                        break;
                    case 6:
                        //alert on reconnection
                        turnOnOffDeviceAlertReconnection(isChecked);
                        break;
                }
            }
        });

        if(position!=0) {
            if( (position!=1) && (position !=4)){
                holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
                holder.cv.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
            }
        }
        if (position == 1 || position == 4) {
            //holder.devicealert_header.setTextColor(Color.BLUE);
            //holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
            holder.itemView.setBackgroundColor(0xFFE8E8E8);
            holder.cv.setBackgroundColor(0xFFE8E8E8);
        }
    }

    @NonNull
    @Override
    public DeviceAlertAdapter.BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_alert_view, parent, false);
        DeviceAlertAdapter.BleViewHolder pvh = new DeviceAlertAdapter.BleViewHolder(v);
        m_BleViewHolder = pvh;
        m_context = parent.getContext();
        preferences = m_context.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        return pvh;
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView devicealert_header;
        TextView devicealert_details;
        Switch devicealert_switch;
        Spinner devicealert_duration_switch;


        BleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.devicealert_cv);
            //Text
            devicealert_header = (TextView) itemView.findViewById(R.id.devicealert_header);
            devicealert_details = (TextView) itemView.findViewById(R.id.devicealert_details);
            devicealert_switch = (Switch) itemView.findViewById(R.id.devicealert_switch);
            devicealert_duration_switch = (Spinner) itemView.findViewById(R.id.devicealert_duration_switch);
            //devicealert_duration_switch.setBackground(itemView.getContext().getDrawable(R.drawable.button_frame_blue));
/*            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    itemView.getContext(), R.array.duration_arrays, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            devicealert_duration_switch.setAdapter(adapter);*/
        }
    }

}


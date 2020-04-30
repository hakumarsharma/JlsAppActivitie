package com.example.nutapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.BleViewHolder> {

    public PhoneAdapter.BleViewHolder m_BleViewHolder;
    RecyclerView m_rv;
    Map<String, PhoneAdapter.BleViewHolder> m_listProgressBar;
    List<SettingsDetails> BleDetails = new ArrayList<SettingsDetails>();
    PhoneAlertSetting m_activity;
    Context m_context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String HEX_DEVICE_ADDRESS;

    public PhoneAdapter(List<SettingsDetails> bledeviceDetails, PhoneAlertSetting currentAct, RecyclerView rv) {
        this.BleDetails = bledeviceDetails;
        this.m_rv = rv;
        this.m_activity = currentAct;
        HEX_DEVICE_ADDRESS = this.m_activity.getHexAddress();
        m_listProgressBar = new HashMap<String, PhoneAdapter.BleViewHolder>();
    }


    @Override
    public int getItemCount() {
        Log.d("SIZE", this.BleDetails.size() + "");
        return this.BleDetails.size();
    }

    public void turnOnOffPhoneAlertMain(boolean val) {
        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "PHONEALERT", true + ""));
        Log.d("PHONE_ALERT", "PHONE_ALERT" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "PHONEALERT", val + "");
        Log.d("PHONE_ALERT", "PHONE_ALERT VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffPhoneAlertRepeat(boolean val) {

        boolean oldVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_REPEAT", false + ""));
        Log.d("PHONE_ALERT_REPEAT", "PHONE_ALERT_REPEAT" + oldVal);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_REPEAT", val + "");
        Log.d("PHONE_ALERT_REPEAT", "PHONE_ALERT_REPEAT_VAL" + val);
        prefEditor.commit();
    }

    public void turnOnOffPhoneAlertDuration(Spinner spin, String selectedItem) {

        String phonealertDurationSwitch = preferences.getString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_DURATION", "5sec");
        Log.d("PHONE_ALERT_DURATION", "PHONE_ALERT_DURATION OLD" + phonealertDurationSwitch);
        prefEditor.putString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_DURATION", selectedItem);
        Log.d("PHONE_ALERT_DURATION", "PHONE_ALERT_DURATION NEW" + selectedItem);
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
        final Spinner mSpinner;

        if (position == 0) {
            holder.phonealert_header.setText(m_context.getResources().getString(R.string.phonealert_main_header));
            holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.attach_asset_round));
            holder.cv.setBackground(m_context.getResources().getDrawable(R.drawable.attach_asset_round));
            holder.phonealert_header.setTextColor(Color.WHITE);
            boolean oldPhoneVal = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "PHONEALERT", true + ""));
            holder.phonealert_switch.setChecked(oldPhoneVal);
            //holder.itemView.setBackgroundColor(0x353398);
/*            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                    *//*width*//* ViewGroup.LayoutParams.MATCH_PARENT,
                    *//*height*//* ViewGroup.LayoutParams.WRAP_CONTENT
            );
            holder.itemView.setLayoutParams(param);*/
            holder.duration_switch.setVisibility(View.GONE);
            holder.phonealert_details.setVisibility(View.GONE);
        }
        if (position == 1) {
            holder.duration_switch.setVisibility(View.VISIBLE);
           // holder.duration_switch.setBackgroundColor(Color.BLUE);
            holder.phonealert_switch.setVisibility(View.INVISIBLE);
            holder.phonealert_header.setText(m_context.getResources().getString(R.string.phonealert_header_duration));
            String phonealertDurationSwitch = preferences.getString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_DURATION", "5sec");
            int indexSpinner = getSpinnerIndex(holder.duration_switch, phonealertDurationSwitch);
            holder.duration_switch.setSelection(indexSpinner);
            //holder.duration_switch.setPopupBackgroundDrawable(holder.itemView.getContext().getDrawable(R.drawable.button_frame_blue));
            //holder.duration_switch.setBackground(holder.itemView.getContext().getDrawable(R.drawable.button_frame_blue));
            mSpinner = holder.duration_switch;
            holder.duration_switch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    turnOnOffPhoneAlertDuration(mSpinner, (String) mSpinner.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.e("PhoneAdater", "parent"+parent);
                }
            });
        } else if (position == 2) {
            holder.duration_switch.setVisibility(View.INVISIBLE);
            holder.phonealert_switch.setVisibility(View.VISIBLE);
            boolean phonealertRepeat = Boolean.valueOf(preferences.getString(HEX_DEVICE_ADDRESS + "PHONE_ALERT_REPEAT", false + ""));
            holder.phonealert_switch.setChecked(phonealertRepeat);
            holder.phonealert_header.setText(m_context.getResources().getString(R.string.phonealert_header_repeat));
        }


        if (position == 1) {
            holder.phonealert_details.setText(m_context.getResources().getString(R.string.phonealert_header_duration_details));
        } else if (position == 2) {

            holder.phonealert_details.setText(m_context.getResources().getString(R.string.phonealert_header_repeat_details));
        }

        holder.phonealert_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pos != 0) {
                    turnOnOffPhoneAlertRepeat(isChecked);
                } else {
                    turnOnOffPhoneAlertMain(isChecked);
                }
            }
        });

        if (position != 0) {
            holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
            holder.cv.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
            holder.phonealert_details.setTextColor(Color.BLACK);
            holder.phonealert_details.setTypeface(JioUtils.mTypeface(m_context, 3));
        }
    }

    @NonNull
    @Override
    public PhoneAdapter.BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_alert_view, parent, false);
        PhoneAdapter.BleViewHolder pvh = new PhoneAdapter.BleViewHolder(v);
        m_BleViewHolder = pvh;
        m_context = parent.getContext();
        preferences = m_context.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        return pvh;
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView phonealert_header;
        TextView phonealert_details;
        Switch phonealert_switch;
        Spinner duration_switch;


        BleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.phonealert_cv);
            //Text
            phonealert_header = (TextView) itemView.findViewById(R.id.phonealert_header);
            phonealert_details = (TextView) itemView.findViewById(R.id.phonealert_details);
            phonealert_switch = (Switch) itemView.findViewById(R.id.phonealert_switch);
            duration_switch = (Spinner) itemView.findViewById(R.id.duration_switch);
            //duration_switch.setBackground(itemView.getContext().getDrawable(R.drawable.button_frame_blue));
        }
    }

}

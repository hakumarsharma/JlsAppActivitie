package com.example.nutapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.BleViewHolder> {


    public BleViewHolder m_BleViewHolder;
    RecyclerView m_rv;
    Map<String, BleViewHolder> m_listProgressBar;
    List<SettingsDetails> BleDetails = new ArrayList<SettingsDetails>();
    Activity m_activity;
    Context m_context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public SettingsAdapter(List<SettingsDetails> bledeviceDetails, Activity currentAct, RecyclerView rv) {
        this.BleDetails = bledeviceDetails;
        this.m_rv = rv;
        this.m_activity = currentAct;
        m_listProgressBar = new HashMap<String, BleViewHolder>();
    }


    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_card, parent, false);
        BleViewHolder pvh = new BleViewHolder(v);
        m_BleViewHolder = pvh;
        m_context = parent.getContext();
        preferences = m_context.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        return pvh;

    }

    @Override
    public int getItemCount() {
        Log.d("SIZE", this.BleDetails.size() + "");
        return this.BleDetails.size();
    }

    public void turnOnOffPhoto(boolean val) {
        boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, false);
        Log.d("PHOTOSET", "FIRST TIME SETTING TRUE" + oldVal);
        prefEditor.putBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, val);
        Log.d("PHOTOSET", "FIRST TIME SETTING TRUE" + val);
        prefEditor.commit();
    }

    public void turnOnOffDisconnectionAlertSetting(boolean val) {
        boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, false);
        Log.d("DISCONNECTAL", "DISCONNECT ALERT VAL" + oldVal);
        prefEditor.putBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, val);
        Log.d("DISCONNECTAL", "DISCONNECT ALERT VAL" + val);
        prefEditor.commit();
    }

    @Override
    public void onBindViewHolder(@NonNull final BleViewHolder holder, final int position) {

        m_listProgressBar.put(BleDetails.get(position).m_settinsMain, holder);
        Log.d("HEADING", BleDetails.get(position).m_settinsMain.toString());
        holder.settingsHeader.setText(BleDetails.get(position).m_settinsMain.toString());
        holder.settingsHeader.setTypeface(JioUtils.mTypeface(m_activity, 2));
        holder.settingsDetails.setTypeface(JioUtils.mTypeface(m_activity, 3));
        holder.settingsDetails.setText(BleDetails.get(position).m_settingsDetails.toString());

        if (position == 0) {
            holder.feedback_lefticon.setVisibility(View.GONE);
            boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_PHOTO_CPATURE, false);
            holder.settings_switch.setChecked(oldVal);
        }
        if (position == 1) {
            holder.feedback_lefticon.setVisibility(View.GONE);
            boolean oldVal = preferences.getBoolean(JioUtils.MYPREFERENCES_DISCONNECTION_ALERT, false);
            holder.settings_switch.setChecked(oldVal);
        }
        if (position == 2) {
            holder.settings_switch.setVisibility(View.GONE);
            holder.feedback_lefticon.setBackground(m_activity.getResources().getDrawable(R.drawable.information));
            holder.feedback_lefticon.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent startInfo = new Intent(v.getContext(), Information.class);
                    m_activity.startActivityForResult(startInfo,JioUtils.LAUNCH_INFO);
                    //Toast.makeText(v.getContext(), "Information details Coming Soon!!!!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (position == 3) {
            holder.settings_switch.setVisibility(View.GONE);
            holder.feedback_lefticon.setBackground(m_activity.getResources().getDrawable(R.drawable.howtouse));
            holder.feedback_lefticon.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   Intent startAttach = new Intent(v.getContext(), Howtoadd.class);
                   m_activity.startActivityForResult(startAttach,JioUtils.LAUNCH_HOW);
                }
            });
        }

        if (position == 4) {
            holder.settings_switch.setVisibility(View.GONE);
            holder.feedback_lefticon.setBackground(m_activity.getResources().getDrawable(R.drawable.feedback));
            holder.feedback_lefticon.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent startAttach = new Intent(v.getContext(), Feedback.class);
                    m_activity.startActivityForResult(startAttach,JioUtils.LAUNCH_FEEDBACK);
                }
            });
        }
        holder.settings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (position == 0) {
                    turnOnOffPhoto(isChecked);
                }
                if(position == 1){
                    turnOnOffDisconnectionAlertSetting(isChecked);
                }
                // The toggle is enabled
            }
        });
        //holder.settigs_switch.
    }

    public static class BleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView settingsHeader;
        TextView settingsDetails;
        Switch settings_switch;
        ImageView feedback_lefticon;


        BleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.settings_cv);
            //Text
            settingsHeader = (TextView) itemView.findViewById(R.id.settings_header);
            settingsDetails = (TextView) itemView.findViewById(R.id.settings_details);
            settings_switch = (Switch) itemView.findViewById(R.id.settings_switch);
            feedback_lefticon=(ImageView)itemView.findViewById(R.id.feedback_lefticon);
        }
    }
}


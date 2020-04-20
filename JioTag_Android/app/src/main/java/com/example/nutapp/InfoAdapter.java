package com.example.nutapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.BleViewHolder> {


    public InfoAdapter(List<SettingsDetails> bledeviceDetails, Activity currentAct, RecyclerView rv) {
        this.BleDetails = bledeviceDetails;
        this.m_rv = rv;
        this.m_activity = currentAct;
        m_listProgressBar = new HashMap<String, InfoAdapter.BleViewHolder>();
    }


    @Override
    public int getItemCount() {
        Log.d("SIZE", this.BleDetails.size() + "");
        return this.BleDetails.size();
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.BleViewHolder holder, int position) {

        Log.d("HEADING", BleDetails.get(position).m_settinsMain.toString());
        holder.settingsHeader.setText(BleDetails.get(position).m_settinsMain.toString());
        holder.settingsHeader.setTypeface(JioUtils.mTypeface(m_activity, 2));
        holder.settingsDetails.setTypeface(JioUtils.mTypeface(m_activity, 3));
        holder.settingsDetails.setText(BleDetails.get(position).m_settingsDetails.toString());

        if (position != 1) {
            holder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
            holder.cv.setBackground(m_context.getResources().getDrawable(R.drawable.dialog_round_background));
        }

        if (position == 1) {
            holder.itemView.setBackgroundColor(0xFFE8E8E8);
            holder.cv.setBackgroundColor(0xFFE8E8E8);
            holder.settingsDetails.setVisibility(View.GONE);
            holder.settingsHeader.setTypeface(JioUtils.mTypeface(m_context, 3));
            holder.settingsHeader.setTextSize(12);
        }
    }

    public InfoAdapter.BleViewHolder m_BleViewHolder;
    RecyclerView m_rv;
    Map<String, InfoAdapter.BleViewHolder> m_listProgressBar;
    List<SettingsDetails> BleDetails = new ArrayList<SettingsDetails>();
    Activity m_activity;
    Context m_context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    @NonNull
    @Override
    public InfoAdapter.BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_card, parent, false);
        InfoAdapter.BleViewHolder pvh = new InfoAdapter.BleViewHolder(v);
        m_BleViewHolder = pvh;
        m_context = parent.getContext();
        preferences = m_context.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        return pvh;
    }


    public static class BleViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView settingsHeader;
        TextView settingsDetails;


        BleViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.info_cv);
            settingsHeader = (TextView) itemView.findViewById(R.id.info_phonealert_header);
            settingsDetails = (TextView) itemView.findViewById(R.id.info_phonealert_details);
        }
    }
}

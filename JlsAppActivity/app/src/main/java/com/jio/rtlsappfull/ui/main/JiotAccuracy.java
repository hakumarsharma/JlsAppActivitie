package com.jio.rtlsappfull.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.model.JiotRtlsRecords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JiotAccuracy extends Fragment {


    private PageViewModel pageViewModel;
    int index = 2;
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    View root;
    TextView accuracy_header;
    File m_rtlsLogFile;
    List<JiotRtlsRecords> m_RtlsRecords;
    TableLayout m_mainTable;
    public int m_sizeOfRecords = 0;

    public BroadcastReceiver m_refreshRecordsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("m_refreshRecordsReceiver", "m_refreshRecordsReceiver");
            readEntries();
        }
    };

    @Override
    public void onDestroy() {
        Log.d("onDestroyAcc", "onDestroy");
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(m_refreshRecordsReceiver);
            Log.d("UNREG", "JiotAccuracy receiver");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UNREGEX", "Exception JiotAccuracy");
        }
        m_refreshRecordsReceiver = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.setIndex(index);
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(getContext());
        m_rtlsLogFile = m_jiotSdkFileLoggerInstance.getRtlsLogFile();
        m_RtlsRecords = new ArrayList<JiotRtlsRecords>();
        IntentFilter refreshFilter = new IntentFilter("com.jio.refreshRtls");
        getContext().registerReceiver(m_refreshRecordsReceiver, refreshFilter);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (index == 2) {
            Log.d("onCreateViewACC", "onCreateView");
            root = inflater.inflate(R.layout.fragment_accuracy, container, false);
            m_mainTable = (TableLayout) root.findViewById(R.id.rtlsRecords);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("RESUMEACC", "onresume");
    }

    @Override
    public void onStart() {
        super.onStart();
        readEntries();
    }

    public void refreshTable() {
        m_mainTable.removeAllViewsInLayout();
        for (int i = 0; i < m_sizeOfRecords; i++) {

            TableRow rowCellId = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            rowCellId.setLayoutParams(lp);
            if(m_RtlsRecords.get(i).getM_recordType().equalsIgnoreCase("R")) {
                rowCellId.setBackgroundColor(Color.LTGRAY);
            }else{
                rowCellId.setBackgroundColor(Color.WHITE);
            }



            TextView tvCellId = new TextView(getContext());
            tvCellId.setText(m_RtlsRecords.get(i).getM_RtlsCellId() + "");

            TextView tvLat = new TextView(getContext());
            tvLat.setText(m_RtlsRecords.get(i).getM_rtlsLat() + "");
            tvLat.setGravity(Gravity.RIGHT);

            TextView tvLng = new TextView(getContext());
            tvLng.setText(m_RtlsRecords.get(i).getM_rtlsLng() + "");
            tvLng.setGravity(Gravity.RIGHT);

            TextView tvAcc = new TextView(getContext());
            tvAcc.setText(m_RtlsRecords.get(i).getM_rtlsAccuracy() + "");
            tvAcc.setGravity(Gravity.RIGHT);


            rowCellId.addView(tvCellId);
            rowCellId.addView(tvLat);
            rowCellId.addView(tvLng);
            rowCellId.addView(tvAcc);
            m_mainTable.addView(rowCellId);
        }
    }

    public void readEntries() {
        Log.d("READENT", "readEntries");
        BufferedReader bReader = null;
        m_RtlsRecords.clear();
        m_sizeOfRecords = 0;
        try {
            bReader = new BufferedReader(new FileReader(m_rtlsLogFile));
            String line;
            while ((line = bReader.readLine()) != null) {
                if (line.contains("RTLSRECORD") || line.contains("GOOGLERECORD")) {
                    m_sizeOfRecords++;
                    Log.d("RECORDS", " " + line + " ");
                    String[] text = line.split("\\$");
                    Log.d("RECORDLENGTH",text.length+"");
                    if(text.length >5) {
                        int i = 0;
                        for (String tex : text) {
                            Log.d("TEXT" + i, tex);
                            i++;
                        }
                        JiotRtlsRecords rtlsRecord = new JiotRtlsRecords();
                        if (line.contains("RTLSRECORD")) {
                            rtlsRecord.setM_recordType("R");
                        }else {
                            rtlsRecord.setM_recordType("G");
                        }
                        rtlsRecord.setM_RtlsCellId(Integer.parseInt(text[2].trim()));

                        String latFormatted=String.format("%.5f", Double.valueOf(text[3].trim()));
                        rtlsRecord.setM_rtlsLat(Double.valueOf(latFormatted.trim()));

                        String lngFormatted=String.format("%.5f", Double.valueOf(text[4].trim()));
                        rtlsRecord.setM_rtlsLng(Double.valueOf(lngFormatted.trim()));

                        String accuracyFormatted=String.format("%.2f", Double.valueOf((text[5].trim())));
                        rtlsRecord.setM_rtlsAccuracy(Double.valueOf((accuracyFormatted.trim())));
                        m_RtlsRecords.add(rtlsRecord);
                    }
                }
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (JiotRtlsRecords rec : m_RtlsRecords) {
            Log.d("PARSEDREC", rec.getM_RtlsCellId() + " " + rec.getM_rtlsLat() + " " + rec.getM_rtlsLng() + " " + rec.getM_rtlsAccuracy());
        }

        refreshTable();
    }


}
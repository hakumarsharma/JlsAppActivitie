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

package com.jio.devicetracker.view.geofence;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;

import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.GeofenceListAdapter;
import java.util.List;

public class GeofenceListViewFragment extends Fragment {
    private String deviceNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_geofence_list, container, false);
        DBManager mDbManager =  new DBManager(getActivity());
        deviceNumber = getActivity().getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        RecyclerView geoFenceListView = view.findViewById(R.id.geofence_list);
        List<GeofenceDetails> list = mDbManager.getGeofenceDetailsList(deviceNumber);
        GeofenceListAdapter adapter = new GeofenceListAdapter(list,getActivity(),deviceNumber);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        geoFenceListView.setLayoutManager(mLayoutManager);
        geoFenceListView.setAdapter(adapter);
        adapter.setOnItemClickPagerListener(new GeofenceListAdapter.AdapterListner() {
            @Override
            public void clickOnEdit(GeofenceDetails details) {
                String addressGeofence = Util.getAddressFromLocation(details.getLat(),details.getLng(),getActivity());
                Intent intent = new Intent(getActivity(), EditGeofenceActivity.class);
                intent.putExtra(Constant.MULTIPLE_GEOFENCE_LAT,details.getLat());
                intent.putExtra(Constant.MULTIPLE_GEOFENCE_LNG,details.getLng());
                intent.putExtra(Constant.GEOFENCE_RADIUS, details.getRadius());
                intent.putExtra(Constant.MULTIPLE_GEOFENCE_EDIT,true);
                intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
                intent.putExtra(Constant.GEOFENCE_ADDRESS,addressGeofence);
                getActivity().startActivityForResult(intent,110);
            }

            @Override
            public void createGeofence() {
                Intent intent = new Intent(getActivity(), CreateGeofenceActivity.class);
                intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
                intent.putExtra(Constant.CREATE_GEOFENCE,true);
                getActivity().startActivityForResult(intent,120);
            }
        });
        return view;
    }

}


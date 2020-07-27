package com.jio.devicetracker.view.geofence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;

import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.GeofenceListAdapter;
import com.jio.devicetracker.view.adapter.PeopleListAdapter;
import java.util.List;
import java.util.Locale;

public class GeofenceListViewFragment extends Fragment {

    private DBManager mDbManager;
    private static PeopleListAdapter peopleListAdapter;
    private List<MapData> mapDataList;
    private String groupId;
    private Locale locale = Locale.ENGLISH;
    private GeofenceListAdapter adapter;
    private List<GeofenceDetails> list;
    private  String deviceNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_geofence_list, container, false);
        mDbManager = new DBManager(getActivity());
       /* mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        groupId = getActivity().getIntent().getStringExtra(Constant.GROUP_ID);*/
        deviceNumber = getActivity().getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        RecyclerView geoFenceListView = view.findViewById(R.id.geofence_list);
        list = mDbManager.getGeofenceDetailsList(deviceNumber);
        adapter = new GeofenceListAdapter(list,getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        geoFenceListView.setLayoutManager(mLayoutManager);
        geoFenceListView.setAdapter(adapter);
        return view;
    }



    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    /*private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                strAddress.setLength(0);
                strAddress.append(fetchedAddress.getAddressLine(0)).append(" ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strAddress.toString();
    }*/
}


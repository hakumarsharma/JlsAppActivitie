package com.jio.devicetracker.view.geofence;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.BaseActivity;

import java.io.IOException;
import java.util.List;

public class GeofenceActivity extends BaseActivity implements View.OnClickListener  {
    private GeofenceMapFragment fragmentMap;
    private EditText addressText;
    private ImageView searchAddress;
    private ImageView cancelAddress;
    private FragmentManager manager;
    private FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        fragmentMap = new GeofenceMapFragment();
        initUI();
    }
    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(getResources().getColor(R.color.geofence_background));
        addressText = findViewById(R.id.address);
        searchAddress = findViewById(R.id.search);
        cancelAddress = findViewById(R.id.clear);
        searchAddress.setOnClickListener(this);
        cancelAddress.setOnClickListener(this);
        TextView memberName = findViewById(R.id.member_name);
        TextView memberAddrss = findViewById(R.id.member_address);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.map_fragment, fragmentMap).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                LatLng latlng = getLocationFromAddress(addressText.getText().toString());
                if(latlng != null) {
                    addFragment(latlng);
                } else {
                    Toast.makeText(this,Constant.ADDRESS_MESSAGE,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear:
                addressText.setText("");
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 10);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void addFragment(LatLng latLng){
        Bundle bundle = new Bundle();
        bundle.putDouble(Constant.LATITUDE, latLng.latitude);
        bundle.putDouble(Constant.LONGNITUDE, latLng.longitude);
        fragmentMap.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragmentMap);
        transaction.addToBackStack(null);
        transaction.add(R.id.map_fragment,fragmentMap).commit();
    }

}

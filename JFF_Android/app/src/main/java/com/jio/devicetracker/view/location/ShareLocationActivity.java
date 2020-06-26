package com.jio.devicetracker.view.location;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.BaseActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ShareLocationActivity extends BaseActivity implements View.OnClickListener {
    private MapFragment fragmentMap;
    private List<MapData> mapDataList;
    private static StringBuilder strAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        fragmentMap = new MapFragment();
        strAddress = new StringBuilder();
        initUI();
    }

    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SHARE_LOCATION);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        FrameLayout mapFragment = findViewById(R.id.map_fragment);
        ImageView shareLocation = findViewById(R.id.share_location);
        TextView memberName = findViewById(R.id.member_name);
        TextView memberAddrss = findViewById(R.id.member_address);
        shareLocation.setOnClickListener(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_fragment,fragmentMap).commit();
        mapDataList = getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        if(getIntent().getStringExtra(Constant.MEMBER_NAME) != null) {
            memberName.setText(getIntent().getStringExtra(Constant.MEMBER_NAME));
        } else {
            memberName.setText("Test");
        }
        if(!(mapDataList.size() ==0)) {
            memberAddrss.setText(getAddressFromLocation(mapDataList.get(0).getLatitude(), mapDataList.get(0).getLongitude()));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.share_location:
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
                String app_url = "app url i have to enter here";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                break;

            case R.id.back:
                finish();
                break;

        }
    }
    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
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
    }
}

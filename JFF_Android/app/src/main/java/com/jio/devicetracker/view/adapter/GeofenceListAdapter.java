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

package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.util.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeofenceListAdapter extends RecyclerView.Adapter<GeofenceListAdapter.ViewHolder> {
    private List<GeofenceDetails> mData;
    private Context mContext;
    private DBManager mDbManager;
    private String deviceNumber;
    private AdapterListner itemListener;

    /**
     * Constructor to add geofence
     *
     * @param mData
     */
    public GeofenceListAdapter(List mData, Context mContext,String deviceNumber) {
        this.mData = mData;
        this.mContext = mContext;
        mDbManager = new DBManager(mContext);
        this.deviceNumber = deviceNumber;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_geofence_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeofenceDetails details = mData.get(position);
        String address = getAddressFromLocation(mData.get(position).getLat(), mData.get(position).getLng());
        holder.address.setText(address);
        holder.radius.setText(Constant.AREA_COVERED + String.valueOf(mData.get(position).getRadius() / 1000) + " km");
        holder.geofenceName.setText("Geofence " + (position + 1));
        holder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.clickOnEdit(details);
            }
        });


    }

    public interface AdapterListner{
        void clickOnEdit(GeofenceDetails details);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address;
        private TextView radius;
        private TextView geofenceName;
        private RelativeLayout oprationLayout;
        private ImageView close;
        private ImageView menuOption;
        private TextView editText;
        private TextView shareLocation;
        private TextView deleteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_line_1);
            radius = itemView.findViewById(R.id.geofence_radius);
            geofenceName = itemView.findViewById(R.id.geofence_name);
            oprationLayout = itemView.findViewById(R.id.oprationLayout);
            close = itemView.findViewById(R.id.close);
            menuOption = itemView.findViewById(R.id.menu_option);
            editText = itemView.findViewById(R.id.edit);
            deleteText = itemView.findViewById(R.id.delete);
            shareLocation = itemView.findViewById(R.id.share_location);
            editText.setOnClickListener(this);
            deleteText.setOnClickListener(this);
            shareLocation.setOnClickListener(this);
            close.setOnClickListener(this);
            menuOption.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close:
                    oprationLayout.setVisibility(View.GONE);
                    break;
                case R.id.menu_option:
                    oprationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.delete:
                    oprationLayout.setVisibility(View.GONE);
                    GeofenceDetails details = mData.get(getAdapterPosition());
                    mDbManager.deleteGeofenceData(details.getLat(), details.getLng());
                    mData.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;
                case R.id.share_location:
                    oprationLayout.setVisibility(View.GONE);
                    GeofenceDetails detailsShare = mData.get(getAdapterPosition());
                    String geoUri = "http://maps.google.com/maps?q=loc:" + detailsShare.getLat() + "," + detailsShare.getLng();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, geoUri);
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    break;
                default:
                    break;
            }
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
        Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
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
    }
    /**
     * Register the listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(AdapterListner mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

}

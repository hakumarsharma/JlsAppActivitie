package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.util.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeofenceListAdapter extends RecyclerView.Adapter<GeofenceListAdapter.ViewHolder> {
    private List<GeofenceDetails> mData;
    private Context mContext;

    /**
     * Constructor to add geofence
     *
     * @param mData
     */
    public GeofenceListAdapter(List mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;

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
        String address = getAddressFromLocation(mData.get(position).getLat(),mData.get(position).getLng());
        holder.address.setText(address);
        holder.radius.setText(Constant.AREA_COVERED + String.valueOf(mData.get(position).getRadius()/1000)+" km");
        holder.geofenceName.setText("Geofence "+(position+1));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView radius;
        private TextView geofenceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_line_1);
            radius = itemView.findViewById(R.id.geofence_radius);
            geofenceName = itemView.findViewById(R.id.geofence_name);

        }
    }

    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddressFromLocation ( double latitude, double longitude){
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

}

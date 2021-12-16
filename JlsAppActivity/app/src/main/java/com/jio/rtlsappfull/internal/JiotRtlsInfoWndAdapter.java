package com.jio.rtlsappfull.internal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.model.JiotRtlsInfoWindowData;

public class JiotRtlsInfoWndAdapter implements GoogleMap.InfoWindowAdapter {

    private Context m_context;
    View m_rootView;
    ImageView info_pic;
    TextView info_heading;
    TextView info_lat;
    TextView info_lng;
    TextView info_acc;
    TextView info_delta;
    TextView info_tag;

    public JiotRtlsInfoWndAdapter(Context m_context) {
        this.m_context = m_context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        m_rootView = ((Activity) m_context).getLayoutInflater().inflate(R.layout.maps_info, null);
        info_heading = (TextView) m_rootView.findViewById(R.id.info_heading);
        info_pic = (ImageView) m_rootView.findViewById(R.id.info_pic);
        info_lat = (TextView) m_rootView.findViewById(R.id.info_lat);
        info_lng = (TextView) m_rootView.findViewById(R.id.info_lng);
        info_acc = (TextView) m_rootView.findViewById(R.id.info_acc);
        info_delta = (TextView) m_rootView.findViewById(R.id.info_delta);
        info_tag = (TextView) m_rootView.findViewById(R.id.info_tag);

        JiotRtlsInfoWindowData m_rtlsInfoWindow = (JiotRtlsInfoWindowData) marker.getTag();
        /*info_heading.setText(m_rtlsInfoWindow.getM_info_heading());
        info_lat.setText(m_rtlsInfoWindow.getM_info_lat());
        info_lng.setText(m_rtlsInfoWindow.getM_info_lng());
        info_acc.setText(m_rtlsInfoWindow.getM_info_acc());
        info_delta.setText(m_rtlsInfoWindow.getM_info_delta());
        info_tag.setText(m_rtlsInfoWindow.getM_info_tag());
        info_pic.setImageDrawable(m_context.getResources().getDrawable(R.drawable.marker_img));*/

        return m_rootView;
    }
}


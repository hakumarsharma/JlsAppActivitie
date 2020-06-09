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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HelpPagedata;
import com.jio.devicetracker.util.Util;

import java.util.List;

/**
 * Implementation of adapter for help page's content.
 */
public class HelpPageAdapter extends PagerAdapter {
    private List<HelpPagedata> list;
    private LayoutInflater layoutInflater;

    public HelpPageAdapter(Context context, List<HelpPagedata> mList) {
        Context mContext = context;
        list = mList;
        this.layoutInflater=LayoutInflater.from(mContext);

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.activity_help_content, container, false);
        TextView mHeading = view.findViewById(R.id.heading);
        mHeading.setTypeface(Util.mTypeface(container.getContext(),5));
        ImageView mHelpImage = view.findViewById(R.id.helpImage);
        TextView mHelpContent = view.findViewById(R.id.content);
        mHelpContent.setTypeface(Util.mTypeface(container.getContext(),3));
        mHelpImage.setImageResource(list.get(position).getImage());
        mHelpContent.setText(list.get(position).getContent());
        mHeading.setText(list.get(position).getTitle());
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

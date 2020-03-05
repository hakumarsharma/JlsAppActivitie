// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
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

import java.util.List;

/**
 * Implementation of adapter for help page's content.
 */
public class HelpPageAdapter extends PagerAdapter {
    private List<HelpPagedata> list;
    private LayoutInflater layoutInflater;


    public HelpPageAdapter(Context context, List<HelpPagedata> mList)
    {
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
        ImageView mHelpImage = view.findViewById(R.id.helpImage);
        TextView mHelpContent = view.findViewById(R.id.content);
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

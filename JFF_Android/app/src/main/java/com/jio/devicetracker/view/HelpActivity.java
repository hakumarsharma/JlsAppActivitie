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

package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HelpPagedata;
import com.jio.devicetracker.view.adapter.HelpPageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Help screen, It displays login, dashboard and map screenshots along with details that how to use our app.
 */
public class HelpActivity extends Activity implements View.OnClickListener {

    private List<HelpPagedata> mList;
    private LinearLayout mLayout;
    TextView[] dot;
    private ViewPager mPager;
    private int helpImage[] = {R.drawable.helpscreen1, R.drawable.helpscreen1, R.drawable.helpscreen1, R.drawable.helpscreen1,R.drawable.helpscreen5};
    private int helpTitle[] = {R.string.helpScreen1heading, R.string.helpScreen2heading, R.string.helpScreen3heading, R.string.helpScreen4heading,R.string.helpScreen5heading};
    private int helpContent[] = {R.string.helpScreen1desc, R.string.helpScreen2desc, R.string.helpScreen3desc, R.string.helpScreen4desc,R.string.helpScreen4desc};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView mSkip = findViewById(R.id.skip);
        mSkip.setOnClickListener(this);
        mList = new ArrayList<>();
        addDataforHelpscreen();

        mPager = findViewById(R.id.pager);
        mLayout = findViewById(R.id.layout_dot);
        HelpPageAdapter mHelpadapter = new HelpPageAdapter(this, mList);
        mPager.setAdapter(mHelpadapter);
        addDot(0);
        pageChangeListener();
    }

    /**
     * Called when you change the page
     */
    private void pageChangeListener() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // To do
            }

            @Override
            public void onPageSelected(int position) {
                addDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // To do
            }
        });
    }

    /**
     * Adds data in help screen along with image, title and content
     */
    private void addDataforHelpscreen() {
        for (int i = 0; i < helpImage.length; i++) {
            HelpPagedata data = new HelpPagedata();
            data.setImage(helpImage[i]);
            data.setTitle(helpTitle[i]);
            data.setContent(helpContent[i]);
            mList.add(data);
        }
    }

    public void addDot(int pagePosition) {
        dot = new TextView[helpImage.length];
        mLayout.removeAllViews();

        for (int i = 0; i < helpImage.length; i++) {
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml(getResources().getString(R.string.pagerindicater)));
            dot[i].setTextSize(35);
            dot[i].setTextColor(getResources().getColor(R.color.colorAccent));
            mLayout.addView(dot[i]);
        }
        //active dot
        dot[pagePosition].setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.skip) {
            gotoTermandCondition();
        }
    }

    /**
     * Go to Term & Condition page when you click on skip button
     */
    private void gotoTermandCondition() {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }
}

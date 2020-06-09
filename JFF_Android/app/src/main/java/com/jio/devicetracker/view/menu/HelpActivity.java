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

package com.jio.devicetracker.view.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HelpPagedata;
import com.jio.devicetracker.view.adapter.HelpPageAdapter;
import com.jio.devicetracker.view.signinsignup.SigninSignupActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Help screen, It displays login, dashboard and map screenshots along with details that how to use our app.
 */
public class HelpActivity extends Activity implements View.OnClickListener {

    private List<HelpPagedata> mList;
    private LinearLayout mLayout;
    private TextView[] dot;
    private ViewPager mPager;
    private ImageView backImageView;
    private ImageView forwardImageView;
    private int position;
    private int helpImage[] = {R.drawable.helpscreen1, R.drawable.helpscreen2, R.drawable.helpscreen3, R.drawable.helpscreen4, R.drawable.helpscreen5};
    private int helpTitle[] = {R.string.helpScreen1heading, R.string.helpScreen2heading, R.string.helpScreen3heading, R.string.helpScreen4heading, R.string.helpScreen5heading};
    private int helpContent[] = {R.string.helpScreen1desc, R.string.helpScreen2desc, R.string.helpScreen3desc, R.string.helpScreen4desc, R.string.helpScreen5desc};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView mSkip = findViewById(R.id.skip);
        backImageView = findViewById(R.id.helpPageBack);
        forwardImageView = findViewById(R.id.helpPageForward);
        backImageView.setOnClickListener(this);
        forwardImageView.setOnClickListener(this);
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
                HelpActivity.this.position = position;
                if (position == 0) {
                    backImageView.setVisibility(View.INVISIBLE);
                } else if (position == 4) {
                    backImageView.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                    forwardImageView.setVisibility(View.INVISIBLE);
                } else {
                    backImageView.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                    forwardImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                addDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("Hello");
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
            dot[i].setTextSize(12);
            dot[i].setTextColor(getResources().getColor(R.color.colorAccent));
            mLayout.addView(dot[i]);
        }
        //active dot
        dot[pagePosition].setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.skip) {
            gotoSigninSignupActivity();
        } else if (v.getId() == R.id.helpPageBack) {
            onHelpPageBackButtonClick();
        } else if (v.getId() == R.id.helpPageForward) {
            onHelpPageForwardButtonClick();
        }
    }

    private void onHelpPageBackButtonClick() {
        switch (position) {
            case 1:
                mPager.setCurrentItem(0);
                break;
            case 2:
                mPager.setCurrentItem(1);
                break;
            case 3:
                mPager.setCurrentItem(2);
                break;
            case 4:
                mPager.setCurrentItem(3);
                break;
            default:
                mPager.setCurrentItem(1);
                break;
        }
    }

    private void onHelpPageForwardButtonClick() {
        switch (position) {
            case 0:
                mPager.setCurrentItem(1);
                break;
            case 1:
                mPager.setCurrentItem(2);
                break;
            case 2:
                mPager.setCurrentItem(3);
                break;
            case 3:
                mPager.setCurrentItem(4);
                break;
            default:
                mPager.setCurrentItem(0);
                break;
        }
    }

    /**
     * Go to Term & Condition page when you click on skip button
     */
    private void gotoSigninSignupActivity() {
        Intent intent = new Intent(this, SigninSignupActivity.class);
        startActivity(intent);
    }
}

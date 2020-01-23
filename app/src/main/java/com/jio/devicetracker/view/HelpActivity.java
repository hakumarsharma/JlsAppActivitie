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

import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HelpPagedata;
import com.jio.devicetracker.view.adapter.HelpPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends Activity implements View.OnClickListener {

    private ViewPager mPager;
    private TabLayout mTabview;
    private HelpPageAdapter mHelpadapter;
    private List<HelpPagedata> mList;
    private LinearLayout mLayout;
    ArrayList<Integer> arrayList;
    TextView[] dot;
    private TextView mNext;
    private int helpImage [] = {R.drawable.login_screenshot,R.drawable.login_screenshot,R.drawable.login_screenshot,R.drawable.login_screenshot};
    private String helpTitle [] = {"Login","Home","Add","Track"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);
        mList = new ArrayList<>();
        arrayList = new ArrayList<>();

        arrayList.add(R.color.white);
        arrayList.add(R.color.colorPrimaryDark);
        arrayList.add(R.color.colorAccent);
        arrayList.add(R.color.colorAccent);


        addDataforHelpscreen();

        mPager = findViewById(R.id.pager);
        mLayout = findViewById(R.id.layout_dot);
        mHelpadapter = new HelpPageAdapter(this,mList);
        mPager.setAdapter(mHelpadapter);
        addDot(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                addDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addDataforHelpscreen() {
        for(int i = 0;i < 4; i++)
        {
            HelpPagedata data = new HelpPagedata();
            data.setImage(helpImage[i]);
            data.setTitle(helpTitle[i]);
            mList.add(data);
        }

    }

    public void addDot(int page_position) {
        dot = new TextView[arrayList.size()];
        mLayout.removeAllViews();

        for (int i = 0; i < dot.length; i++) {;
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#9673;"));
            dot[i].setTextSize(35);
            dot[i].setTextColor(getResources().getColor(R.color.colorAccent));
            mLayout.addView(dot[i]);
        }
        //active dot
        dot[page_position].setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View v) {
        gotoTermandCondition();

    }

    private void gotoTermandCondition() {
        Intent intent = new Intent(HelpActivity.this,TermAndConditionActivity.class);
        startActivity(intent);
    }
}

package com.jio.devicetracker.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.HowToUseAdapter;

public class HowToUseActivity extends AppCompatActivity implements View.OnClickListener {

   // private TabLayout tabLayout;
    private ViewPager howToUseViewPager;
    private HowToUseAdapter howToUseAdapter;
    private TextView qrCodeTitle;
    private TextView manualTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        setLayoutData();
    }

    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.howtotuseToolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(Util.mTypeface(this, 5));
        toolbarTitle.setText(Constant.HOW_TO_ADD_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        manualTitle = findViewById(R.id.manual_help);
        qrCodeTitle = findViewById(R.id.qr_code_help);
        manualTitle.setTypeface(Util.mTypeface(this,5));
        qrCodeTitle.setTypeface(Util.mTypeface(this,5));
        qrCodeTitle.setText("QR code\n"+ Html.fromHtml(getResources().getString(R.string.white_indicater)));
        qrCodeTitle.setOnClickListener(this);
        manualTitle.setOnClickListener(this);

        howToUseAdapter = new HowToUseAdapter(getSupportFragmentManager(), 2);
        howToUseViewPager = findViewById(R.id.howToUsePager);
        howToUseViewPager.setAdapter(howToUseAdapter);
        pageChangeListener();
    }


    /**
     * Called when you change the page
     */
    private void pageChangeListener() {
        howToUseViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // To do
            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    qrCodeTitle.setText("QR code\n"+ Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    qrCodeTitle.setTextColor(Color.WHITE);
                    manualTitle.setText("Manual");
                    manualTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                } else {

                    manualTitle.setText("Manual\n"+Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    manualTitle.setTextColor(Color.WHITE);
                    qrCodeTitle.setText("QR code");
                    qrCodeTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // To do
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.manual_help:
                howToUseViewPager.setCurrentItem(1);
                manualTitle.setText("Manual\n"+Html.fromHtml(getResources().getString(R.string.white_indicater)));
                manualTitle.setTextColor(Color.WHITE);
                qrCodeTitle.setText("QR code");
                qrCodeTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                break;
            case R.id.qr_code_help:
                howToUseViewPager.setCurrentItem(0);
                qrCodeTitle.setText("QR code\n"+Html.fromHtml(getResources().getString(R.string.white_indicater)));
                qrCodeTitle.setTextColor(Color.WHITE);
                manualTitle.setText("Manual");
                manualTitle.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                break;

            case R.id.back:
                finish();
                break;

            default:
                break;
        }
    }
}

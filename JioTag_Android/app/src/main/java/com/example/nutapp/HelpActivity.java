package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.nutapp.adapter.HelpPageAdapter;
import com.example.nutapp.pojo.HelpPagedata;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private List<HelpPagedata> mList;
    private LinearLayout mLayout;
    TextView[] dot;
    private int helpImage[] = {R.drawable.ic_instruction1, R.drawable.ic_instruction2,R.drawable.ic_instruction3,R.drawable.ic_instruction4};
    private int helpTitle[] = { R.string.location_help_title, R.string.ring_help_title, R.string.community_help_title,R.string.share_help_title};
    private int helpContent[] = { R.string.location_help_content, R.string.ring_help_content, R.string.community_help_content,R.string.share_help_content};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_screen);
        mList = new ArrayList<>();
        addDataforHelpscreen();
        TextView skipText = findViewById(R.id.skip);
        ViewPager mPager = findViewById(R.id.pager);
        mLayout = findViewById(R.id.layout_dot);
        HelpPageAdapter mHelpadapter = new HelpPageAdapter(this, mList);
        mPager.setAdapter(mHelpadapter);
        addDot(0);

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

        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JioUtils.setAutologinStatus(HelpActivity.this,true);
                Intent intent = new Intent(HelpActivity.this,OtpRequest.class);
                startActivity(intent);
            }
        });
    }


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
            ;
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml(getResources().getString(R.string.pagerindicater)));
            dot[i].setTextSize(25);
            dot[i].setTextColor(getResources().getColor(R.color.colorAccent));
            mLayout.addView(dot[i]);
        }
        //active dot
        dot[pagePosition].setTextColor(getResources().getColor(R.color.white));


    }
}

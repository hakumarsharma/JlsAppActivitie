package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class FaqActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.FAQ_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
    }
}

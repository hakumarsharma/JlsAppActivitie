package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class TermAndConditionPolicyActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_condition);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.TERM_CONDITION_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView termPolicy = findViewById(R.id.termConditioncontent);
        termPolicy.setText(Html.fromHtml(getString(R.string.privacy_policy)));
    }
}

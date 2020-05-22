package com.jio.devicetracker.util;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jio.devicetracker.R;

public class CustomAlertActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alert);
        initializeDataMember();
    }

    private void initializeDataMember() {
        TextView title = findViewById(R.id.dialogue_title);
        title.setText(Constant.Custom_Alert_Title);
        title.setTypeface(Util.mTypeface(this,5));
        TextView message = findViewById(R.id.dialogue_message);
        message.setText(Constant.Custom_Alert_Message);
        message.setTypeface(Util.mTypeface(this,3));
        Button laterBtn = findViewById(R.id.btn_later);
        laterBtn.setTypeface(Util.mTypeface(this,5));
        Button viewBtn = findViewById(R.id.btn_view);
        viewBtn.setTypeface(Util.mTypeface(this,5));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_later) {

        } else if (v.getId() == R.id.btn_view){

        }
    }
}

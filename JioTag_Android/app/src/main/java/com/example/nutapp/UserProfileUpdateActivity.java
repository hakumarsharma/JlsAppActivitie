package com.example.nutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class UserProfileUpdateActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_userprofile);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.USER_DETAIL);
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        Button homeBtn = findViewById(R.id.home);
        homeBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.back:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.home:
                Intent intentHome = new Intent(this,MainActivity.class);
                startActivity(intentHome);
                break;
        }

    }

}

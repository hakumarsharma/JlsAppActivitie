package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SkipLogin extends AppCompatActivity {
    boolean m_isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skip_login);

        final CheckBox login_check = (CheckBox) findViewById(R.id.skip_login_check_box);
        login_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_isChecked=isChecked;
            }
        });

        Button skip_login_btn_send_otp = (Button) findViewById(R.id.skip_login_btn_send_otp);
        skip_login_btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(m_isChecked){
                   Intent i = new Intent(v.getContext(), JioAddFinder.class);
                   startActivity(i);
               }else{
                   Intent i = new Intent(v.getContext(), OtpRequest.class);
                   startActivity(i);
               }
            }
        });
    }
}

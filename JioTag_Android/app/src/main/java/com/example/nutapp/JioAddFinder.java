package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JioAddFinder extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_jio_finder);
        TextView addjio_app_header_text= findViewById(R.id.addjio_app_header_text);
        TextView addjiolink= findViewById(R.id.addjiolink);
        Button btn_search_devices= findViewById(R.id.addjio_btn_search_devices);
        btn_search_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),QRReaderInstruction.class);
                startActivity(startMain);
            }
        });
        btn_search_devices.setTypeface(JioUtils.mTypeface(this,5));
        addjio_app_header_text.setTypeface(JioUtils.mTypeface(this,5));
        addjiolink.setTypeface(JioUtils.mTypeface(this,5));

        addjiolink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),JioPermissions.class);
                startActivity(startMain);
            }
        });
    }
}


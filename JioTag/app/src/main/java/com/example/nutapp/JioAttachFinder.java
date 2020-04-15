package com.example.nutapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JioAttachFinder extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attach_jio_finder);

        preferences = this.getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        TextView app_header_text = (TextView) findViewById(R.id.app_header_text);
        TextView attach_jio_finder_Header = (TextView) findViewById(R.id.attach_jio_finder_Header);
        Button btn_search_devices = (Button) findViewById(R.id.btn_search_devices);
        btn_search_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefEditor.putString("FIRSTBOOT", "false");
                Log.d("FIRSTBOOT", "FIRSTBOOT IS DONE");
                prefEditor.commit();
                Intent startMain = new Intent(getApplicationContext(), MainActivity.class);
                //startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startMain.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(startMain);
                finishAffinity();
            }
        });
        btn_search_devices.setTypeface(JioUtils.mTypeface(this, 5));
        app_header_text.setTypeface(JioUtils.mTypeface(this, 5));
        attach_jio_finder_Header.setTypeface(JioUtils.mTypeface(this, 3));
    }
}

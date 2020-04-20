package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class JioFinderNotFound extends AppCompatActivity {

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jio_finder_not_found);
        Button btn_search_devices=(Button)findViewById(R.id.notfound_btn_search_devices);
        btn_search_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startMain);
            }
        });
        TextView notfound_attach_jio_finder_Header=(TextView)findViewById(R.id.notfound_attach_jio_finder_Header);
        notfound_attach_jio_finder_Header.setTypeface(JioUtils.mTypeface(this, 5));

        TextView notfound_attach_jio_finder_steps=(TextView)findViewById(R.id.notfound_attach_jio_finder_steps);
        notfound_attach_jio_finder_steps.setTypeface(JioUtils.mTypeface(this, 5));

        TextView notfound_attach_jio_finder_steps_detail=(TextView)findViewById(R.id.notfound_attach_jio_finder_steps_detail);
        notfound_attach_jio_finder_steps_detail.setTypeface(JioUtils.mTypeface(this, 3));

    }
}

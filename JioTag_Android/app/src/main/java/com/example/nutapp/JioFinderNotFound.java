package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


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
        Button btnSearchDevices=(Button)findViewById(R.id.notfound_btn_search_devices);
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startMain);
            }
        });
        TextView notfoundAttachJioFinderHeader=(TextView)findViewById(R.id.notfound_attach_jio_finder_Header);
        notfoundAttachJioFinderHeader.setTypeface(JioUtils.mTypeface(this, 5));

        TextView notfoundAttachJioFinderSteps=(TextView)findViewById(R.id.notfound_attach_jio_finder_steps);
        notfoundAttachJioFinderSteps.setTypeface(JioUtils.mTypeface(this, 5));

        TextView notfoundAttachJioFinderStepsDetail=(TextView)findViewById(R.id.notfound_attach_jio_finder_steps_detail);
        notfoundAttachJioFinderStepsDetail.setTypeface(JioUtils.mTypeface(this, 3));

    }
}

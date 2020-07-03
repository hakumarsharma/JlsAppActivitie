package com.jio.devicetracker.view.geofence;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Util;

public class EditGeofenceActivity  extends Activity implements View.OnClickListener {

    SeekBar radiusSeekBar;
    String  meterOrKiloMeter;
    private Button metersRadioButton;
    private Button kiloMetersRadioButton;
    private TextView radiusText;
    private static final String TAG = "EditGeofenceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_geofence);

        meterOrKiloMeter = " km";

        radiusText = findViewById(R.id.radiusText);
        radiusText.setTypeface(Util.mTypeface(this,5));

        metersRadioButton = findViewById(R.id.metersButton);
        metersRadioButton.setOnClickListener(this);
        kiloMetersRadioButton = findViewById(R.id.kiloMetersButton);
        kiloMetersRadioButton.setOnClickListener(this);

        radiusSeekBar=(SeekBar)findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusText.setText(progressChangedValue + meterOrKiloMeter );
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.metersButton){
            meterOrKiloMeter = " m";
            metersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
            kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
            radiusSeekBar.setMax(100);
            radiusSeekBar.setProgress(50);
            radiusText.setText("50 m");
        }else if (v.getId() == R.id.kiloMetersButton){
            meterOrKiloMeter = " km";
            metersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
            kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
            radiusSeekBar.setMax(20);
            radiusSeekBar.setProgress(10);
            radiusText.setText("10 km");
        }else if (v.getId() == R.id.updateGeofence){

            Log.d(TAG,"Update function implementation");
        }

    }
}

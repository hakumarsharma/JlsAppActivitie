package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FriendlyName extends AppCompatActivity {
    String dev_address;
    String defVal = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendly_name);

        Intent receiveIntent = this.getIntent();
        dev_address = receiveIntent.getStringExtra("ADDR_DEV");
        Log.d("INTENTADDR",dev_address);

        final EditText filled_name_value = (EditText) findViewById(R.id.filled_name);
        Button friendly_name_key = (Button) findViewById(R.id.button_key);
        friendly_name_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_key));
            }
        });
        Button friendly_name_wallet = (Button) findViewById(R.id.button_wallet);
        friendly_name_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_wallet));
            }
        });
        Button friendly_name_laptop = (Button) findViewById(R.id.button_laptops);
        friendly_name_laptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_laptops));
            }
        });
        Button friendly_name_suitcase = (Button) findViewById(R.id.button_suitcase);
        friendly_name_suitcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_suitcase));
            }
        });
        Button friendly_name_satchel = (Button) findViewById(R.id.button_satchel);
        friendly_name_satchel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_satchel));
            }
        });
        Button friendly_name_others = (Button) findViewById(R.id.button_others);
        friendly_name_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled_name_value.setText(getResources().getString(R.string.friendly_others));
            }
        });

        Button dialog_cancel = (Button) findViewById(R.id.button_cancel);
        dialog_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button dialog_finish = (Button) findViewById(R.id.button_finish);
        dialog_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ADDR_DEV", dev_address);
                intent.putExtra("ADDR_DEV_NAME", filled_name_value.getText().toString());
                Log.d("FRADD",dev_address);
                Log.d("FRNAME",filled_name_value.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

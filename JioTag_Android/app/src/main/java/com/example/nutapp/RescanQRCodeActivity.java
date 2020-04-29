package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.JioConstant;

public class RescanQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescan_qrcode);
        Button rescanBtn = findViewById(R.id.rescan_btn);
        TextView title = findViewById(R.id.toolbar_title);
        Button close = findViewById(R.id.close);
        close.setVisibility(View.VISIBLE);
        title.setText(JioConstant.SCAN_QR_CODE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this, 5));
        TextView rescanText = findViewById(R.id.rescanText);
        rescanText.setTypeface(JioUtils.mTypeface(this, 5));
        rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RescanQRCodeActivity.this,QRCodeScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}

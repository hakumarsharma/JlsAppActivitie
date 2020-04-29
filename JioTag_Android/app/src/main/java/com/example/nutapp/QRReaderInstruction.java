package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.JioConstant;

public class QRReaderInstruction extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan_instruction);
        Button scanBtn = findViewById(R.id.scan_btn);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.SCAN_QR_CODE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this, 5));
        TextView scanHelpText = findViewById(R.id.qrcode_scan_help_title);
        scanHelpText.setTypeface(JioUtils.mTypeface(this,3));
        TextView addManually = findViewById(R.id.manual_add);
        addManually.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.scan_btn:
                gotoQRReaderScreen();
                break;

            case R.id.manual_add:
                gotoJioPermissionScreen();
                break;

            default:
                break;
        }
    }

    private void gotoJioPermissionScreen() {
        Intent startMain=new Intent(getApplicationContext(),JioPermissions.class);
        startActivity(startMain);
    }

    private void gotoQRReaderScreen() {

        Intent startMain=new Intent(getApplicationContext(),QRCodeScannerActivity.class);
        startActivity(startMain);
    }
}

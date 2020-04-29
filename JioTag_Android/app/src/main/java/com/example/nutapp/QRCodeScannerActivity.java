package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.Constant;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView mScannerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        TextView title = findViewById(R.id.toolbar_title);
        TextView scanCodeText =findViewById(R.id.scanText);
        Button closeBtn = findViewById(R.id.close);
        closeBtn.setVisibility(View.VISIBLE);
        title.setText(Constant.SCAN_QR_CODE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this, 5));
        scanCodeText.setTypeface(JioUtils.mTypeface(this, 5));
        RelativeLayout scanView = findViewById(R.id.scanframe);
        mScannerView = new ZXingScannerView(this);
        mScannerView.setBackground(getDrawable(R.drawable.ic_qrcodescan));
        scanView.addView(mScannerView);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCodeScannerActivity.this,RescanQRCodeActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void handleResult(Result result) {

        Intent intent = new Intent(this, JioTagDeviceInfoActivity.class);
        intent.putExtra("QRCodeValue",result.getText());
        startActivity(intent);

       /* Toast.makeText(this,"QR code scanning is done",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,QRReaderInstruction.class);
        startActivity(intent);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}

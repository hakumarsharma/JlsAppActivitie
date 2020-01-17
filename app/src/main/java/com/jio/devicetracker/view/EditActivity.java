package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.EditProfileData;
import com.jio.devicetracker.util.Util;

public class EditActivity extends Activity implements View.OnClickListener {

    private EditText mName, mNumber, mIMEI;
    private String number;
    private DBManager mDBmanager;
    private EditProfileData editData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Edit");
        mName = findViewById(R.id.memberName);
        //mRelation = findViewById(R.id.relation);
        mNumber = findViewById(R.id.deviceName);
        mIMEI = findViewById(R.id.deviceIMEINumber);
        Button update = findViewById(R.id.update);
        mDBmanager = new DBManager(this);
        update.setOnClickListener(this);
        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        if (!RegistrationActivity.isFMSFlow) {
            editData = mDBmanager.getUserdataForEdit(number);
        } else {
            editData = mDBmanager.getUserdataForEditFMS(number);
        }
        //relation = intent.getStringExtra("Relation");
        mName.setText(editData.getName());
        mNumber.setText(editData.getPhoneNumber());
        //mRelation.setText(editData.getRelation());
        mIMEI.setText(editData.getImeiNumber());


    }

    @Override
    public void onClick(View v) {
        String phoneNumber = mDBmanager.getAdminphoneNumber();
        if (!RegistrationActivity.isFMSFlow) {
            mDBmanager.updateProfile(number, mName.getText().toString(), mNumber.getText().toString(), mIMEI.getText().toString());
        } else {
            if (phoneNumber.equals("91" + mNumber.getText().toString())) {
                Util.alertDilogBox("You can't add registered mobile number", "Jio Alert", this);
                return;
            }
            mDBmanager.updateProfileFMS(number, mName.getText().toString(), mNumber.getText().toString(), mIMEI.getText().toString());
            }
            gotoDashboard();
        }

        private void gotoDashboard () {

            Intent intent = new Intent(EditActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

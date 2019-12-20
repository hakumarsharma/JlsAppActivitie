package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.EditProfileData;

public class EditActivity extends Activity implements View.OnClickListener {

    private EditText mName, mRelation, mNumber,mIMEI;
    private Button mUpdate;
    private String number, relation;
    private DBManager mDBmanager;
    private EditProfileData editData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Edit");
        mName = findViewById(R.id.memberName);
        mRelation = findViewById(R.id.relation);
        mNumber = findViewById(R.id.deviceName);
        mIMEI = findViewById(R.id.deviceIMEINumber);
        mUpdate = findViewById(R.id.update);
        mDBmanager = new DBManager(this);
        mUpdate.setOnClickListener(this);
        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        editData = mDBmanager.getUserdataForEdit(number);
        //relation = intent.getStringExtra("Relation");
        mName.setText(editData.getName());
        mNumber.setText(editData.getPhoneNumber());
        mRelation.setText(editData.getRelation());
        mIMEI.setText(editData.getImeiNumber());


    }

    @Override
    public void onClick(View v) {
        mDBmanager.updateProfile(number,mName.getText().toString(),mNumber.getText().toString(),mRelation.getText().toString(),mIMEI.getText().toString());
        gotoDashboard();
    }

    private void gotoDashboard() {

        Intent intent = new Intent(EditActivity.this, DashboardActivity.class);
        startActivity(intent);
    }
}

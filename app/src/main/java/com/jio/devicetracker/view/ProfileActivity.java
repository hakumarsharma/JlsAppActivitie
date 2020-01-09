package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.RegisterData;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static DBManager mDbManager;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhoneNumber;
    private TextView userDateOfBirth;
    private Button editProfileButton;
    private Button saveProfileDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDbManager = new DBManager(this);
        userName = findViewById(R.id.name);
        userEmail = findViewById(R.id.user_Email);
        /*userPhoneNumber = findViewById(R.id.user_phone_number);
        userDateOfBirth = findViewById(R.id.user_dob);*/
       /* editProfileButton = findViewById(R.id.profileEdit);
        saveProfileDetail = findViewById(R.id.saveProfileDetail);
        saveProfileDetail.setEnabled(false);
        editProfileButton.setOnClickListener(this);*/
        getUserAdminDetail();
    }

    private void getUserAdminDetail() {
        AdminLoginData adminLoginData = mDbManager.getAdminLoginDetail();
        userName.setText(adminLoginData.getName());
        userEmail.setText(adminLoginData.getEmail());
        /*userPhoneNumber.setText(adminLoginData.getPhoneNumber());
        userDateOfBirth.setText(adminLoginData.getDob());*/
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.profileEdit:
                //validate();
                break;
            case R.id.saveProfileDetail:
                // SelectDate();
                break;
        }*/
    }
}

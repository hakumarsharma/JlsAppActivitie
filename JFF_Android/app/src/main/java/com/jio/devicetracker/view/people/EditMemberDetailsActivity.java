package com.jio.devicetracker.view.people;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.EditMemberDetailsData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.EditMemberDetailsRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;

public class EditMemberDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private String consentId;
    private EditText userName;
    private DBManager mDbmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_details);
        Intent intent = getIntent();
        userName = findViewById(R.id.editUserName);
        consentId = intent.getStringExtra(Constant.CONSENT_ID);
        userName.setText(intent.getStringExtra(Constant.GROUPNAME));
        Button updateBtn = findViewById(R.id.updateName);
        updateBtn.setOnClickListener(this);
        mDbmanager = new DBManager(this);
        if(!userName.getText().toString().isEmpty()){
            updateBtn.setBackground(getResources().getDrawable(R.drawable.login_selector));
        }
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.equals("")){
                    updateBtn.setBackground(getResources().getDrawable(R.drawable.login_selector));
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        EditMemberDetailsData data = new EditMemberDetailsData();
        data.setName(userName.getText().toString());
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new EditMemberDetailsRequest(new EditdetailSuccessListener(), new EditdetailErrorListener(), data, consentId, mDbmanager.getAdminLoginDetail().getUserId()));
    }

    private class EditdetailSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            Log.d("respone","Checking response value"+response);
            Intent intent = new Intent(EditMemberDetailsActivity.this, DashboardMainActivity.class);
            intent.putExtra(Constant.Add_People,false);
            intent.putExtra(Constant.Add_Device,false);
            startActivity(intent);
        }
    }

    private class EditdetailErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Log.d("respone","Checking response value"+error);
        }
    }
}

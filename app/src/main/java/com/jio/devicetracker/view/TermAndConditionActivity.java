package com.jio.devicetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Term and Condition Screen to show the term and condition of application.
 */
public class TermAndConditionActivity extends Activity implements View.OnClickListener {
    private CheckBox mCheckbox;
    private Button mAccept;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_termcondition);
        mCheckbox = findViewById(R.id.checkbox);
        mAccept = findViewById(R.id.accept);

        mAccept.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        gotoLoginScreen();
    }

    private void gotoLoginScreen() {

        Util.setTermconditionFlag(TermAndConditionActivity.this,true);
        Intent intent = new Intent(TermAndConditionActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}

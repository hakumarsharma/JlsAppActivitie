package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.JioConstant;

public class EmailConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        Button lgnBtn = findViewById(R.id.login_btn);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.EMAIL_VERIFICATION_TITLE);

        lgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),JioAddFinder.class);
                startActivity(startMain);
            }
        });
    }
}

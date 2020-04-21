package com.example.nutapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.Constant;

public class AuthLoginActivity extends AppCompatActivity {

    private EditText email_edittext;
    private EditText pass_edittext;
    private TextView email_title_text;
    private TextView pass_title_text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth_login);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.AUTH_LOGIN_TITLE);
        email_edittext = findViewById(R.id.login_edit_email);
        pass_edittext = findViewById(R.id.login_edit_password);
        email_title_text = findViewById(R.id.login_text_email);
        pass_title_text = findViewById(R.id.login_text_password);

        email_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(email_edittext.getText().toString().isEmpty())
                {
                    email_title_text.setVisibility(View.INVISIBLE);
                } else {
                    email_title_text.setVisibility(View.VISIBLE);
                }
            }
        });

        pass_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pass_edittext.getText().toString().isEmpty())
                {
                    pass_title_text.setVisibility(View.INVISIBLE);
                } else {
                    pass_title_text.setVisibility(View.VISIBLE);
                }
            }
        });



    }
}

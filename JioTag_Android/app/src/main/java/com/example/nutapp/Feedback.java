package com.example.nutapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Feedback extends AppCompatActivity {
    EditText feedback_email;
    EditText feedback_text;
    Button feedback_attach_asset_btn_add;


    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        //emailIntent.setType("text/plain");
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ feedback_email.getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback JioTags");
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback_text.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finish", "EMAIL CHOOSER OPENED");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        feedback_attach_asset_btn_add = (Button) findViewById(R.id.feedback_attach_asset_btn_add);
        feedback_attach_asset_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback_email.getText().toString().isEmpty() || feedback_text.getText().toString().isEmpty()) {
                    Toast.makeText(Feedback.this, "To or Body fields are empty!!!!.", Toast.LENGTH_SHORT).show();
                } else {
                    sendEmail();
                }
            }
        });
        feedback_email = (EditText) findViewById(R.id.feedback_email);
        feedback_text = (EditText) findViewById(R.id.feedback_text);
        feedback_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Feedback","before method");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if(count>0){
                      feedback_attach_asset_btn_add.setEnabled(true);
                      feedback_attach_asset_btn_add.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                  }else{
                      feedback_attach_asset_btn_add.setEnabled(false);
                      feedback_attach_asset_btn_add.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                  }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Feedback","afterTextChanged method");
            }
        });

        ImageButton feedbackAttachBack = (ImageButton) findViewById(R.id.feedback_attach_back);
        feedbackAttachBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton feedbackAttachTick = (ImageButton) findViewById(R.id.feedback_attach_tick);
        feedbackAttachTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });

    }
}

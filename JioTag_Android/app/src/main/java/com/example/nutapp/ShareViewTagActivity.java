package com.example.nutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class ShareViewTagActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_invite);

        Button viewTagBtn = findViewById(R.id.viewtag_btn);
        viewTagBtn.setOnClickListener(this);
        setToolbarIconTitle();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,CardDetails.class);
        intent.putExtra("ShareFlag",true);
        startActivity(intent);
    }
    public void setToolbarIconTitle()
    {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.SHARE_INVITE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backIcn = findViewById(R.id.back);
        backIcn.setVisibility(View.VISIBLE);
        Button homeicn = findViewById(R.id.home);
        homeicn.setVisibility(View.VISIBLE);
    }
}

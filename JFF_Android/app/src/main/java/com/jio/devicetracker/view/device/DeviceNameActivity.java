/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.group.ChooseGroupActivity;

public class DeviceNameActivity extends Activity implements View.OnClickListener {
    private ImageView motherIcon;
    private ImageView fatherIcon;
    private ImageView husbandIcon;
    private ImageView wifeIcon;
    private ImageView kidIcon;
    private ImageView otherIcon;
    private ImageView dogIcon;
    private ImageView catIcon;
    private ImageView otherPetIcon;
    private EditText deviceName;
    private Button done;
    private String deviceNumber;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.DEVICE_NAME_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        done = findViewById(R.id.done);
        done.setVisibility(View.VISIBLE);
        done.setOnClickListener(this);
        done.setAlpha(.5f);
        Intent intent = getIntent();
        deviceNumber = intent.getStringExtra("DeviceNumber");
        title.setTypeface(Util.mTypeface(this,5));
        TextView iconSelectionText = findViewById(R.id.icon_selection);
        iconSelectionText.setTypeface(Util.mTypeface(this,3));
        initiateUI();
    }

    public void initiateUI(){
        motherIcon = findViewById(R.id.mother_icon);
        fatherIcon = findViewById(R.id.father_icon);
        husbandIcon = findViewById(R.id.husband_icon);
        wifeIcon = findViewById(R.id.wife_icon);
        kidIcon = findViewById(R.id.kid_icon);
        otherIcon = findViewById(R.id.other_icon);
        dogIcon = findViewById(R.id.dog_icon);
        catIcon = findViewById(R.id.cat_icon);
        otherPetIcon = findViewById(R.id.other_pet_icon);
        deviceName = findViewById(R.id.device_edit_name);

        motherIcon.setOnClickListener(this);
        fatherIcon.setOnClickListener(this);
        wifeIcon.setOnClickListener(this);
        husbandIcon.setOnClickListener(this);
        kidIcon.setOnClickListener(this);
        otherIcon.setOnClickListener(this);
        dogIcon.setOnClickListener(this);
        catIcon.setOnClickListener(this);
        otherPetIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.mother_icon:
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));

                deviceName.setText("Mom");
                done.setAlpha(1);
                //navigateTochooseGroup("Mother");
                break;
            case R.id.father_icon:
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Father");
                done.setAlpha(1);
                break;
            case R.id.husband_icon:
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Husband");
                done.setAlpha(1);
                break;
            case R.id.wife_icon:
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Wife");
                done.setAlpha(1);
                break;
            case R.id.kid_icon:
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Kid");
                done.setAlpha(1);
                break;
            case R.id.other_icon:
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Other");
                done.setAlpha(1);
                break;
            case R.id.dog_icon:
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Dog");
                done.setAlpha(1);
                break;
            case R.id.cat_icon:
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                deviceName.setText("Cat");
                done.setAlpha(1);
                break;
            case R.id.other_pet_icon:
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.groupselected));
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                deviceName.setText("Other Pet");
                done.setAlpha(1);
                break;

            case R.id.done:
                navigateTochooseGroup(deviceName.getText().toString());
                break;
            default:
                break;

        }
       //navigateTochooseGroup();
    }

    private void navigateTochooseGroup(String memberLabel) {
        Intent intent = new Intent(this, ChooseGroupActivity.class);
        intent.putExtra("Title",memberLabel);
        intent.putExtra("Number",deviceNumber);
        startActivity(intent);
    }

}

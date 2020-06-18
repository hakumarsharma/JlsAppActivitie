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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.group.ChooseGroupActivity;

public class DeviceNameActivity extends BaseActivity implements View.OnClickListener {

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
    private String groupId;
    private String iconName;


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
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        deviceNumber = intent.getStringExtra(Constant.DEVICE_NUMBER);
        title.setTypeface(Util.mTypeface(this, 5));
        TextView iconSelectionText = findViewById(R.id.icon_selection);
        iconSelectionText.setTypeface(Util.mTypeface(this, 3));
        initiateUI();
    }

    public void initiateUI() {
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
        String name = deviceName.getText().toString().trim();
        switch (v.getId()) {
            case R.id.mother_icon:
                iconName = "Mother";
                if (motherIcon.getTag() != null && motherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.MOM)) {
                    motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                    motherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    motherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.MOM);
                }
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.father_icon:
                iconName = "Father";
                if (fatherIcon.getTag() != null && fatherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.FATHER)) {
                    fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                    fatherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    fatherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.FATHER);
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.husband_icon:
                iconName = "Husband";
                if (husbandIcon.getTag() != null && husbandIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.HUSBAND)) {
                    husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                    husbandIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    husbandIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.HUSBAND);
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.wife_icon:
                iconName = "Wife";
                if (wifeIcon.getTag() != null && wifeIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.WIFE)) {
                    wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                    wifeIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    wifeIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.WIFE);
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.kid_icon:
                iconName = "Kid";
                if (kidIcon.getTag() != null && kidIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.KID)) {
                    kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                    kidIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    kidIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.KID);
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.other_icon:
                iconName = "Other";
                if (otherIcon.getTag() != null && otherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.OTHER)) {
                    otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                    otherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    otherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.OTHER);
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.dog_icon:
                iconName = "Dog";
                if (dogIcon.getTag() != null && dogIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.DOG)) {
                    dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                    dogIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    dogIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.DOG);
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.cat_icon:
                iconName = "Cat";
                if (catIcon.getTag() != null && catIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.CAT)) {
                    catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                    catIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    catIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.CAT);
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.other_pet_icon:
                iconName = "OtherPet";
                if (otherPetIcon.getTag() != null && otherPetIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.OTHER_PET)) {
                    otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                    otherPetIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                } else {
                    otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    otherPetIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.OTHER_PET);
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                done.setAlpha(1);
                break;

            case R.id.done:
                if (deviceName.getText().length() == 0) {
                    deviceName.setError(Constant.ENTER_DEVICE_NAME);
                    return;
                } else {
                    gotoChooseGroupActivity(iconName);
                   /* Intent intent = new Intent(DeviceNameActivity.this, ChooseGroupActivity.class);
                    intent.putExtra(Constant.GROUP_ID, createdGroupId);
                    startActivity(intent);*/
                }
                /*if (groupId == null || groupId.isEmpty()) {
                    createGroupAndAddContactDetails();
                } else {
                    addMemberToCreatedGroup();
                }*/
                break;
            default:
                break;

        }
    }

    private void createGroupAndAddContactDetails() {
        this.memberName = deviceName.getText().toString();
        this.memberNumber = deviceNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_DEVICE_GROUP_NAME);
    }

    private void addMemberToCreatedGroup() {
        this.createdGroupId = groupId;
        this.memberName = deviceName.getText().toString();
        this.memberNumber = deviceNumber;
        this.isFromCreateGroup = true;
        this.isGroupMember = false;
        this.isFromDevice = true;
        addMemberInGroupAPICall();
    }

    public void gotoChooseGroupActivity(String iconName)
    {
        Intent intent = new Intent(DeviceNameActivity.this, ChooseGroupActivity.class);
        intent.putExtra(Constant.GROUP_ID, createdGroupId);
        intent.putExtra("DeviceNumber",deviceNumber);
        intent.putExtra("Title",iconName);
        startActivity(intent);
    }
}

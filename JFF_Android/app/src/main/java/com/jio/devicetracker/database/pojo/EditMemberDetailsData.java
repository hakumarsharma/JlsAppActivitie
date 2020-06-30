package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

public class EditMemberDetailsData {
    @SerializedName("name")
    private String name;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}

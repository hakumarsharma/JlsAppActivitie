// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Pojo implementation for search device api .
 */
public class SearchDevice {

    @SerializedName("usersAssigned")
    private List<String> usersAssigned;

    public List<String> getUsersAssigned() {
        return usersAssigned;
    }

    public void setUsersAssigned(List<String> usersAssigned) {
        this.usersAssigned = usersAssigned;
    }
}

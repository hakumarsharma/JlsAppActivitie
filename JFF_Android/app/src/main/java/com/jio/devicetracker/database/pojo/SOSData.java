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

package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SOSData implements Serializable {
    @SerializedName("desired")
    private Desired desired;

    public Desired getDesired() {
        return desired;
    }

    public void setDesired(Desired desired) {
        this.desired = desired;
    }

    public class Desired implements Serializable {
        @SerializedName("phonebook")
        private Phonebook phonebook;

        public Phonebook getPhonebook() {
            return phonebook;
        }

        public void setPhonebook(Phonebook phonebook) {
            this.phonebook = phonebook;
        }

        public class Phonebook implements Serializable {
            @SerializedName("name")
            private String name;
            @SerializedName("type")
            private String type;
            @SerializedName("priority")
            private int priority;
            @SerializedName("number")
            private String number;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getPriority() {
                return priority;
            }

            public void setPriority(int priority) {
                this.priority = priority;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }
        }

    }

}

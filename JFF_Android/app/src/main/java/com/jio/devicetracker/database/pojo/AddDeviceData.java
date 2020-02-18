// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo;

import java.io.Serializable;
import java.util.List;

public class AddDeviceData implements Serializable {
    private List<Devices> devices;
    private Flags flags;

    public List<Devices> getDevices() {
        return devices;
    }

    public void setDevices(List<Devices> devices) {
        this.devices = devices;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public class Devices implements Serializable{
        private String mac;
        private String identifier;
        private String name;
        private String phone;
        private String type;
        private String model;
        private String height;
        private String weight;
        private String age;
        private String gender;
        private Metaprofile metaprofile;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Metaprofile getMetaprofile() {
            return metaprofile;
        }

        public void setMetaprofile(Metaprofile metaprofile) {
            this.metaprofile = metaprofile;
        }

        public class Metaprofile implements Serializable {
            private String First;
            private String second;

            public String getFirst() {
                return First;
            }

            public void setFirst(String first) {
                First = first;
            }

            public String getSecond() {
                return second;
            }

            public void setSecond(String second) {
                this.second = second;
            }
        }
    }

    public class Flags implements Serializable{
        private boolean isSkipAddDeviceToGroup;

        public boolean isSkipAddDeviceToGroup() {
            return isSkipAddDeviceToGroup;
        }

        public void setSkipAddDeviceToGroup(boolean skipAddDeviceToGroup) {
            isSkipAddDeviceToGroup = skipAddDeviceToGroup;
        }
    }
}

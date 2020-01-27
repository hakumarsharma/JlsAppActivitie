// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FMSAPIData {
    @SerializedName("evt")
    private String[] evt;
    @SerializedName("dvt")
    private String dvt;
    @SerializedName("imi")
    private List<String> imi;
    @SerializedName("efd")
    private long efd;
    @SerializedName("mob")
    private List<String> mob;
    @SerializedName("tid")
    private String tid;

    public List<String> getImi() {
        return imi;
    }

    public void setImi(List<String> imi) {
        this.imi = imi;
    }

    public List<String> getMob() {
        return mob;
    }

    public void setMob(List<String> mob) {
        this.mob = mob;
    }

    public String[] getEvt() {
        return evt;
    }

    public void setEvt(String[] evt) {
        this.evt = evt;
    }

    public String getDvt() {
        return dvt;
    }

    public void setDvt(String dvt) {
        this.dvt = dvt;
    }

    public long getEfd() {
        return efd;
    }

    public void setEfd(long efd) {
        this.efd = efd;
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}

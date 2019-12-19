package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

public class FMSAPIData {
    @SerializedName("evt")
    private String[] evt;
    @SerializedName("dvt")
    private String dvt;
    @SerializedName("imi")
    private String[] imi;
    @SerializedName("efd")
    private long efd;
    @SerializedName("mob")
    private String[] mob;
    @SerializedName("tid")
    private String tid;

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

    public String[] getImi() {
        return imi;
    }

    public void setImi(String[] imi) {
        this.imi = imi;
    }

    public long getEfd() {
        return efd;
    }

    public void setEfd(long efd) {
        this.efd = efd;
    }

    public String[] getMob() {
        return mob;
    }

    public void setMob(String[] mob) {
        this.mob = mob;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}

package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

public class FMSHeader {
    @SerializedName("reqId")
    private String reqId;
    @SerializedName("trkId")
    private String trkId;
    @SerializedName("crmId")
    private String crmId;
    @SerializedName("sesId")
    private String[] sesId;
    @SerializedName("sesTyp")
    private int sesTyp;
    @SerializedName("svc")
    private String svc;
    @SerializedName("mode")
    private int mode;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getTrkId() {
        return trkId;
    }

    public void setTrkId(String trkId) {
        this.trkId = trkId;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String[] getSesId() {
        return sesId;
    }

    public void setSesId(String[] sesId) {
        this.sesId = sesId;
    }

    public int getSesTyp() {
        return sesTyp;
    }

    public void setSesTyp(int sesTyp) {
        this.sesTyp = sesTyp;
    }

    public String getSvc() {
        return svc;
    }

    public void setSvc(String svc) {
        this.svc = svc;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

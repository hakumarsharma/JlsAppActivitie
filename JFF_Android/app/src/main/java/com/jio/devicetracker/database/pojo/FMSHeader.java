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

package com.jio.rtlsappfull.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubmitAPIData implements Serializable {

    @SerializedName("ltecells")
    public List<LteCells> ltecells;

    @SerializedName("gpsloc")
    public GpsLoc gpsloc;

    public List<LteCells> getLtecells() {
        return ltecells;
    }

    public void setLtecells(List<LteCells> ltecells) {
        this.ltecells = ltecells;
    }

    public GpsLoc getGpsloc() {
        return gpsloc;
    }

    public void setGpsloc(GpsLoc gpsloc) {
        this.gpsloc = gpsloc;
    }

    public class LteCells {
        private int mcc;
        private int mnc;
        private int tac;
        private int cellid;
        private int rssi;
        private int frequency;

        public int getMcc() {
            return mcc;
        }

        public void setMcc(int mcc) {
            this.mcc = mcc;
        }

        public int getMnc() {
            return mnc;
        }

        public void setMnc(int mnc) {
            this.mnc = mnc;
        }

        public int getTac() {
            return tac;
        }

        public void setTac(int tac) {
            this.tac = tac;
        }

        public int getCellid() {
            return cellid;
        }

        public void setCellid(int cellid) {
            this.cellid = cellid;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }

    public class GpsLoc {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

}

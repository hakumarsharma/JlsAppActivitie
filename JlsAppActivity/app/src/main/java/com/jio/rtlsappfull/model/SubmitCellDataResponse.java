package com.jio.rtlsappfull.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubmitCellDataResponse implements Serializable {


    @SerializedName("ltecells")
    private List<SubmitApiDataResponse.LteCellsInfo> ltecells;

    public List<SubmitApiDataResponse.LteCellsInfo> getLtecells() {
        return ltecells;
    }

    public void setLtecells(List<SubmitApiDataResponse.LteCellsInfo> ltecells) {
        this.ltecells = ltecells;
    }

    public class LteCellsInfo {
        @SerializedName("mcc_mnc_cellid_tac")
        private String mncMncCellIdTac;

        @SerializedName("message")
        private SubmitApiDataResponse.LteCellsInfo.CellInfoMessage message;

        public String getMncMncCellIdTac() {
            return mncMncCellIdTac;
        }

        public void setMncMncCellIdTac(String mncMncCellIdTac) {
            this.mncMncCellIdTac = mncMncCellIdTac;
        }

        public SubmitApiDataResponse.LteCellsInfo.CellInfoMessage getMessage() {
            return message;
        }

        public void setMessage(SubmitApiDataResponse.LteCellsInfo.CellInfoMessage message) {
            this.message = message;
        }

        public class CellInfoMessage {
            @SerializedName("details")
            private SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details details;

            public SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details getDetails() {
                return details;
            }

            public void setDetails(SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details details) {
                this.details = details;
            }

            public class Details {
                @SerializedName("success")
                private SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details.Success success;

                public SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details.Success getSuccess() {
                    return success;
                }

                public void setSuccess(SubmitApiDataResponse.LteCellsInfo.CellInfoMessage.Details.Success success) {
                    this.success = success;
                }

                public class Success {

                    @SerializedName("code")
                    private int code;
                    @SerializedName("message")
                    private String message;

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public String getMessage() {
                        return message;
                    }

                    public void setMessage(String message) {
                        this.message = message;
                    }
                }
            }
        }

    }

}

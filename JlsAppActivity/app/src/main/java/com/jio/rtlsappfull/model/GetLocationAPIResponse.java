package com.jio.rtlsappfull.model;

import com.google.gson.annotations.SerializedName;

public class GetLocationAPIResponse {

    @SerializedName("error")
    private SubmitApiError error;

    public SubmitApiError getError() {
        return error;
    }

    public void setError(SubmitApiError error) {
        this.error = error;
    }

    public class SubmitApiError {

        private int code;
        private String message;
        private String time;
        private String serviceName;
        private String errorCode;

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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}

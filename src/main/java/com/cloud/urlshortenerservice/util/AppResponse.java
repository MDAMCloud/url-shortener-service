package com.cloud.urlshortenerservice.util;

public class AppResponse<T> {

    private Boolean successful;

    private String errorCode;

    private String errorReason;

    private T data;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppResponse{" +
                "successful=" + successful +
                ", errorCode='" + errorCode + '\'' +
                ", errorReason='" + errorReason + '\'' +
                ", data=" + data +
                '}';
    }
}
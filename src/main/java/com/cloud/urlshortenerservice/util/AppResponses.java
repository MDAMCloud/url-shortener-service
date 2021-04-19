package com.cloud.urlshortenerservice.util;

public final class AppResponses {

    public static AppResponse successful() {
        return from(null);
    }

    public static <T> AppResponse<T> from(T data) {
        AppResponse<T> appResponse = new AppResponse<T>();
        appResponse.setSuccessful(true);
        appResponse.setData(data);
        return  appResponse;
    }

    public static <T> AppResponse<T> failure(String errorCode, String errorReason) {
        AppResponse<T> appResponse = new AppResponse<T>();
        appResponse.setSuccessful(false);
        appResponse.setErrorCode(errorCode);
        appResponse.setErrorReason(errorReason);
        return appResponse;
    }

    public static <T> AppResponse<T> failure(String errorReason) {
        AppResponse<T> appResponse = new AppResponse<T>();
        appResponse.setSuccessful(false);
        appResponse.setErrorReason(errorReason);
        return appResponse;
    }

}

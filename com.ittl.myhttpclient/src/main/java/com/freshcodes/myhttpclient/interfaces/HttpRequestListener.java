package com.freshcodes.myhttpclient.interfaces;

public interface HttpRequestListener {
    void onRequestStarted(String WSType);

    void onSuccess(String json, String WSType);

    void onError(int errorCode);
}

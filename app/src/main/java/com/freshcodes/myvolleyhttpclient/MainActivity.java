package com.freshcodes.myvolleyhttpclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.freshcodes.myhttpclient.HttpRequest;
import com.freshcodes.myhttpclient.interfaces.HttpRequestListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        callRequest();
    }

    private void init() {
    }

    private void callRequest() {
        JSONObject jsonObject = new JSONObject();
        HttpRequest httpRequest = new HttpRequest(MainActivity.this, HttpRequest.POST, jsonObject.toString(), "APP_CONFIG", new HttpRequestListener() {
            @Override
            public void onRequestStarted(String WSType) {

            }

            @Override
            public void onSuccess(String json, String WSType) {
                Log.e("Response", json);
            }

            @Override
            public void onError(int errorCode) {
                Log.e("Error", "" + errorCode);
            }
        });
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        httpRequest.addHeaders(headers);
        httpRequest.setRequestQueue(MyApplication.getInstance().getRequestQueue());
        httpRequest.execute("https://www.homebethe.com/ios_webservices/appconfig.php");
    }
}
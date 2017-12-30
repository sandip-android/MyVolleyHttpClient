package com.freshcodes.myhttpclient;

import android.content.Context;
import android.webkit.URLUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.freshcodes.myhttpclient.interfaces.HttpRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HttpRequest {
    Context context;
    int method;
    String reqBody;
    String WSName;
    HttpRequestListener httpRequestListener;

    Map<String, String> headers;

    RequestQueue requestQueue;

    public static int GET = 1;
    public static int POST = 2;

    public static int INVALID_URL_ERROR = 101;
    public static int NETWORK_ERROR = 102;
    public static int SERVER_ERROR = 103;
    public static int AUTH_FAILURE_ERROR = 103;
    public static int PARSE_ERROR = 104;
    public static int NO_CONNECTION_ERROR = 105;
    public static int TIMEOUT_ERROR = 106;
    public static int INVALID_RESPONSE_ERROR = 107;
    public static int UNHANDLED_ERROR = 108;

    public HttpRequest(Context context, int method, String WSName, HttpRequestListener httpRequestListener) {
        this.context = context;
        this.method = method;
        this.WSName = WSName;
        this.httpRequestListener = httpRequestListener;
    }

    public HttpRequest(Context context, int method, String reqBody, String WSName, HttpRequestListener httpRequestListener) {
        this.context = context;
        this.method = method;
        this.reqBody = reqBody;
        this.WSName = WSName;
        this.httpRequestListener = httpRequestListener;
    }

    public void execute(String url) {
        httpRequestListener.onRequestStarted(WSName);
        try {
            if (URLUtil.isValidUrl(url)) {
                int reqMethod = Request.Method.GET;
                if (method == GET) {
                    reqMethod = Request.Method.GET;
                } else if (method == POST) {
                    reqMethod = Request.Method.POST;
                }

                StringRequest jsonReq = new StringRequest(reqMethod, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (context != null && isJSONValid(response)) {
                                httpRequestListener.onSuccess(response, WSName);
                            }else {
                                httpRequestListener.onError(INVALID_RESPONSE_ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            httpRequestListener.onError(INVALID_RESPONSE_ERROR);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            httpRequestListener.onError(NETWORK_ERROR);
                        } else if (error instanceof ServerError) {
                            httpRequestListener.onError(SERVER_ERROR);
                        } else if (error instanceof AuthFailureError) {
                            httpRequestListener.onError(AUTH_FAILURE_ERROR);
                        } else if (error instanceof ParseError) {
                            httpRequestListener.onError(PARSE_ERROR);
                        } else if (error instanceof NoConnectionError) {
                            httpRequestListener.onError(NO_CONNECTION_ERROR);
                        } else if (error instanceof TimeoutError) {
                            httpRequestListener.onError(TIMEOUT_ERROR);
                        } else {
                            httpRequestListener.onError(UNHANDLED_ERROR);
                        }
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        if (reqBody != null && isJSONValid(reqBody)) {
                            return reqBody.getBytes();
                        } else {
                            return super.getBody();
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        if (headers != null) {
                            return headers;
                        } else {
                            return super.getHeaders();
                        }
                    }
                };

                addToRequestQueue(jsonReq, WSName);
            } else {
                httpRequestListener.onError(INVALID_URL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpRequestListener.onError(UNHANDLED_ERROR);
        }
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req, String WSName) {
        req.setTag(WSName);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(String WSName) {
        if (requestQueue != null) {
            requestQueue.cancelAll(WSName);
        }
    }

    public static boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
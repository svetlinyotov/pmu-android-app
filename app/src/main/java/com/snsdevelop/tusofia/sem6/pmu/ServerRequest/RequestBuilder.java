package com.snsdevelop.tusofia.sem6.pmu.ServerRequest;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import com.snsdevelop.tusofia.sem6.pmu.Helpers.Auth;

public class RequestBuilder {
    private final String TAG;
    private final Map<String, String> params = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final int method;
    private final String url;
    private Response.Listener<String> responseListener = null;
    private Response.ErrorListener errorListener = null;

    public RequestBuilder(int method, String url) {
        this.method = method;
        this.url = url;
        this.TAG = "SelFitRequestTAG_" + method + "_" + url;
    }

    public RequestBuilder(int method, String url, int id) {
        this(method, url.replace("{id}", String.valueOf(id)));
    }


    public RequestBuilder setResponseListener(Response.Listener<String> responseListener) {
        this.responseListener = responseListener;
        return this;
    }

    public RequestBuilder setErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public RequestBuilder addParams(Map<String, String> params) {
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getValue() != null) {
                addParam(param.getKey(), param.getValue());
            }
        }
        return this;
    }

    public RequestBuilder addParam(String key, @NonNull String value) {
        this.params.put(key, value);
        return this;
    }

    public RequestBuilder addHeaders(Map<String, String> params) {
        this.headers.putAll(params);
        return this;
    }

    public RequestBuilder addHeader(String key, String value) {
        if (value != null)
            this.headers.put(key, value);

        return this;
    }

    public StringRequest build() {
        return build(null);
    }

    public StringRequest build(Context context) {
        if (responseListener == null) {
            responseListener = response -> {
            };
        }
        if (errorListener == null) {
            errorListener = error -> {
            };
        }

        addHeader("Authorization", "Bearer " + Auth.getAccessToken(context));
        addHeader("AuthOrigin", Auth.getOrigin(context));
        if (context != null)
            addHeader("UserId", Auth.getUserId(context));

        StringRequest stringRequest = new StringRequest(method, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        stringRequest.setTag(TAG);

        return stringRequest;
    }
}

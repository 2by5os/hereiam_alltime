package com.geekwims.hereiam.api;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ApiRequest {
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken = null;
    private final String TAG = ApiRequest.class.getName();

    private enum RequestMethod {
        GET, POST, PUT, DELETE
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private ApiResponse sendRequest(String url, String json, RequestMethod requestMethod) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (accessToken != null) {
            requestBuilder = requestBuilder.header("Authorization", accessToken);
        }

        Request request = null;

        switch (requestMethod) {
            case GET:
                request = requestBuilder.get().build();
                break;
            case POST:
                request = requestBuilder.post(body).build();
                break;
            case PUT:
                request = requestBuilder.put(body).build();
                break;
            case DELETE:
                request = requestBuilder.delete().build();
                break;
        }


        Response response = client.newCall(request).execute();

        Log.e(TAG, response.body().toString());

        return new ApiResponse(response.code(), response.body().string());
    }

    public ApiResponse get(String url) throws IOException {
        return sendRequest(url, "", RequestMethod.GET);
    }

    public ApiResponse post(String url, String json) throws IOException {
        return sendRequest(url, json, RequestMethod.POST);
    }
    public ApiResponse put(String url, String json) throws IOException {
        return sendRequest(url, json, RequestMethod.PUT);
    }
    public ApiResponse delete(String url, String json) throws IOException {
        return sendRequest(url, json, RequestMethod.DELETE);
    }

    public ApiResponse post(String url, Object json) throws IOException {
        return sendRequest(url, objectMapper.writeValueAsString(json), RequestMethod.POST);
    }
    public ApiResponse put(String url, Object json) throws IOException {
        return sendRequest(url, objectMapper.writeValueAsString(json), RequestMethod.PUT);
    }
    public ApiResponse delete(String url, Object json) throws IOException {
        return sendRequest(url, objectMapper.writeValueAsString(json), RequestMethod.DELETE);
    }
}

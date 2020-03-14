package com.geekwims.hereiam.api;


public class ApiResponse {
    private int status = 0;
    private String response;

    public ApiResponse(int status, String response) {
        this.status = status;
        this.response = response;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", response='" + response + '\'' +
                '}';
    }
}

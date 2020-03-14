package com.geekwims.hereiam.response;

import java.sql.Timestamp;


public class AuthResponse {
    private String token;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private String username;

    public AuthResponse() {
    }

    public AuthResponse(String token, Timestamp createdAt, Timestamp expiredAt, String username) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
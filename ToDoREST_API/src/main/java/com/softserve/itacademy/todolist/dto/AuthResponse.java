package com.softserve.itacademy.todolist.dto;

public class AuthResponse {
    private String username;
    private String accessToken;

    public AuthResponse(String username, String jwtToken) {
        this.username = username;
        this.accessToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

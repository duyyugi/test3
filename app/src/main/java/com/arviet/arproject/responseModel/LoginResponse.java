package com.arviet.arproject.responseModel;

public class LoginResponse {
    private String token;
    private String status;
    private String username;

    public LoginResponse(String token, String status, String username) {
        this.token = token;
        this.status = status;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

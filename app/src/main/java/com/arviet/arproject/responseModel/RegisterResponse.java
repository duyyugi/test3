package com.arviet.arproject.responseModel;

public class RegisterResponse {
    private String status;

    public RegisterResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

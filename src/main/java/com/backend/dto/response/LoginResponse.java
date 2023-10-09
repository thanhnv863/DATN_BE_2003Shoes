package com.backend.dto.response;

public class LoginResponse {
    private int statusCode;
    private String message;

    public LoginResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}

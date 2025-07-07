package com.crystal.store.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String timestamp;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.timestamp = new Date().toString();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
        this.statusCode = status.value();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.boha.skunk.util;

public class ErrorMessage {
    int statusCode;
    String date;
    String message;

    public ErrorMessage(int statusCode, String date, String message) {
        this.statusCode = statusCode;
        this.date = date;
        this.message = message;
    }
    public String toString() {
        return "statusCode: " + statusCode + ", date: " + date + ", message: " + message;
    }
}

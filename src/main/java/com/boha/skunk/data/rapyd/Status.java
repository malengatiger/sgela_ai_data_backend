package com.boha.skunk.data.rapyd;

import java.util.UUID;

public class Status {
    private String errorCode;
    private String status;
    private String message;
    private String responseCode;
    private UUID operationID;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String value) {
        this.responseCode = value;
    }

    public UUID getOperationID() {
        return operationID;
    }

    public void setOperationID(UUID value) {
        this.operationID = value;
    }
}

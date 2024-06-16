package com.boha.skunk.data;

public class UploadResponse {
    public String downloadUrl;
    public String gsUri;

    public UploadResponse(String downloadUrl, String gsUri) {
        this.downloadUrl = downloadUrl;
        this.gsUri = gsUri;
    }
}
package com.boha.skunk.data;

public class UrlData {

    private String url;
    private String text;

    public UrlData(String url, String text) {
        this.url = url;
        this.text = text;
    }

    // Getters and setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

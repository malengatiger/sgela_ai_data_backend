package com.boha.skunk.data;

public class ParseResult {
    private boolean header;
    private String text;
    private boolean blankLine;

    public ParseResult(boolean header, String text, boolean blankLine) {
        this.header = header;
        this.text = text;
        this.blankLine = blankLine;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isBlankLine() {
        return blankLine;
    }

    public void setBlankLine(boolean blankLine) {
        this.blankLine = blankLine;
    }

    public boolean isHeader() {
        return header;
    }

    public String getText() {
        return text;
    }
}

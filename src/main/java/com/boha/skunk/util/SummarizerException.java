package com.boha.skunk.util;

public class SummarizerException extends Exception {
    public SummarizerException(ErrorMessage errorMessage) {
        super(errorMessage.toString());
    }
}


package com.boha.skunk.data.rapyd;

import java.io.IOException;

public enum Type {
    BOOLEAN, CUSTOMER, STRING;

    public String toValue() {
        switch (this) {
            case BOOLEAN:
                return "boolean";
            case CUSTOMER:
                return "customer";
            case STRING:
                return "string";
        }
        return null;
    }

    public static Type forValue(String value) throws IOException {
        if (value.equals("boolean")) return BOOLEAN;
        if (value.equals("customer")) return CUSTOMER;
        if (value.equals("string")) return STRING;
        throw new IOException("Cannot deserialize Type");
    }
}

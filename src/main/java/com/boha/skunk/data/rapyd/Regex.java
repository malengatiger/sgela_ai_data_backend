package com.boha.skunk.data.rapyd;

import java.io.IOException;

public enum Regex {
    CUSTOMER_PRESENT_RECURRING_INSTALLMENT_UNSCHEDULED, EMPTY, THE_113;

    public String toValue() {
        switch (this) {
            case CUSTOMER_PRESENT_RECURRING_INSTALLMENT_UNSCHEDULED:
                return "(customer_present|recurring|installment|unscheduled)";
            case EMPTY:
                return "";
            case THE_113:
                return "^.{1,13}$";
        }
        return null;
    }

    public static Regex forValue(String value) throws IOException {
        if (value.equals("(customer_present|recurring|installment|unscheduled)"))
            return CUSTOMER_PRESENT_RECURRING_INSTALLMENT_UNSCHEDULED;
        if (value.equals("")) return EMPTY;
        if (value.equals("^.{1,13}$")) return THE_113;
        throw new IOException("Cannot deserialize Regex");
    }
}

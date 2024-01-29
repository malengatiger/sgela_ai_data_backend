package com.boha.skunk.data.rapyd;

public class AmountRangePerCurrency {
    private String currency;
    private Object maximumAmount;
    private Object minimumAmount;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String value) {
        this.currency = value;
    }

    public Object getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(Object value) {
        this.maximumAmount = value;
    }

    public Object getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Object value) {
        this.minimumAmount = value;
    }
}

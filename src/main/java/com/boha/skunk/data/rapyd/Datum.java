package com.boha.skunk.data.rapyd;

public class Datum {
    private String type;
    private String name;
    private String category;
    private String image;
    private String country;
    private String paymentFlowType;
    private String[] currencies;
    private long status;
    private boolean isCancelable;
    private PaymentOption[] paymentOptions;
    private boolean isExpirable;
    private boolean isOnline;
    private boolean isRefundable;
    private long minimumExpirationSeconds;
    private long maximumExpirationSeconds;
    private Object virtualPaymentMethodType;
    private boolean isVirtual;
    private boolean multipleOverageAllowed;
    private AmountRangePerCurrency[] amountRangePerCurrency;
    private boolean isTokenizable;
    private Object[] supportedDigitalWalletProviders;
    private boolean isRestricted;
    private boolean supportsSubscription;

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String value) {
        this.category = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        this.image = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public String getPaymentFlowType() {
        return paymentFlowType;
    }

    public void setPaymentFlowType(String value) {
        this.paymentFlowType = value;
    }

    public String[] getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String[] value) {
        this.currencies = value;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long value) {
        this.status = value;
    }

    public boolean getIsCancelable() {
        return isCancelable;
    }

    public void setIsCancelable(boolean value) {
        this.isCancelable = value;
    }

    public PaymentOption[] getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(PaymentOption[] value) {
        this.paymentOptions = value;
    }

    public boolean getIsExpirable() {
        return isExpirable;
    }

    public void setIsExpirable(boolean value) {
        this.isExpirable = value;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean value) {
        this.isOnline = value;
    }

    public boolean getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(boolean value) {
        this.isRefundable = value;
    }

    public long getMinimumExpirationSeconds() {
        return minimumExpirationSeconds;
    }

    public void setMinimumExpirationSeconds(long value) {
        this.minimumExpirationSeconds = value;
    }

    public long getMaximumExpirationSeconds() {
        return maximumExpirationSeconds;
    }

    public void setMaximumExpirationSeconds(long value) {
        this.maximumExpirationSeconds = value;
    }

    public Object getVirtualPaymentMethodType() {
        return virtualPaymentMethodType;
    }

    public void setVirtualPaymentMethodType(Object value) {
        this.virtualPaymentMethodType = value;
    }

    public boolean getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(boolean value) {
        this.isVirtual = value;
    }

    public boolean getMultipleOverageAllowed() {
        return multipleOverageAllowed;
    }

    public void setMultipleOverageAllowed(boolean value) {
        this.multipleOverageAllowed = value;
    }

    public AmountRangePerCurrency[] getAmountRangePerCurrency() {
        return amountRangePerCurrency;
    }

    public void setAmountRangePerCurrency(AmountRangePerCurrency[] value) {
        this.amountRangePerCurrency = value;
    }

    public boolean getIsTokenizable() {
        return isTokenizable;
    }

    public void setIsTokenizable(boolean value) {
        this.isTokenizable = value;
    }

    public Object[] getSupportedDigitalWalletProviders() {
        return supportedDigitalWalletProviders;
    }

    public void setSupportedDigitalWalletProviders(Object[] value) {
        this.supportedDigitalWalletProviders = value;
    }

    public boolean getIsRestricted() {
        return isRestricted;
    }

    public void setIsRestricted(boolean value) {
        this.isRestricted = value;
    }

    public boolean getSupportsSubscription() {
        return supportsSubscription;
    }

    public void setSupportsSubscription(boolean value) {
        this.supportsSubscription = value;
    }
}

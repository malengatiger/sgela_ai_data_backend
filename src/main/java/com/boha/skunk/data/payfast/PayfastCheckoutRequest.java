package com.boha.skunk.data.payfast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming()
/*
<input type="hidden" name="merchant_id" value="10000100">
<input type="hidden" name="merchant_key" value="46f0cd694581a">
<input type="hidden" name="return_url" value="https://www.example.com/success">
<input type="hidden" name="cancel_url" value="https://www.example.com/cancel">
<input type="hidden" name="notify_url" value="https://www.example.com/notify">

 */
public class PayfastCheckoutRequest {
    @JsonProperty("merchant_id")
    private String merchantId;
    @JsonProperty("merchant_key")
    private String merchantKey;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("cancel_url")
    private String cancelUrl;
    @JsonProperty("notify_url")
    private String notifyUrl;

    public PayfastCheckoutRequest(String merchantId, String merchantKey, String returnUrl, String cancelUrl, String notifyUrl) {
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
        this.notifyUrl = notifyUrl;
    }

    public PayfastCheckoutRequest() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}

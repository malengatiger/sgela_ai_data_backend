package com.boha.skunk.data.payfast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming()
public class PayfastSubscriptionRequest {
    public PayfastSubscriptionRequest() {
    }


    public PayfastSubscriptionRequest(String subscriptionType, String type,
                                      String billingDate, double recurringAmount,
                                      int cycles, String subscriptionNotifyEmail,
                                      String subscriptionNotifyWebhook,
                                      String subscriptionNotifyBuyer,
                                      String token, double initialAmount,
                                      double amount, String nextRun,
                                      int frequency, String itemName,
                                      String itemDescription, String nameFirst,
                                      String nameLast, String emailAddress) {
        this.subscriptionType = subscriptionType;
        this.type = type;
        this.billingDate = billingDate;
        this.recurringAmount = recurringAmount;
        this.cycles = cycles;
        this.subscriptionNotifyEmail = subscriptionNotifyEmail;
        this.subscriptionNotifyWebhook = subscriptionNotifyWebhook;
        this.subscriptionNotifyBuyer = subscriptionNotifyBuyer;
        this.token = token;
        this.initialAmount = initialAmount;
        this.amount = amount;
        this.nextRun = nextRun;
        this.frequency = frequency;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.emailAddress = emailAddress;
    }

    @JsonProperty("subscription_type")
    private String subscriptionType;
    @JsonProperty("type")
    private String type;
    @JsonProperty("billing_date")
    private String billingDate;
    @JsonProperty("recurring_amount")
    private double recurringAmount;

    @JsonProperty("cycles")
    private int cycles;
    @JsonProperty("subscription_notify_email")
    private String subscriptionNotifyEmail;

    @JsonProperty("subscription_notify_webhook")
    private String subscriptionNotifyWebhook;

    @JsonProperty("subscription_notify_buyer")
    private String subscriptionNotifyBuyer;
    @JsonProperty("token")
    private String token;

    @JsonProperty("initial_amount")
    private double initialAmount;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("next_run")
    private String nextRun;

    @JsonProperty("frequency")
    private int frequency;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_description")
    private String itemDescription;

    @JsonProperty("name_first")
    private String nameFirst;

    @JsonProperty("name_last")
    private String nameLast;

    @JsonProperty("email_address")
    private String emailAddress;

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public double getRecurringAmount() {
        return recurringAmount;
    }

    public void setRecurringAmount(double recurringAmount) {
        this.recurringAmount = recurringAmount;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public String getSubscriptionNotifyEmail() {
        return subscriptionNotifyEmail;
    }

    public void setSubscriptionNotifyEmail(String subscriptionNotifyEmail) {
        this.subscriptionNotifyEmail = subscriptionNotifyEmail;
    }

    public String getSubscriptionNotifyWebhook() {
        return subscriptionNotifyWebhook;
    }

    public void setSubscriptionNotifyWebhook(String subscriptionNotifyWebhook) {
        this.subscriptionNotifyWebhook = subscriptionNotifyWebhook;
    }

    public String getSubscriptionNotifyBuyer() {
        return subscriptionNotifyBuyer;
    }

    public void setSubscriptionNotifyBuyer(String subscriptionNotifyBuyer) {
        this.subscriptionNotifyBuyer = subscriptionNotifyBuyer;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getNextRun() {
        return nextRun;
    }

    public void setNextRun(String nextRun) {
        this.nextRun = nextRun;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
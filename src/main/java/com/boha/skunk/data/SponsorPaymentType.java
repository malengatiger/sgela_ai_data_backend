package com.boha.skunk.data;


public class SponsorPaymentType {
    double amountPerSponsoree;
    String title;
    int periodInMonths;
    String currency;
    String countryName;
    String date;
    Long id;
    boolean activeFlag;
    int studentsSponsored;

    public SponsorPaymentType() {
    }

    public int getStudentsSponsored() {
        return studentsSponsored;
    }

    public void setStudentsSponsored(int studentsSponsored) {
        this.studentsSponsored = studentsSponsored;
    }

    public double getAmountPerSponsoree() {
        return amountPerSponsoree;
    }

    public void setAmountPerSponsoree(double amountPerSponsoree) {
        this.amountPerSponsoree = amountPerSponsoree;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPeriodInMonths() {
        return periodInMonths;
    }

    public void setPeriodInMonths(int periodInMonths) {
        this.periodInMonths = periodInMonths;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}

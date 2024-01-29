package com.boha.skunk.data;

public class OrganizationSponsorPaymentType {
    Long organizationId;
    String organizationName;
    SponsorPaymentType sponsorPaymentType;
    String date;

    public OrganizationSponsorPaymentType(Long organizationId, String organizationName, SponsorPaymentType sponsorPaymentType, String date) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.sponsorPaymentType = sponsorPaymentType;
        this.date = date;
    }

    public OrganizationSponsorPaymentType() {
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public SponsorPaymentType getSponsorPaymentType() {
        return sponsorPaymentType;
    }

    public void setSponsorPaymentType(SponsorPaymentType sponsorPaymentType) {
        this.sponsorPaymentType = sponsorPaymentType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

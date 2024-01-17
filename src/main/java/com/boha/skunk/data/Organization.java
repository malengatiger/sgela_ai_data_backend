package com.boha.skunk.data;

public class Organization {
    Long id;
    User adminUser;
    private Country country;
    private City city;
    String name;
    String logoUrl;
    String splashUrl;
    String date;
    String tagLine;
    int maxUsers;
    int coverageRadiusInKM;
    boolean exclusiveCoverage;


    public Organization() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isExclusiveCoverage() {
        return exclusiveCoverage;
    }

    public void setExclusiveCoverage(boolean exclusiveCoverage) {
        this.exclusiveCoverage = exclusiveCoverage;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public String getSplashUrl() {
        return splashUrl;
    }

    public int getCoverageRadiusInKM() {
        return coverageRadiusInKM;
    }

    public void setCoverageRadiusInKM(int coverageRadiusInKM) {
        this.coverageRadiusInKM = coverageRadiusInKM;
    }

    public void setSplashUrl(String splashUrl) {
        this.splashUrl = splashUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public User getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(User adminUser) {
        this.adminUser = adminUser;
    }

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

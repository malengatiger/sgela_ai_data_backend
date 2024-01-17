package com.boha.skunk.data;

//@Table(name = "City")
public class City {
    
    //@GeneratedValue(strategy = GenerationType.AUTO)
   //@Column(name = "id", nullable = false)
    private Long id;

    String name;
    double latitude;
    double longitude;
    //
    //(name = "state_id")
    Long stateId;
    //
    //(name = "country_id")
    Long countryId;

    //@Table(mappedBy = "city")
//    List<Organization> organizations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}

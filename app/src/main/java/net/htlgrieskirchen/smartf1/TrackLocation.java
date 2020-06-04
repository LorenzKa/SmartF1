package net.htlgrieskirchen.smartf1;

public class TrackLocation {
    private String longitude;
    private String latitude;
    private String locality;
    private String country;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return longitude+","+ latitude +","+locality+","+country;
    }
    public TrackLocation(String latitude, String longitude, String locality, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

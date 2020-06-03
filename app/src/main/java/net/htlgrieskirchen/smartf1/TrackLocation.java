package net.htlgrieskirchen.smartf1;

public class TrackLocation {
    private String longitude;
    private String latiude;
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
        return longitude+","+latiude+","+locality+","+country;
    }

    public String getLatiude() {
        return latiude;
    }

    public void setLatiude(String latiude) {
        this.latiude = latiude;
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

    public TrackLocation(String longitude, String latiude, String locality, String country) {
        this.longitude = longitude;
        this.latiude = latiude;
        this.locality = locality;
        this.country = country;
    }
}

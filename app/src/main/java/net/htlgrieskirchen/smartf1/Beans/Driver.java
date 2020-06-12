package net.htlgrieskirchen.smartf1.Beans;


public class Driver  {

    private String driverid;
    private String permanentNumber;
    private String code;
    private String url;
    private String givenName;
    private String familyName;
    private String dateOfBirth;
    private String nationality;
    private Constructor[] constructors;
    private String seasonWins;
    private String seasonPoints;


    @Override
    public String toString() {
        return driverid+","+permanentNumber+","+code+","+url+","+givenName+","+familyName+","+dateOfBirth+","+nationality+","+constructors.toString()+","+seasonWins+","+seasonPoints;
    }

    public Driver(String driverId, String permanentNumber, String code, String url, String givenName, String familyName, String dateOfBirth, String nationality, Constructor[] constructors, String seasonWins, String seasonPoints) {
        this.driverid = driverId;
        this.permanentNumber = permanentNumber;
        this.code = code;
        this.url = url;
        this.givenName = givenName;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.constructors = constructors;
        this.seasonWins = seasonWins;
        this.seasonPoints = seasonPoints;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getPermanentNumber() {
        return permanentNumber;
    }

    public void setPermanentNumber(String permanentNumber) {
        this.permanentNumber = permanentNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Constructor[] getConstructors() {
        return constructors;
    }

    public void setConstructors(Constructor[] constructors) {
        this.constructors = constructors;
    }

    public String getSeasonWins() {
        return seasonWins;
    }

    public void setSeasonWins(String seasonWins) {
        this.seasonWins = seasonWins;
    }

    public String getSeasonPoints() {
        return seasonPoints;
    }

    public void setSeasonPoints(String seasonPoints) {
        this.seasonPoints = seasonPoints;
    }
}

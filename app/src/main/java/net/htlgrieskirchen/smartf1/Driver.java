package net.htlgrieskirchen.smartf1;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class Driver  {
    private String driverid;
    private String permanentNumber;
    private String code;
    private String url;
    private String givenName;
    private String familyName;
    private String dateOfBirth;
    private String age;
    private String nationality;
    private Constructor[] constructors;
    private String SeasonWins;
    private String SeasonPoints;

    public void setSeasonWins(String seasonWins) {
        SeasonWins = seasonWins;
    }

    public void setSeasonPoints(String seasonPoints) {
        SeasonPoints = seasonPoints;
    }

    public String getSeasonWins() {
        return SeasonWins;
    }

    public String getSeasonPoints() {
        return SeasonPoints;
    }

    public String format(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = sdf.parse(dateOfBirth);
            dateOfBirth = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateOfBirth;
    }

    public String Calcage(){
        int age = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            Date birth = sdf.parse(dateOfBirth);
            Date d = new Date();

            LocalDate birthday = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

             age = Period.between(birthday, now).getYears();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(age);
    }

    @Override
    public String toString() {
        return driverid+","+permanentNumber+","+code+","+url+","+givenName+","+familyName+","+dateOfBirth+","+nationality+","+constructors.toString();
    }

    public Driver(String driverId, String permanentNumber, String code, String url, String givenName, String familyName, String dateOfBirth, String nationality, Constructor[] constructors) {
        this.driverid = driverId;
        this.permanentNumber = permanentNumber;
        this.code = code;
        this.url = url;
        this.givenName = givenName;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.constructors = constructors;
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
}

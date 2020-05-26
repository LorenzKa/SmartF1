package net.htlgrieskirchen.smartf1;

public class Driver {

    private String firstname;
    private String lastname;
    private String wins;
    private String nationality;
    private String points;
    private String constructor;
    private String position;


    public String getFirstname() {
        return firstname;
    }

    public Driver(String firstname, String lastname, String wins, String nationality, String points, String constructor, String position) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.wins = wins;
        this.nationality = nationality;
        this.points = points;
        this.constructor = constructor;
        this.position = position;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String toString() {
        return position+" "+firstname+" "+lastname+", "+constructor+", Punkte: "+points;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getConstructor() {
        return constructor;
    }

    public void setConstructor(String constructor) {
        this.constructor = constructor;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

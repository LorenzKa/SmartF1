package net.htlgrieskirchen.smartf1.Beans;

public class ConstructorResult {
    String positionText;
    String points;
    String wins;
    Constructor Constructor;

    public String getPositionText() {
        return positionText;
    }

    public void setPositionText(String positionText) {
        this.positionText = positionText;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public net.htlgrieskirchen.smartf1.Beans.Constructor getConstructor() {
        return Constructor;
    }

    public void setConstructor(net.htlgrieskirchen.smartf1.Beans.Constructor constructor) {
        Constructor = constructor;
    }

    public ConstructorResult(String positionText, String points, String wins, net.htlgrieskirchen.smartf1.Beans.Constructor constructor) {
        this.positionText = positionText;
        this.points = points;
        this.wins = wins;
        Constructor = constructor;
    }
}

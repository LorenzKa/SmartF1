package net.htlgrieskirchen.smartf1.Beans;

public class RaceResult {
    String positionText;
    String points;
    Driver Driver;
    Constructor Constructor;
    String grid;
    String laps;
    String status;
    Time Time;
    FastestLap FastestLap;

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

    public net.htlgrieskirchen.smartf1.Beans.Driver getDriver() {
        return Driver;
    }

    public void setDriver(net.htlgrieskirchen.smartf1.Beans.Driver driver) {
        Driver = driver;
    }

    public net.htlgrieskirchen.smartf1.Beans.Constructor getConstructor() {
        return Constructor;
    }

    public void setConstructor(net.htlgrieskirchen.smartf1.Beans.Constructor constructor) {
        Constructor = constructor;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getLaps() {
        return laps;
    }

    public void setLaps(String laps) {
        this.laps = laps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public net.htlgrieskirchen.smartf1.Beans.Time getTime() {
        return Time;
    }

    public void setTime(net.htlgrieskirchen.smartf1.Beans.Time time) {
        Time = time;
    }

    public net.htlgrieskirchen.smartf1.Beans.FastestLap getFastestLap() {
        return FastestLap;
    }

    public void setFastestLap(net.htlgrieskirchen.smartf1.Beans.FastestLap fastestLap) {
        FastestLap = fastestLap;
    }

    public RaceResult(String positionText, String points, net.htlgrieskirchen.smartf1.Beans.Driver driver, net.htlgrieskirchen.smartf1.Beans.Constructor constructor, String grid, String laps, String status, net.htlgrieskirchen.smartf1.Beans.Time time, net.htlgrieskirchen.smartf1.Beans.FastestLap fastestLap) {
        this.positionText = positionText;
        this.points = points;
        Driver = driver;
        Constructor = constructor;
        this.grid = grid;
        this.laps = laps;
        this.status = status;
        Time = time;
        FastestLap = fastestLap;
    }
}
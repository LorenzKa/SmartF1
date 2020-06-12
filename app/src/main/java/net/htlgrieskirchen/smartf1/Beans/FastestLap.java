package net.htlgrieskirchen.smartf1.Beans;


public class FastestLap {
    String rank;
    String lap;
    Time Time;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getLap() {
        return lap;
    }

    public void setLap(String lap) {
        this.lap = lap;
    }

    public net.htlgrieskirchen.smartf1.Beans.Time getTime() {
        return Time;
    }

    public void setTime(net.htlgrieskirchen.smartf1.Beans.Time time) {
        Time = time;
    }

    public FastestLap(String rank, String lap, net.htlgrieskirchen.smartf1.Beans.Time time) {
        this.rank = rank;
        this.lap = lap;
        Time = time;
    }
}

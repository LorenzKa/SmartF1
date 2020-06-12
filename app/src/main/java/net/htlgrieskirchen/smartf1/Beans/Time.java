package net.htlgrieskirchen.smartf1.Beans;

public class Time {
    String time;
    String millis;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMillis() {
        return millis;
    }

    public void setMillis(String millis) {
        this.millis = millis;
    }

    public Time(String time, String millis) {
        this.time = time;
        this.millis = millis;
    }
}

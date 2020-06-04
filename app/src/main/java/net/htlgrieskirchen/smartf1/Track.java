package net.htlgrieskirchen.smartf1;

public class Track {

    private String circuitId;
    private String url;
    private String circuitName;
    private TrackLocation[] trackLocations;

    public String getCircuitId() {
        return circuitId;
    }

    @Override
    public String toString() {
        return circuitId+","+url+","+circuitName+","+trackLocations;
    }

    public void setCircuitId(String circuitId) {
        this.circuitId = circuitId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public Track(String circuitId, String url, String circuitName, TrackLocation[] trackLocations) {
        this.circuitId = circuitId;
        this.url = url;
        this.circuitName = circuitName;
        this.trackLocations = trackLocations;
    }

    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    public TrackLocation[] getTrackLocations() {
        return trackLocations;
    }

    public void setTrackLocations(TrackLocation[] trackLocations) {
        this.trackLocations = trackLocations;
    }
}

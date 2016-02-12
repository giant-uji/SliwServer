package es.uji.al259348.sliwserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(indexName = "sliw", type = "samples")
public class Sample {

    public static class WifiScanResult {

        public String SSID;
        public String BSSID;
        public int level;

        public WifiScanResult() {
            super();
        }

        public WifiScanResult(String SSID, String BSSID, int level) {
            this.SSID = SSID;
            this.BSSID = BSSID;
            this.level = level;
        }

        @Override
        public String toString() {
            return "WifiScanResult{" +
                    "SSID='" + SSID + '\'' +
                    ", BSSID='" + BSSID + '\'' +
                    ", level=" + level +
                    '}';
        }

    }

    @Id
    private String id;
    private User user;
    private Date date;
    private Location location;
    private List<WifiScanResult> scanResults;
    private boolean valid;

    public Sample() {
        super();
        this.date = new Date();
        this.scanResults = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<WifiScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<WifiScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", date=" + date +
                ", location=" + location +
                ", scanResults=" + scanResults +
                ", valid=" + valid +
                '}';
    }

}

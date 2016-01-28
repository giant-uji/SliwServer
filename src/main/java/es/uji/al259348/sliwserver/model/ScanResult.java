package es.uji.al259348.sliwserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanResult {

    public String SSID;
    public String BSSID;
    public String capabilities;
    public int level;

    @Override
    public String toString() {
        return "ScanResult{" +
                "SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", capabilities='" + capabilities + '\'' +
                ", level=" + level +
                '}';
    }
}

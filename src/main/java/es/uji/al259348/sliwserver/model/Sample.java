package es.uji.al259348.sliwserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {

    private Date date;
    private List<ScanResult> scanResultList;

    public Sample() {
        this.date = new Date();
        this.scanResultList = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ScanResult> getScanResultList() {
        return scanResultList;
    }

    public void setScanResultList(List<ScanResult> scanResultList) {
        this.scanResultList = scanResultList;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "date=" + date +
                ", scanResultList=" + scanResultList +
                '}';
    }
}

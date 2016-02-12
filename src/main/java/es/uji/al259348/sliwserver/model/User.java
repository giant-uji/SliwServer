package es.uji.al259348.sliwserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import weka.classifiers.Classifier;

import java.util.List;

@Document(indexName = "sliw", type = "users")
public class User {

    @Id
    private String id;
    private String name;
    private List<Location> locations;
    private boolean configured;
    private List<Classifier> classifiers;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public List<Classifier> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(List<Classifier> classifiers) {
        this.classifiers = classifiers;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locations=" + locations +
                ", configured=" + configured +
                ", classifiers=" + classifiers +
                '}';
    }

}

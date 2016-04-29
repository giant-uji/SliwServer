package es.uji.al259348.sliwserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sliw", type = "devices")
public class Device {

    @Id
    private String id;
    private String name;
    private User user;

    public Device() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user=" + user +
                '}';
    }

}

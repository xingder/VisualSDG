package io.yingchi.visualsdgmongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SelectedService")
public class SelectedService {

    @Id
    private String id;
    private String serviceName;
    private String version;

    public SelectedService(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }
}

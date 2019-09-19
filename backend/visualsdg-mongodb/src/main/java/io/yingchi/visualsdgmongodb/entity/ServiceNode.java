package io.yingchi.visualsdgmongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "service")
public class ServiceNode {

    @Id
    private String id;
    private String serviceName;
    private String version;
    private List<String> endpoints;
    private List<Map<String, Object>> dependencies;

    public ServiceNode(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies) {
        this.serviceName = serviceName;
        this.version = version;
        this.endpoints = endpoints;
        this.dependencies = dependencies;
    }

    public ServiceNode() {

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

    public List<String> getEndpoints() {
        return endpoints;
    }

    public List<Map<String, Object>> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "ServiceNode{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                ", endpoints=" + endpoints +
                ", dependencies=" + dependencies +
                '}';
    }
}

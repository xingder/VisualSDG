package io.yingchi.visualsdgmongodb.domain.VO;

import java.util.List;
import java.util.Map;

/**
 * 用于接收 Yaml 上传后转换生成的 Object
 */
public class ResultYamlObject {

    private String serviceName;
    private String version;
    private List<String> endpoints;
    private List<Map<String, Object>> dependencies;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public List<Map<String, Object>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Map<String, Object>> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "ResultYamlObject{" +
                "serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                ", endpoints=" + endpoints +
                ", dependencies=" + dependencies +
                '}';
    }
}

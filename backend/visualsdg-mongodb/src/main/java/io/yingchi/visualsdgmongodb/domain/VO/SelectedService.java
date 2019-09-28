package io.yingchi.visualsdgmongodb.domain.VO;

public class SelectedService {

    private String serviceName;
    private String version;

    public SelectedService(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }
}

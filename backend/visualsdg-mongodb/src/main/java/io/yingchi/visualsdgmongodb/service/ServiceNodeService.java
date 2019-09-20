package io.yingchi.visualsdgmongodb.service;

import java.util.List;
import java.util.Map;

public interface ServiceNodeService {

    public void add(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies);

    public void delete(String serviceName, String version);
}

package io.yingchi.visualsdgmongodb.service;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;

import java.util.List;
import java.util.Map;

public interface ServiceNodeService {


    public void add(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies);

    public void delete(String serviceName, String version);

    public List<String> getAllExistingServiceNameList();
}

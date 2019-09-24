package io.yingchi.visualsdgmongodb.service;

import java.util.List;
import java.util.Map;

public interface ServiceNodeService {


    public void add(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies);

    public void delete(String serviceName, String version);

    public List<String> getAllExistingServiceNameList(); // 获取服务库中服务名单

    public Map<String, Object> serviceVersionChangeCheck(String serviceName, String toVersion);
}

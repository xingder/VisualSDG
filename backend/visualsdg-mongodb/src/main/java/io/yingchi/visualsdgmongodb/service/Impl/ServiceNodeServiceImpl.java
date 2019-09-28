package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.domain.VO.SelectedService;
import io.yingchi.visualsdgmongodb.domain.PO.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceNodeServiceImpl implements ServiceNodeService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ServiceNodeRepository serviceNodeRepository;

    @Autowired
    SelectedServiceRepository selectedServiceRepository;

    @Override
    public void add(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies) {
        List<ServiceNode> serviceNodesFound = serviceNodeRepository.findServiceNodesByServiceName(serviceName);
        // 库中存在同名服务节点
        if (serviceNodesFound != null) {
            // 检测库中重复版本服务节点
            for (ServiceNode service : serviceNodesFound) {
                if (service.getVersion().equals(version)) {
                    logger.warn("数据库中已存在相同节点 --> " + serviceName + ": " + version);
                    return;
                }
            }
        }
        // 库中无重复版本服务节点
        ServiceNode serviceNode = new ServiceNode(serviceName, version, endpoints, dependencies);
        serviceNodeRepository.save(serviceNode);
        logger.info("新 ServiceNode 已经添加：" + serviceNode.toString());
    }

    @Override
    public void delete(String serviceName, String version) {
        List<ServiceNode> serviceNodesFound = serviceNodeRepository.findServiceNodesByServiceName(serviceName);

        if (serviceNodesFound == null) {
            logger.warn("不存在服务节点: " + serviceName + " 删除终止");
        } else {
            for (ServiceNode service : serviceNodesFound) {
                if (service.getVersion().equals(version)) {
                    serviceNodeRepository.delete(service);
                    logger.info("已删除节点: " + serviceName + ": " + version);
                    return;
                }
            }
            logger.warn("不存在该版本同名服务节点: " + serviceName + ": " + version);
        }
    }

    @Override
    public List<String> getAllExistingServiceNameList() {
        List<ServiceNode> existingService = serviceNodeRepository.findAll();
        List<String> serviceNameList = new ArrayList<>();

        if (existingService != null) {
            for (ServiceNode service : existingService) {
                String serviceName = service.getServiceName();
                if (!serviceNameList.contains(serviceName)) {
                    // 名单中不存在，添加
                    serviceNameList.add(serviceName);
                }
            }
        }
        return serviceNameList;
    }

    @Override
    public Map<String, Object> serviceVersionChangeCheck(String serviceName, String toVersion) {
        Map<String, Object> checkReslut = new HashMap<>();

        List<SelectedService> allCurrentRunningServices = selectedServiceRepository.findAll();
        Set<String> necessaryEndpoints = new HashSet<>(); // 被依赖的端点，版本变更时关切
        Set<Map<String, Object>> allAvaliableEndpoints = new HashSet<>();
        Map<String, Object> currentServiceProvideEndpoints;


        for (SelectedService currentRunningService : allCurrentRunningServices) {
            if (currentRunningService.getServiceName().equals(serviceName)) {
                continue; // 不统计当前服务节点
            }
            ServiceNode serviceNode = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(currentRunningService.getServiceName(), currentRunningService.getVersion());
            // 当前运行的每一个节点
            List<Map<String, Object>> dependencies = serviceNode.getDependencies();
            // 获取所有依赖信息
            if (dependencies != null) {
                for (Map<String, Object> dependency : dependencies) {
                    if (dependency.get("serviceName").equals(serviceName)) {
                        List<String> endpoints = (List<String>) dependency.get("endpoints");
                        for (String endpoint : endpoints) {
                            necessaryEndpoints.add(endpoint);
                        }
                    }
                }
            }
            List<String> currentServiceEndpoints = serviceNode.getEndpoints();
            currentServiceProvideEndpoints = new HashMap<>();
            currentServiceProvideEndpoints.put("serviceName", currentRunningService.getServiceName());
            currentServiceProvideEndpoints.put("endpoints", currentServiceEndpoints);
            allAvaliableEndpoints.add(currentServiceProvideEndpoints);

        }

        ServiceNode toVersionService = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, toVersion); // 变更到的版本
        if (toVersionService == null) {
            checkReslut.put("ableToChange", "no");
            checkReslut.put("reason", "服务变更版本不存在");
            return checkReslut;
        }
        List<String> endpoints = toVersionService.getEndpoints();
        List<Map<String, Object>> dependencies = toVersionService.getDependencies();
        for (String necessaryEndpoint : necessaryEndpoints) {
            if (!endpoints.contains(necessaryEndpoint)) {
                checkReslut.put("ableToChange", "no");
                checkReslut.put("reason", "当前体系依赖的服务端点 " + necessaryEndpoint + " 变更版本将丢失");
                return checkReslut;
            }
        }
        for (Map<String, Object> dependency : dependencies) {
            // 变更后的版本需要的依赖
            String dependencyService = (String) dependency.get("serviceName"); // 依赖的服务
            List<String> dependencyEndpoints = (List<String>) dependency.get("endpoints"); // 依赖的服务的端点

            // 检查是否可以建立依赖
            for (Map<String, Object> existDependency : allAvaliableEndpoints) {
                if (existDependency.get("serviceName").equals(dependencyService)) {
                    List<String> canProvideEndpoints = (List<String>) existDependency.get("endpoints");
                    // 检查
                    for (String needEndpoint : dependencyEndpoints) {
                        // 对于新版本中每一个依赖的端点，检查是否可以满足
                        if (!canProvideEndpoints.contains(needEndpoint)) {
                            // 对应的服务不能提供变更服务需要的端点
                            checkReslut.put("ableToChange", "no");
                            checkReslut.put("reason", "当前版本 " + dependencyService + " 不能提供变更服务需要的端点 " + needEndpoint);
                            return checkReslut;
                        }
                    }
                }
            }


        }
        checkReslut.put("ableToChange", "yes");



        return checkReslut;
    }
}

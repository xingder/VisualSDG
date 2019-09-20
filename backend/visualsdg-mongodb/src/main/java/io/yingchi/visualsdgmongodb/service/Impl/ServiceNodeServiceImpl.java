package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ServiceNodeServiceImpl implements ServiceNodeService {

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private ServiceNodeRepository serviceNodeRepository;

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
        logger.info("新 ServiceNode 已经添加：");
        logger.info(serviceNode.toString());
    }
}

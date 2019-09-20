package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.WebDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebDataServiceImpl implements WebDataService {
    @Autowired
    ServiceNodeRepository serviceNodeRepository;

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Override
    public List<Map<String, Object>> getServiceTableData() {
        List<ServiceNode> serviceNodesFound = serviceNodeRepository.findAll();
        if (serviceNodesFound != null) {
            List<Map<String, Object>> servicesList = new ArrayList<>();
            int counter = 0;

            for (ServiceNode service : serviceNodesFound) {
                Map<String, Object> row = new HashMap<>();
                row.put("service", service.getServiceName()); // 服务名
                row.put("version", service.getVersion()); // 服务版本
                row.put("endpoints", service.getEndpoints()); // 端点列表
                row.put("dependencies", service.getDependencies()); // 依赖列表

                servicesList.add(row);
                counter++;
            }
            logger.info("getServiceTableData() 成功获取到 " + counter + " 条 Service 数据");
            return servicesList;
        }

        logger.warn("getServiceTableData(): 无 Service 数据");
        return null;
    }

    @Override
    public List<Map<String, Object>> getGraphNodesData() {
        List<ServiceNode> serviceNodesFound = serviceNodeRepository.findAll();
        if (serviceNodesFound == null) {
            logger.error("数据库 ServiceNode 数据空，无法生成 nodes 数据");
            return null;
        } else {
            List<Map<String, Object>> nodes = new ArrayList<>();

            for (ServiceNode service : serviceNodesFound) {

            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> getGraphLinksData() {
        return null;
    }
}

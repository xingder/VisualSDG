package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
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

    @Autowired
    ServiceNodeService serviceNodeService;

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
            return servicesList;
        }

        logger.warn("getServiceTableData(): 无 Service 数据");
        return null;
    }

    @Override
    public List<List<Map<String, Object>>> getCascaderOptionsData() {
        List<String> serviceNameList = serviceNodeService.getAllExistingServiceNameList();
        List<List<Map<String, Object>>> cascaders = new ArrayList<>(); // 新建 cascader 的列表

        if (serviceNameList != null) {
            // 当现有服务列表不为空时
            for (String serviceName : serviceNameList) {
                // 遍历所有服务类别
                List<Map<String, Object>> cascader = new ArrayList<>(); // 为当前服务新建一个 cascader
                Map<String, Object> itemFirstClass = new HashMap<>(); // 新建一个一级选项
                itemFirstClass.put("value", serviceName);
                itemFirstClass.put("label", serviceName);
                List<Map<String, Object>> children = new ArrayList<>(); // 二级选项 children 列表
                Map<String, Object> itemSecondClass; // 新建二级选项
                List<ServiceNode> currentServiceList = serviceNodeRepository.findServiceNodesByServiceName(serviceName);
                for (ServiceNode serviceNode : currentServiceList) {
                    itemSecondClass = new HashMap<>();
                    itemSecondClass.put("value", serviceNode.getVersion());
                    itemSecondClass.put("label", serviceNode.getVersion());

                    children.add(itemSecondClass); // 添加二级选项（版本名）
                }
                itemFirstClass.put("children", children);
                cascader.add(itemFirstClass); // 添加一级选项（服务名）
                cascaders.add(cascader); // 添加一个服务 cascader
            }
        }

        return cascaders;
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

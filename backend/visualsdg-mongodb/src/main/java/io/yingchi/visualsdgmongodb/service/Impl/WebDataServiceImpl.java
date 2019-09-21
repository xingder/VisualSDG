package io.yingchi.visualsdgmongodb.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.yingchi.visualsdgmongodb.entity.SelectedService;
import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import io.yingchi.visualsdgmongodb.service.WebDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebDataServiceImpl implements WebDataService {
    @Autowired
    ServiceNodeRepository serviceNodeRepository;

    @Autowired
    SelectedServiceRepository selectedServiceRepository;

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
        List<SelectedService> allSelectedServices = selectedServiceRepository.findAll(); // 获取到依赖生成页面已经选择的 Service 列表
        List<Map<String, Object>> nodes = new ArrayList<>(); // 声明并初始化 nodes 列表
        Map<String, Object> node; // 声明单个 node

        Map<String, Object> itemStyleForService = new HashMap<>();
        Map<String, Object> itemStyleForEndpoint = new HashMap<>();
        itemStyleForService.put("color", "blue");
        itemStyleForEndpoint.put("color", "red");

        if (allSelectedServices != null) {
            for (SelectedService selectedService : allSelectedServices) {

                String serviceName = selectedService.getServiceName();
                String version = selectedService.getVersion();
                ServiceNode serviceFound = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, version);
                node = new HashMap<>();
                node.put("name", serviceName);
                node.put("label", serviceName);
                node.put("categories", serviceName);
                List<String> endpoints = serviceFound.getEndpoints();
                if (endpoints != null) {
                    // 有端点的服务，非 EdgeService
                    node.put("value", endpoints.size() * 5 + 30);
                } else {
                    node.put("value", 30);
                }
                node.put("itemStyle", itemStyleForService);
                nodes.add(node);

                // 添加 Service 下的 Endpoints
                if (endpoints != null) {
                    for (String enpointName : endpoints) {
                        String endpointNodeName = serviceName + " " + enpointName;
                        node = new HashMap<>();
                        node.put("name", endpointNodeName);
                        node.put("label", endpointNodeName);
                        node.put("categories", endpointNodeName);
                        node.put("value", 20);
                        node.put("itemStyle", itemStyleForEndpoint);
                        nodes.add(node);
                    }
                }
            }
            logger.info("add nodes finish");
        } else {
            logger.error("Selected Services 为空");
        }


        return nodes;
    }

    @Override
    public List<Map<String, Object>> getGraphLinksData() {
        List<SelectedService> allSelectedServices = selectedServiceRepository.findAll(); // 获取到依赖生成页面已经选择的 Service 列表
        List<Map<String, Object>> links = new ArrayList<>(); // 声明并初始化 nodes 列表
        Map<String, Object> link; // 声明单个 node

        Map<String, Object> labelForEndpointLink = new HashMap<>(); // API Endpoint Link 标签
        Map<String, Object> lineStyleForEndpointLink = new HashMap<>(); // API Endpoint Link 标签
        labelForEndpointLink.put("formatter", "ENDPOINT");
        labelForEndpointLink.put("show", true);
        lineStyleForEndpointLink.put("color", "blue");
        lineStyleForEndpointLink.put("width", 3);

        Map<String, Object> labelForInvokeLink = new HashMap<>(); // Invoke Link 标签
        Map<String, Object> lineStyleForInvokeLink = new HashMap<>(); // Invoke Link 标签
        List<String> symbolForInvokeLink = new ArrayList<>();
        labelForInvokeLink.put("formatter", "INVOKE");
        labelForInvokeLink.put("show", true);
        lineStyleForInvokeLink.put("color", "orange");
        lineStyleForInvokeLink.put("width", 3);
        symbolForInvokeLink.add(0, "none");
        symbolForInvokeLink.add(1, "arrow");

        if (allSelectedServices != null) {
            for (SelectedService selectedService : allSelectedServices) {

                String serviceName = selectedService.getServiceName();
                String version = selectedService.getVersion();
                ServiceNode serviceFound = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, version);

                List<String> endpoints = serviceFound.getEndpoints();
                if (endpoints != null) {
                    // 有端点的服务，非 EdgeService
                    for (String endpoint : endpoints) {
                        link = new HashMap<>();
                        link.put("source", serviceName);
                        link.put("target", serviceName + " " + endpoint);
                        link.put("value", 100);
                        link.put("label", labelForEndpointLink);
                        link.put("lineStyle", lineStyleForEndpointLink);

                        links.add(link);
                    }
                }

                List<Map<String, Object>> dependencies = serviceFound.getDependencies();
                if (dependencies != null) {
                    for (Map<String, Object> dependency : dependencies) {
                        String toService = (String) dependency.get("serviceName");
                        List<String> toEndpoints = (List<String>) dependency.get("endpoints");
                        for (String toEndpoint : toEndpoints) {
                            link = new HashMap<>();
                            link.put("source", serviceName);
                            link.put("target", toService + " " + toEndpoint);
                            link.put("value", 5);
                            link.put("label", labelForInvokeLink);
                            link.put("lineStyle", lineStyleForInvokeLink);
                            link.put("symbol", symbolForInvokeLink);

                            links.add(link);
                        }
                    }
                }

            }
        } else {
            logger.error("Selected Services 为空");
        }


        return links;
    }

}

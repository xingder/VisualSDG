package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.domain.PO.SelectedService;
import io.yingchi.visualsdgmongodb.domain.PO.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
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
    SelectedServiceRepository selectedServiceRepository;

    @Autowired
    ServiceNodeService serviceNodeService;

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Override
    public List<Map<String, Object>> getServiceTableData() {

        List<Map<String, Object>> servicesList = new ArrayList<>();
        List<Map<String, Object>> servicesChildVersionList;
        Map<String, Object> row, childRow;
        int serviceKey, versionsKey;


        List<String> allExistingServiceNameList = serviceNodeService.getAllExistingServiceNameList();
        if (allExistingServiceNameList != null) {
            serviceKey = 1; // 加 key 否则报 Duplicate key 错误
            for (String serviceName : allExistingServiceNameList) {
                // 每一个已经存在的 service，每个 service 有不同版本
                row = new HashMap<>();
                row.put("service", serviceName);
                row.put("key", serviceKey);

                List<String> parentVersionList = new ArrayList<>();
                List<ServiceNode> differentVersionServices = serviceNodeRepository.findServiceNodesByServiceName(serviceName);
                servicesChildVersionList = new ArrayList<>();

                int counter = 1;
                for (ServiceNode differentVersionService : differentVersionServices) {
                    // 同名 service 下每一个 version 的 service
                    childRow = new HashMap<>();
                    versionsKey = serviceKey * 10000 + counter;
                    childRow.put("service", serviceName);
                    childRow.put("key", versionsKey);
                    String version = differentVersionService.getVersion();
                    List<String> childVersionList = new ArrayList<>();
                    childVersionList.add(version);
                    childRow.put("versions", childVersionList);
                    childRow.put("endpoints", differentVersionService.getEndpoints());
                    childRow.put("dependencies", differentVersionService.getDependencies());
                    servicesChildVersionList.add(childRow);

                    counter++;
                    parentVersionList.add(version);
                }
                row.put("versions", parentVersionList);
                row.put("children", servicesChildVersionList);

                servicesList.add(row);
                serviceKey++;
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

        Map<String, Object> itemStyleForNormalService = new HashMap<>();
        Map<String, Object> itemStyleForEndpoint = new HashMap<>();
        Map<String, Object> itemStyleForEdgeService = new HashMap<>();
        Map<String, Object> itemStyleForBaseService = new HashMap<>();
        itemStyleForNormalService.put("color", "blue");
        itemStyleForEndpoint.put("color", "red");
        itemStyleForEdgeService.put("color", "green");
        itemStyleForBaseService.put("color", "black");

        if (allSelectedServices != null) {
            for (SelectedService selectedService : allSelectedServices) {

                String serviceName = selectedService.getServiceName();
                String version = selectedService.getVersion();
                ServiceNode serviceFound = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, version);
                if (serviceFound == null) {
                    return null; // 强行删除服务引起的已选择服务丢失数据，返回空数据
                }
                node = new HashMap<>();
                node.put("name", serviceName);
                node.put("label", serviceName);
                node.put("categories", serviceName);
                List<String> endpoints = serviceFound.getEndpoints();
                if (endpoints != null) {
                    node.put("value", endpoints.size() * 5 + 30);
                    if (serviceFound.getDependencies() != null) {
                        // 提供服务，同时依赖别的服务
                        node.put("itemStyle", itemStyleForNormalService);
                    } else {
                        // 提供服务，不依赖别的服务，BaseService
                        node.put("itemStyle", itemStyleForBaseService);
                    }
                } else {
                    // 不提供服务
                    node.put("value", 30);
                    node.put("itemStyle", itemStyleForEdgeService);
                }

                nodes.add(node);

                // 添加 Service 下的 Endpoints
                if (endpoints != null) {
                    for (String enpointName : endpoints) {
                        String endpointNodeName = serviceName + " " + enpointName;
                        node = new HashMap<>();
                        node.put("name", endpointNodeName);
                        node.put("label", endpointNodeName);
                        node.put("categories", endpointNodeName);
                        node.put("value", 15);
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
                if (serviceFound == null) {
                    return null; // 强行删除服务导致的已选择服务数据丢失，返回空数据
                }
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

    /**
     * 获取部署顺序列表
     * @return
     */
    @Override
    public List<Map<String, Object>> getDeployList() {
        List<Map<String, Object>> deployList = new ArrayList<>();
        Map<String, Object> deployNode;
        List<SelectedService> allSelectedServices = selectedServiceRepository.findAll();

        List<Map<String, Object>> deployComputeList = new ArrayList<>(); // 当前需要计算的节点
        Map<String, Object> serviceUnit; // serviceName:  outDegree:
        int outDegree = 0; // 节点出度（当前阶段依赖的服务数量）
        int deployed = 0;  // 当前已经安排部署的点
        List<String> currentStageRemovedNodes; // 当前阶段移除的出度为 0 的节点

        // 1. 初始化出度计算列表
        for (SelectedService selectedService : allSelectedServices) {
            ServiceNode selectedServiceNode = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(selectedService.getServiceName(), selectedService.getVersion());
            serviceUnit = new HashMap<>();
            List<Map<String, Object>> dependencies = selectedServiceNode.getDependencies();
            if (dependencies == null) {
                // 依赖列表为 null，出度为 0
                outDegree = 0;
            } else {
                // 依赖列表存在，出度即为其容量
                outDegree = dependencies.size();
            }
            serviceUnit.put("serviceName", selectedService.getServiceName());
            serviceUnit.put("version", selectedService.getVersion());
            serviceUnit.put("outDegree", outDegree);
            serviceUnit.put("status", 0); // 0 未安排部署 1 已安排
            deployComputeList.add(serviceUnit);
        }

        while (deployed != allSelectedServices.size()) {
            // 2. 当已经安排部署的数量不等于当前选定的服务数量时循环

            currentStageRemovedNodes = new ArrayList<>(); // 当前阶段移除的出度为 0 的点

            for (Map<String, Object> currentNode : deployComputeList) {
                // 选部署点阶段（找出度0）
                deployNode = new HashMap<>();
                outDegree = (int) currentNode.get("outDegree");
                int status = (int) currentNode.get("status");
                if (status != 1) {
                    if (outDegree == 0) {
                        // 存在出度为0的点，记为当前阶段移除点，并添加进部署清单
                        String serviceName = (String) currentNode.get("serviceName");
                        String version = (String) currentNode.get("version");
                        currentStageRemovedNodes.add((String) currentNode.get("serviceName")); // 当前阶段移除点中加入当前节点名称

                        deployNode.put("serviceName", serviceName);
                        deployNode.put("version", version);
                        deployList.add(deployed++, deployNode);

                        currentNode.put("status", 1); // 已经安排
                    }
                }
            }

            for (Map<String, Object> currentNode : deployComputeList) {
                // 更新各个节点的出度，只有依赖中包含上一轮移除的点的节点出度才会变化
                String serviceName = (String) currentNode.get("serviceName");
                String version = (String) currentNode.get("version");
                ServiceNode serviceFound = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, version);
                List<Map<String, Object>> dependencies = serviceFound.getDependencies();

                if (dependencies != null) {
                    for (Map<String, Object> dependency : dependencies) {
                        String dependentServiceName = (String) dependency.get("serviceName");
                        if (currentStageRemovedNodes.contains(dependentServiceName)) {
                            // 移除的点在当前节点的依赖列表中，当前节点的出度 -1
                            outDegree = (int) currentNode.get("outDegree");
                            currentNode.put("outDegree", outDegree - 1);
                        }
                    }
                }
            }
        }


        return deployList;
    }

    @Override
    public List<Boolean> getSelectedServicesMutiversionFlags() {
        List<SelectedService> allSelectedServices = selectedServiceRepository.findAll();
        List<ServiceNode> allServices = serviceNodeRepository.findAll();
        List<Boolean> selectedServicesMultiversionFlags = new ArrayList<>();

        for (SelectedService selectedService : allSelectedServices) {
            int flag = 0;
            for (ServiceNode serviceNode : allServices) {
                if (serviceNode.getServiceName().equals(selectedService.getServiceName()) && !serviceNode.getVersion().equals(selectedService.getVersion())) {
                    // 服务中心中存在同名不同版本服务
                    flag = 1;
                }
            }

            if (flag == 1) {
                selectedServicesMultiversionFlags.add(true);
            } else {
                selectedServicesMultiversionFlags.add(false);
            }

        }
        return selectedServicesMultiversionFlags;
    }

}

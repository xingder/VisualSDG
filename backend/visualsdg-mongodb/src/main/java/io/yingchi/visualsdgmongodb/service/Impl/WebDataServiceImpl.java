package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.domain.VO.SelectedService;
import io.yingchi.visualsdgmongodb.domain.PO.ServiceNode;
import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.repository.TenantRepository;
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
    TenantRepository tenantRepository;

    @Autowired
    ServiceNodeService serviceNodeService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public List<Map<String, Object>> getGraphNodesData(String tenantName) {
        Tenant tenant = tenantRepository.findTenantByTenantName(tenantName);
        List<SelectedService> allSelectedServices = tenant.getDeployedServiceList(); // 获取到依赖生成页面已经选择的 Service 列表

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
                        node.put("categories", endpointNodeName);
                        node.put("value", 15);
                        node.put("itemStyle", itemStyleForEndpoint);
                        nodes.add(node);
                    }
                }
            }
        } else {
            logger.error("Selected Services 为空");
        }


        return nodes;
    }

    @Override
    public List<Map<String, Object>> getGraphLinksData(String tenantName) {
        Tenant tenant = tenantRepository.findTenantByTenantName(tenantName);
        List<SelectedService> allSelectedServices = tenant.getDeployedServiceList(); // 获取到依赖生成页面已经选择的 Service 列表
        List<Map<String, Object>> links = new ArrayList<>(); // 声明并初始化 nodes 列表
        Map<String, Object> link; // 声明单个 node

        Map<String, Object> labelForEndpointLink = new HashMap<>(); // API Endpoint Link 标签
        labelForEndpointLink.put("formatter", "ENDPOINT");
        labelForEndpointLink.put("show", true);

        Map<String, Object> labelForInvokeLink = new HashMap<>(); // Invoke Link 标签
        List<String> symbolForInvokeLink = new ArrayList<>();
        labelForInvokeLink.put("formatter", "INVOKE");
        labelForInvokeLink.put("show", true);
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
     * 获取部署顺序列表，部署依赖检测是依据当前已选择的服务
     * @return
     */
    @Override
    public List<Map<String, Object>> getDeployList(List<SelectedService> allSelectedServices) {

        List<Map<String, Object>> deployList = new ArrayList<>(); // 新建部署序列
        Map<String, Object> deployNode; // 部署序列中各部署节点的信息 map
        List<String> allSelectedServicesNameList = new ArrayList<>(); // 获取已选择部署的服务名单

        if (allSelectedServices != null) { // 当已选择的服务列表非空时
            for (SelectedService selectedService : allSelectedServices) {
                allSelectedServicesNameList.add(selectedService.getServiceName());
            }

            List<Map<String, Object>> deployComputeList = new ArrayList<>(); // 当前需要计算的节点列表
            Map<String, Object> serviceUnit; // 计算节点列表中各个节点当前的计算信息，包含 serviceName 和当前出度 outDegree
            int outDegree = 0; // 节点出度（当前阶段依赖的服务数量）
            int deployed = 0;  // 部署序列中已经确定的部署服务数量
            List<String> currentStageRemovedNodes; // 当前阶段计算后移除的出度为 0 的节点

            // 1. 利用所有已选择部署的服务信息，初始化出度计算列表
            for (SelectedService selectedService : allSelectedServices) { // 对于当前已经选择部署的每个服务 selectedService
                ServiceNode selectedServiceNode = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(selectedService.getServiceName(), selectedService.getVersion()); // 获取该服务对象的 PO (包含端点及依赖信息)
                serviceUnit = new HashMap<>(); // 每个已选择部署的服务均初始化计算列表中的一个计算单元
                List<Map<String, Object>> dependencies = selectedServiceNode.getDependencies(); // 获取当前选择节点的依赖信息用于初始化出度
                if (dependencies != null) {
                    // 依赖列表存在，出度即为其容量，这一步需要判断非空再操作，否则异常
                    outDegree = dependencies.size();
                    for (Map<String, Object> dependency : dependencies) { // 增加此处代码，支持当服务依赖不完整时根据局部依赖生成
                        String serviceName = (String) dependency.get("serviceName");
                        if (!allSelectedServicesNameList.contains(serviceName)) { // 如果依赖的服务并不在已经选择的服务中，出度减1 TODO: 是否影响能否支持局部依赖生成
                            outDegree--;
                        }
                    }
                } else {
                    // 依赖列表为 null，出度为 0
                    outDegree = 0;
                }

                // 计算单元初始化其各个计算数据
                serviceUnit.put("serviceName", selectedService.getServiceName()); // 服务名
                serviceUnit.put("version", selectedService.getVersion()); // 服务版本
                serviceUnit.put("outDegree", outDegree); // 初始化出度
                serviceUnit.put("status", 0); // 0 未安排部署 1 已安排
                deployComputeList.add(serviceUnit); // 计算节点列表中添加当前服务节点的初始计算单元
            }

            // 2. 部署序列迭代计算阶段
            while (deployed != allSelectedServices.size()) { // 当部署序列中已经确定的数量不等于选择部署的服务总数量时循环

                currentStageRemovedNodes = new ArrayList<>(); // 初始化当前阶段移除的出度为 0 的点列表

                // 2-1 部署序列更新
                for (Map<String, Object> currentNode : deployComputeList) { // 对于计算节点列表中的每一个计算节点
                    outDegree = (int) currentNode.get("outDegree"); // 得到该计算节点的当前出度
                    int status = (int) currentNode.get("status"); // 检查其是否已经添加进部署序列
                    if (status != 1) { // 若未添加进部署序列则进行计算
                        if (outDegree == 0) { // 当发现出度为 0 而且仍未添加进序列的节点时
                            String serviceName = (String) currentNode.get("serviceName"); // 获取其服务名
                            String version = (String) currentNode.get("version"); // 获取其版本
                            currentStageRemovedNodes.add((String) currentNode.get("serviceName")); // 将其添加进本轮移除的名单中

                            deployNode = new HashMap<>(); // 初始化一个部署序列节点信息
                            deployNode.put("serviceName", serviceName); // 添加部署序列节点服务名
                            deployNode.put("version", version); // 添加部署序列节点版本
                            deployList.add(deployed++, deployNode); // 部署序列添加此节点，并将已经添加进部署序列的服务数量计数器自增更新

                            currentNode.put("status", 1); // 标记状态已经添加进部署列表
                        }
                    }
                }

                // 2-2 计算列表更新
                for (Map<String, Object> currentNode : deployComputeList) { // 对于部署序列中的每一个计算节点，更新出度，只有依赖中包含上一轮移除的点的节点出度才会变化
                    String serviceName = (String) currentNode.get("serviceName"); // 获取当前计算节点的服务名
                    String version = (String) currentNode.get("version"); // 获取当前计算节点的版本
                    ServiceNode serviceFound = serviceNodeRepository.findServiceNodeByServiceNameAndVersion(serviceName, version); // 对应查找到当前计算节点映射服务对应的 PO，以得到具体的服务依赖信息
                    List<Map<String, Object>> dependencies = serviceFound.getDependencies(); // 获取服务依赖列表

                    if (dependencies != null) { // 当服务依赖不为空时，即不是 BaseService 时
                        for (Map<String, Object> dependency : dependencies) { // 对于依赖列表中的每一项依赖
                            String dependentServiceName = (String) dependency.get("serviceName"); // 获取该项依赖所依赖的服务名
                            if (currentStageRemovedNodes.contains(dependentServiceName)) { // 若依赖的服务中包含了本轮移除的节点服务
                                outDegree = (int) currentNode.get("outDegree"); // 首先获取该计算节点的出度信息
                                currentNode.put("outDegree", outDegree - 1); // 更新该计算节点数据，将出度 -1
                            }
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

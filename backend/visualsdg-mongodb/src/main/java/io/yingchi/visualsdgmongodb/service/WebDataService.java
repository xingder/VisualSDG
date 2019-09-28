package io.yingchi.visualsdgmongodb.service;

import io.yingchi.visualsdgmongodb.domain.VO.SelectedService;

import java.util.List;
import java.util.Map;

public interface WebDataService {

    public List<Map<String, Object>> getServiceTableData();  // 获取 service 表格数据

    public List<List<Map<String, Object>>> getCascaderOptionsData();  // 获取级联选择数据集合

    public List<Map<String, Object>> getGraphNodesData(String tenantName);  // 获取指定 tenant 的 nodes 数据用于 ECharts Graph

    public List<Map<String, Object>> getGraphLinksData(String tenantName);  // 获取指定 tenant 的 links 据用于 ECharts Graph

    public List<Map<String, Object>> getDeployList(List<SelectedService> allSelectedServices); // 获取部署顺序列表

    List<Boolean> getSelectedServicesMutiversionFlags(); // 获取服务多版本标记
}

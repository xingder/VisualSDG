package io.yingchi.visualsdgmongodb.service;

import java.util.List;
import java.util.Map;

public interface WebDataService {

    public List<Map<String, Object>> getServiceTableData();  // 获取 service 表格数据

    public List<Map<String, Object>> getGraphNodesData();  // 获取 nodes 数据用于 ECharts Graph

    public List<Map<String, Object>> getGraphLinksData();  // 获取 links 据用于 ECharts Graph
}

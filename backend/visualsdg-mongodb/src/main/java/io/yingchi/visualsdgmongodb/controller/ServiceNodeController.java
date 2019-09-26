package io.yingchi.visualsdgmongodb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.yingchi.visualsdgmongodb.domain.VO.ResultYamlObject;
import io.yingchi.visualsdgmongodb.domain.PO.SelectedService;
import io.yingchi.visualsdgmongodb.domain.PO.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.SelectedServiceService;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import io.yingchi.visualsdgmongodb.service.WebDataService;
import io.yingchi.visualsdgmongodb.util.FileObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class ServiceNodeController {

    Logger logger = LoggerFactory.getLogger(this.getClass());;

    @Autowired
    ServiceNodeService serviceNodeService;

    @Autowired
    WebDataService webDataService;

    @Autowired
    SelectedServiceRepository selectedServiceRepository;

    @Autowired
    ServiceNodeRepository serviceNodeRepository;

    @Autowired
    SelectedServiceService selectedServiceService;

    @PostMapping("/service")
    public void createGraph(HttpServletRequest request) throws IOException {

        String jsonString = FileObjectUtil.getJsonStringFromYamlFileRequest(request);
        ResultYamlObject resObject = JSON.parseObject(jsonString, ResultYamlObject.class);

        serviceNodeService.add(
                resObject.getServiceName(),
                resObject.getVersion(),
                resObject.getEndpoints(),
                resObject.getDependencies());

    }

    @GetMapping("/service")
    public List<ServiceNode> fetchAllServicesData() {
        return serviceNodeRepository.findAll();
    }

    @GetMapping("/serviceTable")
    public List<Map<String, Object>> fetchServicesTableData() {
        return webDataService.getServiceTableData();
    }

    @DeleteMapping("/service")
    public boolean deleteService(@RequestParam("serviceName") String deleteService,
                                 @RequestParam("versions") String deleteVersion) {
        long counterForDeletedServices = serviceNodeRepository.deleteServiceNodeByServiceNameAndVersion(deleteService, deleteVersion);
        return counterForDeletedServices != 0;
    }

    @DeleteMapping("/services")
    public void deleteAllService() {
        serviceNodeRepository.deleteAll();
    }

    @GetMapping("/cascaders")
    public List<List<Map<String, Object>>> fetchCascaderOptionsData() {
        return webDataService.getCascaderOptionsData();
    }

    @PostMapping("/selectedService")
    public void receiveSelectedService(@RequestBody Object o) {
        selectedServiceRepository.deleteAll(); // 首先清空之前的已选择服务
        String jsonString = JSON.toJSONString(o);
        System.out.println(jsonString);
        List<SelectedService> selectedServices = JSONArray.parseArray(jsonString, SelectedService.class);
        selectedServiceRepository.saveAll(selectedServices);

    }

    @GetMapping("/selectedService")
    public List<SelectedService> fetchSelectedServices() {
        return selectedServiceRepository.findAll();
    }

    @GetMapping("/nodes")
    public List<Map<String, Object>> fetchAllGraphNodesData() {

        return webDataService.getGraphNodesData();
    }

    @GetMapping("/links")
    public List<Map<String, Object>> fetchAllGraphLinksData() {
        return webDataService.getGraphLinksData();
    }

    @GetMapping("/serviceVersionChangeCheck")
    public Map<String, Object> serviceVersionChangeCheck(@RequestParam("serviceName") String serviceName,
                                                         @RequestParam("toVersion") String toVersion) {

        Map<String, Object> checkResult = serviceNodeService.serviceVersionChangeCheck(serviceName, toVersion);
        System.out.println(checkResult);
        return checkResult;
    }

    @GetMapping("/deployList")
    public List<Map<String, Object>> fetchDeployList() {
        return webDataService.getDeployList();
    }

    @GetMapping("/selectedServicesMutiversionFlags")
    public List<Boolean> fetchSelectedServicesMutiversionFlags() {
        return webDataService.getSelectedServicesMutiversionFlags();
    }

    @GetMapping("/SelectedServices/VersionChanged")
    public void executeSelectedServicesChange(@RequestParam("serviceName") String serviceName,
                                                 @RequestParam("toVersion") String toVersion) {
        selectedServiceService.executeSelectedServicesChange(serviceName, toVersion);

    }
}

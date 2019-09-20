package io.yingchi.visualsdgmongodb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.yingchi.visualsdgmongodb.entity.ResultYamlObject;
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

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    ServiceNodeService serviceNodeService;

    @Autowired
    WebDataService webDataService;

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
    public List<Map<String, Object>> fetchServicesTableData() {
        return webDataService.getServiceTableData();
    }

    @GetMapping("/cascaders")
    public List<List<Map<String, Object>>> fetchCascaderOptionsData() {
        return webDataService.getCascaderOptionsData();
    }

    @PostMapping("/selectedService")
    public void receiveSelectedService(@RequestBody Object o) {

        String jsonString = JSON.toJSONString(o);
        JSONArray jsonArray = JSONArray.parseArray(jsonString); // [{},{}]
    }
}

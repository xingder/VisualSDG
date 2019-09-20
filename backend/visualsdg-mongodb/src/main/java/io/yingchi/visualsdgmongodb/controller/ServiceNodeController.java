package io.yingchi.visualsdgmongodb.controller;

import com.alibaba.fastjson.JSON;
import io.yingchi.visualsdgmongodb.entity.ResultYamlObject;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import io.yingchi.visualsdgmongodb.util.FileObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
public class ServiceNodeController {

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    ServiceNodeService serviceNodeService;

    @PostMapping("/service")
    public void createGraph(HttpServletRequest request) throws IOException {

        String jsonString = FileObjectUtil.getJsonStringFromYamlFileRequest(request);
        ResultYamlObject resObject = JSON.parseObject(jsonString, ResultYamlObject.class);

//        logger.info("结果接收 Object 内容: " + resObject.toString());

        serviceNodeService.add(
                resObject.getServiceName(),
                resObject.getVersion(),
                resObject.getEndpoints(),
                resObject.getDependencies());

    }
}

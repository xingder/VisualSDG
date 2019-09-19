package io.yingchi.visualsdgmongodb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.yingchi.visualsdgmongodb.entity.ResultYamlObject;
import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
public class ServiceNodeController {

    @PostMapping("/service")
    public void createGraph(HttpServletRequest request) throws IOException {

        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");

        Yaml yaml = new Yaml();
        Object load = yaml.load(file.getInputStream());
        System.out.println(load);

        String s = JSON.toJSONString(load);
        System.out.println(s);

        ResultYamlObject resObject = JSON.parseObject(s, ResultYamlObject.class);


        System.out.println(resObject.toString());

        System.out.println("serviceName: " + resObject.getServiceName());
        System.out.println("version: " + resObject.getVersion());

//
//        System.out.println(jsonObject);
//
//        ServiceNode serviceNode = JSONObject.toJavaObject(jsonObject, ServiceNode.class);
//
//        System.out.println(serviceNode.toString());

    }
}

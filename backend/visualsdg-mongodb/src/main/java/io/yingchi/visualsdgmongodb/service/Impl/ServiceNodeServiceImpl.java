package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ServiceNodeServiceImpl implements ServiceNodeService {

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private ServiceNodeRepository serviceNodeRepository;

    @Override
    public void add(String serviceName, String version, List<String> endpoints, List<Map<String, Object>> dependencies) {


    }
}

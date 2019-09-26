package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.repository.TenantDeployRepository;
import io.yingchi.visualsdgmongodb.service.TenantDeployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantDeployServiceImpl implements TenantDeployService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TenantDeployRepository tenantDeployRepository;



}

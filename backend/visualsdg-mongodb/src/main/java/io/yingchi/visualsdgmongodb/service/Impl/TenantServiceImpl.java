package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.repository.TenantRepository;
import io.yingchi.visualsdgmongodb.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceImpl implements TenantService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TenantRepository tenantRepository;



}

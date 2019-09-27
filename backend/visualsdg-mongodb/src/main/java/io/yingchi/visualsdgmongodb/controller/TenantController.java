package io.yingchi.visualsdgmongodb.controller;


import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import io.yingchi.visualsdgmongodb.domain.VO.Result;
import io.yingchi.visualsdgmongodb.service.Impl.TenantServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class TenantController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TenantServiceImpl tenantService;


    @PostMapping("/tenant/{tenantName}")
    public Result saveNewTenant(@PathVariable("tenantName") String tenantName) {
        return tenantService.createTenantByTenantName(tenantName);
    }


    @GetMapping("/tenant/{tenantName}")
    public Result findTenantByTenantName(@PathVariable("tenantName") String tenantName) {
        return tenantService.findTenantByTenantName(tenantName);
    }

    @GetMapping("/tenants")
    public Result getAllTenants() {
        return tenantService.getAllTenants();
    }

    @DeleteMapping("/tenant/{tenantName}")
    public Result deleteTenantByTenantName(@PathVariable("tenantName") String tenantName) {
        return tenantService.deleteTenantByTenantName(tenantName);
    }
}

package io.yingchi.visualsdgmongodb.service;

import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import io.yingchi.visualsdgmongodb.domain.VO.Result;

public interface TenantService {

    public Result save(Tenant newTenant);

    public Result findTenantByTenantName(String tenantName);

    public Result createTenantByTenantName(String tenantName);

    public Result getAllTenants();

    public Result deleteTenantByTenantName(String tenantName);


}

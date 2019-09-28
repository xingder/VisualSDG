package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import io.yingchi.visualsdgmongodb.domain.VO.Result;
import io.yingchi.visualsdgmongodb.repository.TenantRepository;
import io.yingchi.visualsdgmongodb.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TenantRepository tenantRepository;


    @Override
    public Result save(Tenant newTenant) {
        Tenant save = tenantRepository.save(newTenant);
        Result result = new Result<>();
        result.setMsg("新租户保存成功，租户id: " + save.get_id());
        result.setStatus(Result.STATUS_NO_ERROR);

        return result;
    }

    @Override
    public Result findTenantByTenantName(String tenantName) {
        Tenant tenantFound = tenantRepository.findTenantByTenantName(tenantName);
        if (tenantFound != null) {
            Result<Tenant> result = new Result<>();
            result.setStatus(Result.STATUS_NO_ERROR);
            result.setMsg("存在租户：" + tenantName);
            result.setData(tenantFound);
            return result;
        } else {
            Result result = new Result();
            result.setStatus(Result.STATUS_ERROR);
            result.setMsg("不存在租户：" + tenantName);
            return result;
        }
    }

    @Override
    public Result createTenantByTenantName(String tenantName) {
        Result result = new Result();
        Tenant tenantFound = tenantRepository.findTenantByTenantName(tenantName);
        if (tenantFound != null) {
            result.setStatus(Result.STATUS_ERROR);
            result.setMsg("已存在租户：" + tenantName + "，请勿重复添加租户");
            return result;
        } else {
            Tenant newTenant = new Tenant();
            newTenant.setTenantName(tenantName);
            Tenant save = tenantRepository.save(newTenant);
            result.setStatus(Result.STATUS_NO_ERROR);
            result.setMsg("创建租户：" + tenantName + " 成功，返回租户 id");
            result.setData(save.get_id());
            return result;
        }
    }

    @Override
    public Result getAllTenants() {

        List<Tenant> all = tenantRepository.findAll();
        if (all != null) {
            Result<List<Tenant>> result = new Result<>();
            result.setStatus(Result.STATUS_NO_ERROR);
            result.setMsg("获取到 " + all.size() + " 条租户数据");
            result.setData(all);
            return result;
        } else {
            Result result = new Result();
            result.setStatus(Result.STATUS_ERROR);
            result.setMsg("无租户数据，获取失败");
            return result;
        }
    }

    @Override
    public Result deleteTenantByTenantName(String tenantName) {
        Tenant tenantFound = tenantRepository.findTenantByTenantName(tenantName);

        if (tenantFound != null) {
            tenantRepository.deleteTenantByTenantName(tenantName);
            Result result = new Result();
            result.setStatus(Result.STATUS_NO_ERROR);
            result.setMsg("成功删除租户：" + tenantName);
            return result;
        } else {
            Result result = new Result();
            result.setStatus(Result.STATUS_ERROR);
            result.setMsg("租户：" + tenantName + " 不存在，删除失败");
            return result;
        }
    }

    @Override
    public Result update(Tenant tenant) {
        Tenant tenantFound = tenantRepository.findTenantByTenantName(tenant.getTenantName());
        if (tenantFound != null) {
            Result result = new Result();
            tenantFound.setDeployedServiceList(tenant.getDeployedServiceList());
            tenantRepository.save(tenantFound);
            result.setStatus(Result.STATUS_NO_ERROR);
            result.setMsg(tenant.getTenantName() + " 租户服务部署成功");
            return result;
        } else {
            Result result = new Result();
            result.setStatus(Result.STATUS_ERROR);
            result.setMsg("租户服务部署失败，未找到租户：" + tenant.getTenantName());
            return result;
        }
    }
}

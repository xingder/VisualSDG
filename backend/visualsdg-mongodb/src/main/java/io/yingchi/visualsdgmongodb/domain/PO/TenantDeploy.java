package io.yingchi.visualsdgmongodb.domain.PO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 租户部署服务信息（租户名，部署列表）
 */
@Document(collection = "tenant_deploy")
public class TenantDeploy {

    @Id
    private String _id;
    private String tenantName;
    private List<SelectedService> tenantDeployedServices;

    public TenantDeploy(String tenantName, List<SelectedService> tenantDeployedServices) {
        this.tenantName = tenantName;
        this.tenantDeployedServices = tenantDeployedServices;
    }

    public TenantDeploy() {
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<SelectedService> getTenantDeployedServices() {
        return tenantDeployedServices;
    }

    public void setTenantDeployedServices(List<SelectedService> tenantDeployedServices) {
        this.tenantDeployedServices = tenantDeployedServices;
    }
}

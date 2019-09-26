package io.yingchi.visualsdgmongodb.domain.PO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 租户部署服务信息（租户名，部署列表）
 */
@Document(collection = "tenant")
public class Tenant {

    @Id
    private String _id;
    private String tenantName;
    private List<SelectedService> deployedServiceList;

    public Tenant(String tenantName, List<SelectedService> deployedServiceList) {
        this.tenantName = tenantName;
        this.deployedServiceList = deployedServiceList;
    }

    public Tenant() {
    }

    public String get_id() {
        return _id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<SelectedService> getDeployedServiceList() {
        return deployedServiceList;
    }

    public void setDeployedServiceList(List<SelectedService> deployedServiceList) {
        this.deployedServiceList = deployedServiceList;
    }
}

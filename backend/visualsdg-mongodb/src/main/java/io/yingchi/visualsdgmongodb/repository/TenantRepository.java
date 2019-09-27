package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {

    public Tenant findTenantByTenantName(String tenantName);

    public void deleteTenantByTenantName(String tenantName);
}

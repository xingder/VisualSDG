package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.domain.PO.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TenantRepository extends MongoRepository<Tenant, String> {

}

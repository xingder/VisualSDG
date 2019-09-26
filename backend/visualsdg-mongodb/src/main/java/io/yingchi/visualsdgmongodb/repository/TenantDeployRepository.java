package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.domain.PO.TenantDeploy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TenantDeployRepository extends MongoRepository<TenantDeploy, String> {

}

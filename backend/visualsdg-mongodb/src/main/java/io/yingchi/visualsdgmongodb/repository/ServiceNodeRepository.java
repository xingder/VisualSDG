package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.domain.PO.ServiceNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceNodeRepository extends MongoRepository<ServiceNode, String> {

    public List<ServiceNode> findServiceNodesByServiceName(String serviceName);

    public ServiceNode findServiceNodeByServiceNameAndVersion(String serviceName, String version);

    public long deleteServiceNodeByServiceNameAndVersion(String deleteService, String deleteVersion);

}

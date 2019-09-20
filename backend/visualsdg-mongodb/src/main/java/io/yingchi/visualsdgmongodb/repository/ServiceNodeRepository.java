package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceNodeRepository extends MongoRepository<ServiceNode, String> {

    public List<ServiceNode> findServiceNodesByServiceName(String serviceName);
}

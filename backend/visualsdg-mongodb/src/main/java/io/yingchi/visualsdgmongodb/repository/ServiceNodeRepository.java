package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceNodeRepository extends MongoRepository<ServiceNode, String> {

    public List<ServiceNode> findServiceNodesByServiceName(String serviceName);

}

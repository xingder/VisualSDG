package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceNodeRepository extends MongoRepository<ServiceNode, String> {

}

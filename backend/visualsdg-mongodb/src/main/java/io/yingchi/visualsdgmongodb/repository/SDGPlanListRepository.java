package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.entity.SDGPlanList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SDGPlanListRepository extends MongoRepository<SDGPlanList, String> {

}

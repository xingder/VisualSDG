package io.yingchi.visualsdgmongodb.repository;

import io.yingchi.visualsdgmongodb.domain.VO.SelectedService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SelectedServiceRepository extends MongoRepository<SelectedService, String> {
    public SelectedService findSelectedServiceByServiceName(String serviceName);

    public void deleteSelectedServiceByServiceName(String serviceName);
}

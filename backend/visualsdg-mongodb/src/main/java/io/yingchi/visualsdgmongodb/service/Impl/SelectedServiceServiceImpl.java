package io.yingchi.visualsdgmongodb.service.Impl;

import io.yingchi.visualsdgmongodb.entity.SelectedService;
import io.yingchi.visualsdgmongodb.repository.SelectedServiceRepository;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.SelectedServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectedServiceServiceImpl implements SelectedServiceService {

    @Autowired
    SelectedServiceRepository selectedServiceRepository;

    @Autowired
    ServiceNodeRepository serviceNodeRepository;

    @Override
    public void executeSelectedServicesChange(String serviceName, String toVersion) {
        selectedServiceRepository.deleteSelectedServiceByServiceName(serviceName);
        SelectedService newSelectedService = new SelectedService(serviceName, toVersion);
        selectedServiceRepository.save(newSelectedService);
    }
}

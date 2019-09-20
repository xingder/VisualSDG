package io.yingchi.visualsdgmongodb;

import io.yingchi.visualsdgmongodb.entity.ServiceNode;
import io.yingchi.visualsdgmongodb.repository.ServiceNodeRepository;
import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import io.yingchi.visualsdgmongodb.service.WebDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VisualsdgMongodbApplicationTests {

    @Autowired
    ServiceNodeService serviceNodeService;

    @Autowired
    ServiceNodeRepository serviceNodeRepository;

    @Autowired
    WebDataService webDataService;

    /**
     * 测试添加 ServiceNode
     */
    @Test
    public void serviceNodeDeleteTest() {
        serviceNodeService.delete("ServiceA","v1.0");
    }

    /**
     * 测试获取 Service 表格数据
     */
    @Test
    public void getServiceTableDataTest() {
        List<Map<String, Object>> serviceTableData = webDataService.getServiceTableData();
        serviceTableData.forEach(x -> System.out.println(x));
    }

    @Test
    public void getAllExistingServiceNameListTest() {
        List<String> allExistingServiceNameList = serviceNodeService.getAllExistingServiceNameList();
        allExistingServiceNameList.forEach(x -> System.out.println(x));
    }

}

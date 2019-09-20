package io.yingchi.visualsdgmongodb;

import io.yingchi.visualsdgmongodb.service.ServiceNodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VisualsdgMongodbApplicationTests {

    @Autowired
    ServiceNodeService serviceNodeService;

    @Test
    public void serviceNodeDeleteTest() {
        serviceNodeService.delete("ServiceA","v1.0");
    }

}

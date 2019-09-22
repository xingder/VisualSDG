package io.yingchi.visualsdgmongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "SDGPlanList")
public class SDGPlanList {

    @Id
    private String _id;
    private String name;
    private List<SelectedService> planList;


    public SDGPlanList(String name, List<SelectedService> planList) {
        this.name = name;
        this.planList = planList;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public List<SelectedService> getPlanList() {
        return planList;
    }
}

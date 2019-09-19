package io.yingchi.visualsdgspringboot.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class Service {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String version;

    @Relationship(type = "ENDPOINT", direction = Relationship.INCOMING)
    private Set<Endpoint> endpoints;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }
}

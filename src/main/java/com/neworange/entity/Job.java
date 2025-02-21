package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 18:47
 * @description
 */
@Data
@Node("Job")
public class Job {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Property
    private String type;

    @Relationship("dep")
    private List<TableRelationship> tables;

    @Relationship("dep")
    private List<JobRelationship> jobs;

}

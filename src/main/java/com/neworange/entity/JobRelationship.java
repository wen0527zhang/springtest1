package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 18:48
 * @description
 */
@Data
@RelationshipProperties
public class JobRelationship {
    @Id
    @GeneratedValue
    private Long id;
//标注的属性是关系中的目标节点
    @TargetNode
    private Job job;
}

package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 18:49
 * @description
 */
@Data
//标注的类为关系实体
@RelationshipProperties
public class TableRelationship {
    @Id
    @GeneratedValue
    private Long id;
    //标注的属性是关系中的目标节点
    @TargetNode
    private Table table;
}

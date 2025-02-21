package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 15:35
 * @description
 */
@RelationshipProperties
@Data
public class Knows {
    @RelationshipId
    private Long id;
    @TargetNode
    private Person personNode;

}

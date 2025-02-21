package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 17:48
 * @description
 */

@Data
//关系实体  必须有起始节点和结束节点 标注的类为关系实体
@RelationshipProperties
public class RelationShip {
    @RelationshipId
    private Long id;
    @TargetNode
    @Relationship(type = "parent", direction =INCOMING )
    private Person parent;
    @TargetNode
    @Relationship(type = "child", direction = OUTGOING)
    private  Person child;




    public RelationShip() {

    }


}

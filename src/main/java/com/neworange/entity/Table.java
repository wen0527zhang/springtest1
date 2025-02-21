package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 18:47
 * @description
 */
@Data
@Node
public class Table {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String type;
    //标注的属性为关系属性，值为关系的标签值，另一个值为关系的指向，默认从当前节点向外指向，类似关系型数据库的关系；
    @Relationship("dep")
    private List<Job> jobs;

}

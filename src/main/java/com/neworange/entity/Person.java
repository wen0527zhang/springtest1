package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 17:43
 * @description
 */
@Data
@Node
public class Person {

    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String name;
    @Property
    private String age;
    @Property
    private String sex;

    public Person() {

    }

    public Person(String name, String age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    @Relationship(type = "KNOWS", direction = OUTGOING)
    private List<Knows> knows;

    private RelationShip parent;
    private RelationShip child;


}

package com.neworange.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 15:00
 * @description
 */
@Node("Movie")
@Data
public class MovieEntity {
    @Id @GeneratedValue
    private Long id;
    private final String title;

    @Property("tagline")
    private final String description;



    public MovieEntity(String title, String description) {
        this.id = null;
        this.title = title;
        this.description = description;
    }

    public MovieEntity withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            MovieEntity newObject = new MovieEntity(this.title, this.description);
            newObject.id = id;
            return newObject;
        }
    }
}

package com.neworange.controller;

import com.neworange.entity.RelationShip;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 17:52
 * @description
 */
@Repository
public interface RelationShipRepository extends Neo4jRepository<RelationShip, Long> {
}

package com.neworange.controller;

import com.neworange.entity.Knows;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 15:39
 * @description
 */
public interface KnowsRepository  extends Neo4jRepository<Knows, Long> {
}

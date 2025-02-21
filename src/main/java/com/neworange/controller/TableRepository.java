package com.neworange.controller;

import com.neworange.entity.Table;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 18:54
 * @description
 */
@Repository
public interface TableRepository extends Neo4jRepository<Table, Long> {
}

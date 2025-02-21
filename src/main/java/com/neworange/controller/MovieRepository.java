package com.neworange.controller;

import com.neworange.entity.MovieEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 15:03
 * @description
 */
public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, Long> {
    Mono<MovieEntity> findOneByTitle(String title);
}

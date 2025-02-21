package com.neworange.controller;

import com.neworange.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 17:54
 * @description
 */
@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    //一对一手动指定关系
//    @Query("match (n:person {name:{0}}),(m:person {name:{2}})"+
//            "create (n)-[:动漫人物关系{relation:{1}}]->(m)")
//    void createRelation(String from,String relation, String to);

    //根据关系数据进行当前用户的所有关系生成
//    @Query("match (n:person {name:{0}}),(m:dmRelation),(s:person) where m.from={0} and s.name=m.to create(n)-[:动漫人物关系 {relation:m.relation}]->(s)")
//    void createRelationByName(String fromName);

    //根据关系数据进行当前用户的所有关系生成
//    @Query("CALL db.relationshipTypes()")
//    List<String> getAllRealationTypes();

//    //修改
//    @Query("MATCH (n) WHERE id(n) = :#{#person.id} SET n.name = :#{#person.name},n.age = :#{#person.age},n.sex = :#{#person.sex} RETURN n")
//    Person updateById(@Param("person") Person person);

//    @Query("match (n:person {name:{name}})-[r:`动漫人物关系`]->(m:person) where r.relation={relation} return m")
//    List<Person> getRelationsByName(@Param("name")String name,@Param("relation")String relation);
//
//    @Query("MATCH (n:person {name:'冯宝宝'}) RETURN n")
//    Person getTest();

}

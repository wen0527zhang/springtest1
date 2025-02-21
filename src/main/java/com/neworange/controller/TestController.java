package com.neworange.controller;

import com.neworange.entity.Knows;
import com.neworange.entity.Person;
import com.neworange.entity.RelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/15 17:42
 * @Description
 */
@RestController("/test")
public class TestController {
    @Autowired
    private PersonRepository persionRepository;

    @Autowired
    private RelationShipRepository relationShipRepository;

    @GetMapping("/create")
    public void create(){
        Person ceo =new Person();
        ceo.setId(1L);
        ceo.setName("CEO");
        Person dept1 =new Person();
        ceo.setId(2L);
        dept1.setName("设计部");
        Person dept11 =new Person();
        ceo.setId(3L);
        dept11.setName("设计1组");
        Person dept12 =new Person();
        ceo.setId(4L);
        dept12.setName("设计2组");

        Person dept2 =new Person();
        ceo.setId(5L);
        dept2.setName("技术部");
        Person dept21 =new Person();
        ceo.setId(6L);
        dept21.setName("前端技术部");
        Person dept22 =new Person();
        dept22.setId(7L);
        dept22.setName("后端技术部");

        List<Person> depts = new ArrayList<>(Arrays.asList(ceo,dept1,dept11,dept12,dept2,dept21,dept22));
        persionRepository.saveAll(depts);

        RelationShip r1 = new RelationShip();
        r1.setChild(dept1);
        RelationShip r2 = new RelationShip();
        r2.setChild(dept2);

        ceo.setChild(r1);
        persionRepository.save(ceo);
        ceo.setChild(r2);
        persionRepository.save(ceo);

//        List<RelationShip> l1p = new ArrayList<>();
//        RelationShip r11 = new RelationShip();
//        r11.setParent(ceo);
//        l1p.add(r11);
//        RelationShip r12 = new RelationShip();
//        r12.setChild(dept11);
//        RelationShip r13 = new RelationShip();
//        r13.setChild(dept12);
//        l1p.add(r12);
//        l1p.add(r13);
//        dept1.setRelationShips(l1p);
//        persionRepository.save(dept1);
//
//        List<RelationShip> l2c = new ArrayList<>();
//        l2c.add(r11);
//        RelationShip r21 = new RelationShip();
//        r21.setChild(dept21);
//        RelationShip r22 = new RelationShip();
//        r22.setChild(dept22);
//        l2c.add(r21);
//        l2c.add(r22);
//        dept2.setRelationShips(l2c);
//
//        persionRepository.save(dept2);


        //ceo.setParent(ceo);
//        ceo.setChild(dept2);
//        persionRepository.save(ceo);
//        RelationShip relationShip3 = new  RelationShip();
//        relationShip3.setParent(dept1);
//        relationShip3.setChild(dept11);
//        dept1.setParent(ceo);
//        dept1.setChild(dept11);
//        persionRepository.save(dept1);
//        dept1.setParent(ceo);
//        dept1.setChild(dept12);
//        persionRepository.save(dept1);
//        RelationShip relationShip4 = new  RelationShip();
//        relationShip4.setParent(dept1);
//        relationShip4.setChild(dept12);
//        dept11.setParent(dept1);
//
//        dept12.setParent(dept1);
//        persionRepository.save(dept11);
//        persionRepository.save(dept12);
//        RelationShip relationShip5 =new  RelationShip();
//        relationShip5.setParent(dept2);
//        relationShip5.setChild(dept21);
//        dept2.setParent(ceo);
//        dept2.setChild(dept21);
//        persionRepository.save(dept2);
//        dept2.setParent(ceo);
//        dept2.setChild(dept22);
//        persionRepository.save(dept2);
//        RelationShip relationShip6 =new  RelationShip();
//        relationShip6.setParent(dept2);
//        relationShip6.setChild(dept22);
//        dept21.setParent(dept2);
//        dept22.setParent(dept2);
//        persionRepository.save(dept21);
//        persionRepository.save(dept22);
//        List<RelationShip> relationShips = new ArrayList<>(Arrays.asList(relationShip1,relationShip2,relationShip3,relationShip4,relationShip5
//                ,relationShip6));
//        List<RelationShip> relationShips = new ArrayList<>(Arrays.asList(relationShip1,relationShip2,relationShip3,relationShip4,relationShip5
//                ,relationShip6));
//         relationShipRepository.saveAll(relationShips);
        System.out.println("-----");
    }


    @GetMapping("create1")
    public void create1(){
        Person ceo =new Person();
        ceo.setId(0L);
        ceo.setName("CEO");
        ceo.setSex("男");
        ceo.setAge("90");
        Person dept1 =new Person();
        dept1.setName("设计部");
        dept1.setSex("男");
        dept1.setAge("10");
        Person dept11 =new Person();
        dept11.setName("设计1组");
        dept11.setSex("男");
        dept11.setAge("11");
        Person dept12 =new Person();
        dept12.setName("设计2组");
        dept12.setSex("男");
        dept12.setAge("12");

        Person dept2 =new Person();
        dept2.setName("技术部");
        dept2.setSex("女");
        dept2.setAge("24");
        Person dept21 =new Person();
        dept21.setName("前端技术部");
        dept21.setSex("女");
        dept21.setAge("21");
        Person dept22 =new Person();
        dept22.setName("后端技术部");
        dept22.setSex("女");
        dept22.setAge("20");
        List<Person> depts = new ArrayList<>(Arrays.asList(ceo,dept1,dept11,dept12,
                dept2,dept21,dept22));
       // persionRepository.saveAll(depts);
        // 创建关系并设置目标节点
        Knows knowsRelationship = new Knows();
        knowsRelationship.setPersonNode(dept2); // 设置关系的目标节点
//        if (ceo.getKnows() == null) {
//            ceo.setKnows(new ArrayList<>());
//        }
//        ceo.getKnows().add(knowsRelationship);
        persionRepository.save(ceo);

//        Knows knows1 = new Knows();
//        knows1.setPersonNode(dept1); // 设置关系的目标节点
//        if (ceo.getKnows() == null) {
//            ceo.setKnows(new ArrayList<>());
//        }
//        ceo.getKnows().add(knows1);
//        persionRepository.save(ceo);

        System.out.println("-----");
    }

    @GetMapping("get")
    public RelationShip get(Long id){
        Optional<RelationShip> byId = relationShipRepository.findById(id);
        return byId.orElse(null);
    }

    @GetMapping("deleteRelationShip")
    public void deleteRelationShip(Long id){
        relationShipRepository.deleteById(id);
    }

    @GetMapping("deleteDept")
    public void deleteDept(Long id){
        persionRepository.deleteById(id);
    }

    @GetMapping("deleteAll")
    public void deleteAll(){
        persionRepository.deleteAll();
        relationShipRepository.deleteAll();
    }
    

    @GetMapping("addRelation")
    public void addRelation(){
        Optional<Person> byId = persionRepository.findById(0L);
        Person ceo = byId.orElse(null);
        Optional<Person> byIds = persionRepository.findById(6L);
        Person dept2 = byIds.orElse(null);
        // 创建关系并设置目标节点
        Knows knowsRelationship = new Knows();
        knowsRelationship.setPersonNode(dept2); // 设置关系的目标节点
        if (ceo.getKnows() == null) {
            ceo.setKnows(new ArrayList<>());
        }else {
            ceo.getKnows().add(knowsRelationship);
        }

        persionRepository.save(ceo);

//        Knows knows1 = new Knows();
//        knows1.setPersonNode(dept1); // 设置关系的目标节点
//        if (ceo.getKnows() == null) {
//            ceo.setKnows(new ArrayList<>());
//        }
//        ceo.getKnows().add(knows1);
//        persionRepository.save(ceo);

        System.out.println("-----");
    }

}

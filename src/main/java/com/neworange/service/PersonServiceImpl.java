package com.neworange.service;

import com.neworange.controller.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/22 16:24
 * @description
 */
@Service
public class PersonServiceImpl implements PersonService{
    @Autowired
    private PersonRepository personRepository;
}

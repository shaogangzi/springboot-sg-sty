package com.sg.springbootsgsty.controller;

import com.sg.springbootsgsty.bean.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@EnableConfigurationProperties({Person.class})
public class HWController {

    @Autowired
    Person person;

    @RequestMapping("hello")
    public String sayHelloWorld(){
      return "name="+person.getName()+" ,age="+person.getAge();
    }


}

package com.sg.springbootsgsty.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HWController {


    @RequestMapping("hello")
    public String sayHelloWorld(){
       log.info("helloworld还是开始调用!!");
       return "hello world";
    }


}

package com.sg.springbootsgsty.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "person")
@Data
public class Person {

    public int age;

    public String name;



}

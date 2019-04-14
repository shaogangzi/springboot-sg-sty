package com.sg.springbootsgsty.controller;

import com.sg.springbootsgsty.dao.UserDao;
import com.sg.springbootsgsty.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    public UserDao userDao;


    @RequestMapping("lists")
    public List<User> list(){
        return userDao.findAll();
    }

    @GetMapping("list/{id}")
    public User listByid(@PathVariable("id") Integer id){
        return userDao.findById(id).get();
    }

    @PostMapping(value = "add")
    public List<User> add(User user){
        User save = userDao.save(user);
        return userDao.findAll();
    }



}

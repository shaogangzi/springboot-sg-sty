package com.sg.springbootsgsty.controller;

import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @RequestMapping("/{id}")
    public User getUser(@PathVariable Long id){
        return userService.selectUserById(id);
    }

    @RequestMapping("/list")
    public List<User> listUsers(User user){
        return userService.selectUsersByCondition(user);
    }

    @RequestMapping("/add")
    public int addUser(User user){
        return userService.addUser(user);
    }







}

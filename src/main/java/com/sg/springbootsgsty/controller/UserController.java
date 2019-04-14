package com.sg.springbootsgsty.controller;

import com.sg.springbootsgsty.dao.UserDao;
import com.sg.springbootsgsty.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/")
@Slf4j
@Api(value = "用户服务类")
public class UserController {

    @Autowired
    public UserDao userDao;


    @RequestMapping("lists")
    @ApiOperation(value = "获取所有的用户列表",notes = "获取所有的用户列表")
    public List<User> list(){
        return userDao.findAll();
    }

    @GetMapping("list/{id}")
    @ApiOperation(value = "通过id来查询用户",notes = "通过id来查询用户")
    @ApiImplicitParam(name="id" ,value = "id" ,required = true,dataType = "Integer")
    public User listByid(@PathVariable("id") Integer id){
        return userDao.findById(id).get();
    }

    @PostMapping(value = "add")
    @ApiImplicitParam(name="user" ,value = "user" ,required = true,dataType = "User")
    public List<User> add(User user){
        User save = userDao.save(user);
        return userDao.findAll();
    }



}

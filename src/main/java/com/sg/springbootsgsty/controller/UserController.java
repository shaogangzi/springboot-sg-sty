package com.sg.springbootsgsty.controller;

import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    //http://localhost:port/name/swagger-ui.html

    @Autowired
    UserService userService;


    @GetMapping(value="/{id}")
    @ApiOperation(value = "通过Id查询用户" ,notes = "查询用户")
    @ApiImplicitParam(name = "id" ,value = "用户id",required = true,dataType = "Long",paramType ="path")
    public User getUser(@PathVariable Long id){
        System.out.println("---------"+id);
        return userService.selectUserById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "批量查询用户",notes = "更加条件查询用户")
    @ApiImplicitParam(name = "user",value = "用户信息",required = true,dataType = "User")
    public List<User> listUsers(@RequestBody User user){
        return userService.selectUsersByCondition(user);
    }

    @PutMapping("/add")
    @ApiOperation(value = "新增用户",notes = "新增用户呀")
    @ApiImplicitParam(name = "userinfo",value = "新增的用户信息",required = true,dataType = "User")
    public int addUser(@RequestBody User userinfo){
        return userService.addUser(userinfo);
    }







}

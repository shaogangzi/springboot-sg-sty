package com.sg.springbootsgsty.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Collections2;
import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.dao.UserMapper;
import com.sg.springbootsgsty.service.UserService;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> selectUsersByCondition(User user) {
        PageHelper.startPage(2, 20);
        return userMapper.queryUsersByCondition(user);
    }

    @Override
    public int addUser(User user) {
        if(user==null){
            return -1;
        }
        return userMapper.addUser(user);
    }

    @Override
    public int addUsers(List<User> users) {
        if(users==null ||users.isEmpty()){
            return -1;
        }
        return userMapper.addUsers(users);
    }

    @Override
    public User selectUserById(Long id) {
        if(id<0){
            return null;
        }
        return userMapper.queryUserById(id);
    }


}

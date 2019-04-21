package com.sg.springbootsgsty.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.dao.UserMapper;
import com.sg.springbootsgsty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> queryUsersByCondition(User user) {
        //分页查询
        PageHelper.startPage(2, 20);
        return userMapper.queryUsersByCondition(user);

    }
}

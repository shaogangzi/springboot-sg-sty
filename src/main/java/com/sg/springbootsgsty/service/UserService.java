package com.sg.springbootsgsty.service;

import com.sg.springbootsgsty.bean.User;

import java.util.List;

public interface UserService {

    List<User> selectUsersByCondition(User user);

    int  addUser(User user);

    int  addUsers(List<User> users);

    User selectUserById(Long id);
}

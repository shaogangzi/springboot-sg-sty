package com.sg.springbootsgsty.service;

import com.sg.springbootsgsty.bean.User;

import java.util.List;

public interface UserService {

    List<User> queryUsersByCondition(User user);
}

package com.sg.springbootsgsty.dao;

import com.sg.springbootsgsty.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    int  addUser(User user);

    int  addUsers(List<User> users);

    int deleteUserById(Long id);

    int deleteUserByCondition(User user);

    User queryUserById(Long id);

    User queryUserByCondition(User user);

    List<User> queryUsersByCondition(User user);

    int updateUserById(Long id);
}

package com.sg.springbootsgsty.dao;

import com.sg.springbootsgsty.bean.User;

public interface UserMapper {
    User queryById(Long id);
}

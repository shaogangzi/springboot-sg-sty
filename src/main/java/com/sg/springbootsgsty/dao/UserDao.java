package com.sg.springbootsgsty.dao;

import com.sg.springbootsgsty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDao extends JpaRepository<User,Integer>{
}

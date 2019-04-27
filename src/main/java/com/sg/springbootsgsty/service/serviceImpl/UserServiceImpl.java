package com.sg.springbootsgsty.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Collections2;
import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.dao.UserMapper;
import com.sg.springbootsgsty.service.UserService;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class ,propagation = Propagation.REQUIRED)
    public int addUser(User user) {

        if(user==null){
                return -1;
            }
            test();
            test2();
            test3();

            return 0;
    }

    public void test2() {
        User user1 =new User().setAge(18).setName("张三").setUserCode("ASD");
        userMapper.addUser(user1);
        System.out.println("============插入了一条记录=========");


        test55();





    }

    public void test55() {

        User user2 =new User().setAge(19).setName("张四").setUserCode("CSD");
        userMapper.addUser(user2);


        System.out.println("插入了第二条记录========"+user2);




        User user3 =new User().setAge(19).setName("张四").setUserCode("CSD");
        userMapper.addUser(user3);








    }

    private void test() {
        System.out.println("=========START===========");
    }
    private void test3() {
        System.out.println("=========FINISH===========");
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

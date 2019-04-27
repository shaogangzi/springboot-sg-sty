package com.sg.springbootsgsty;

import com.alibaba.fastjson.JSON;
import com.sg.springbootsgsty.bean.User;
import com.sg.springbootsgsty.dao.UserMapper;
import com.sg.springbootsgsty.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootSgStyApplicationTests {

	@Autowired
    UserService userService;

	@Test
	public void contextLoads() {
	    User user =new User();
	    user.setAge(18);
		int i = userService.addUser(user);
		System.out.println(i);
    }

}

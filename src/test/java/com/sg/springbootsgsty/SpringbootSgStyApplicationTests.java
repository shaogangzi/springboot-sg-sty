package com.sg.springbootsgsty;

import com.sg.springbootsgsty.entity.User;
import com.sg.springbootsgsty.util.httpUtil.HttpClientUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootSgStyApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		HashMap hashMap =new HashMap();
		hashMap.put("age","18");
		hashMap.put("name","18");
		hashMap.put("createTime","2019-04-15 16:55:23");
		hashMap.put("updateTime","2019-04-15 16:55:2");
        String s = HttpClientUtils.postParameters("http://192.168.253.1:8083/user/add", hashMap);
        System.out.println(s);
	}

}

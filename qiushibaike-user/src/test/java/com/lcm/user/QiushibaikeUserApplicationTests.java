package com.lcm.user;

import com.lcm.api.service.UmsMemberService;
import com.lcm.util.MD5Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QiushibaikeUserApplicationTests {

    @Autowired
    private UmsMemberService umsMemberService;

    @Test
    void contextLoads() {

        System.out.println(MD5Utils.md5("123456"));
    }

}

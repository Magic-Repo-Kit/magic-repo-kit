package com.magicrepokit.auth;

import com.magicrepokit.Po.User;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ComponentScan("com.magicrepokit")
public class testRedis {
    private final String REDIS_TEST_PREFIX = "mrk:test:";

    @Autowired
    private MRKRedisUtils redisUtils;

    @Test
    public void testSetObject(){
        User user = new User();
        user.setUsername("测试");
        user.setAge(12);
        redisUtils.set(REDIS_TEST_PREFIX+"user",user);
    }

    @Test
    public void testGetObject(){
        User user = (User) redisUtils.get(REDIS_TEST_PREFIX + "user");
        System.out.println(user.getUsername()+":"+user.getAge());
    }
}

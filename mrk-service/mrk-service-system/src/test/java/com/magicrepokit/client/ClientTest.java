package com.magicrepokit.client;

import cn.hutool.crypto.digest.BCrypt;
import com.magicrepokit.system.MRKSystemApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = MRKSystemApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("com.magicrepokit")
public class ClientTest {

}

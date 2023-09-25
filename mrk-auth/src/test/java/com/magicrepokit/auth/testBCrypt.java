package com.magicrepokit.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class testBCrypt {

    @Test
    public void testHasPW(){
        String magicrepokit = BCrypt.hashpw("magicrepokit",BCrypt.gensalt());
        System.out.println(magicrepokit);
    }
}

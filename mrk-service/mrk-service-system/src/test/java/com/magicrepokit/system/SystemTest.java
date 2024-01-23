package com.magicrepokit.system;

import com.magicrepokit.system.config.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("test")
@ComponentScan(basePackages = {"com.magicrepokit"})
public class SystemTest {

    @Test
    public void testMail() {
        Context context = new Context();
        context.setVariable("verificationCode", "abcedf");
        MailUtil.sendHtml("wanghanlinlin@proton.me", "测试邮件", context,null);
    }
}

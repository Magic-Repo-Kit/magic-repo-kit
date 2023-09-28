package com.magicrepokit.user;

import com.magicrepokit.user.bean.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class GenerateSqlTest {
    @Autowired
    private Configuration freeMarkConfig;
    @Test
    public void StartDemo(){
        Template template;
        User user = new User();
        user.setName("测试");
        try {
            template = freeMarkConfig.getTemplate("GenerateSql.ftl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer out = new OutputStreamWriter(System.out);
        try {
            template.process(user,out);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

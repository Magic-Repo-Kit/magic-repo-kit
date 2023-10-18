package com.magicrepokit.system;

import com.magicrepokit.system.bean.CreateSql;
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
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class GenerateSqlTest {
    @Autowired
    private Configuration freeMarkConfig;
    @Test
    public void StartDemo(){
        Template template;
        // Create the root hash
        Map<String, Object> root = new HashMap<>();
// Put string ``user'' into the root
        root.put("user", "Big Joe");
        try {
            template = freeMarkConfig.getTemplate("GenerateSql.ftl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer out = new OutputStreamWriter(System.out);
        try {
            template.process(root,out);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestCreateSql(){
        Template template;
        // Create the root hash
        Map<String, Object> root = new HashMap<>();
        try {
            template = freeMarkConfig.getTemplate("CreateSql.ftl");
            CreateSql createSql = new CreateSql();
            createSql.setTableName("user");
            root.put("create",createSql);
            Writer out = new OutputStreamWriter(System.out);
            template.process(root,out);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.magicrepokit.i18n.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class LoadMessageResourceWithProperties implements LoadMessageResource {
    @Value("classpath:i18n/")
    private Resource resource;

    @Override
    public Map<String, Map<String, String>> load() {
        Map<String, Map<String, String>> result = new HashMap<>();
        try {
            File parentFile = resource.getFile();
            File[] files = parentFile.listFiles();
            Properties properties = new Properties();
            for (File file : files) {
                FileInputStream fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                Map<String, String> localeMap = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    localeMap.put(key, properties.getProperty(key));
                }
                properties.clear();
                result.put(getKey(file.getName()), localeMap);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private String getKey(String fileName) {
        //去掉后后缀
        String[] keys = fileName.split("\\.")[0].split("_");
        if (keys.length == 1) {
            return "";
        } else if (keys.length == 2) {
            return keys[1];
        } else if (keys.length == 3) {
            return keys[1] + "_" + keys[2];
        }else {
            throw new RuntimeException("i18n-file's name is error.");
        }

    }
}

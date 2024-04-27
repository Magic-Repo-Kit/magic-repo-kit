package com.gpt.i18n.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.gpt.i18n.constant.I18nConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
@Slf4j
public class LoadMessageResourceWithProperties implements LoadMessageResource {


    @Override
    public Map<String, Map<String, String>> load(){
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource(I18nConstant.APP_RESOURCE);
        return load(resourceUrl);
    }

    public Map<String, Map<String, String>> load(URL resourceUrl) {
        Map<String, Map<String, String>> result;
        log.info("-----获取文件地址-------{}", JSONUtil.toJsonStr(resourceUrl));
        try {
            if(ObjectUtil.isEmpty(resourceUrl)){
                return null;
            }
            URI uri = resourceUrl.toURI();
            //如果为jar包的地址
            if (uri.getScheme().equals("jar")) {
                result = loadResourceJar(uri);
            }else{
                File resourceDir = new File(uri);
                result = load(resourceDir.listFiles());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 从jar中获取资源
     *
     * @param uri
     * @return
     * @throws IOException
     */
    private Map<String, Map<String, String>> loadResourceJar(URI uri) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();
        //获取jar路径
        String jarPath = uri.getSchemeSpecificPart().substring(5, uri.getSchemeSpecificPart().indexOf('!'));
        //获取资源路径
        String resourcePath = uri.getSchemeSpecificPart().substring(uri.getSchemeSpecificPart().indexOf('!') + 2);
        if (resourcePath.contains("classes!")){
            resourcePath = resourcePath.replace("classes!", "classes");
        }
        //加载jar包
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            Properties properties = new Properties();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().startsWith(resourcePath)) {
                    String name = entry.getName();
                    if(name.contains("/")){
                        String[] split = name.split("/");
                        name = split[split.length-1];
                    }
                    // 指定正确的字符编码
                    Reader reader = new InputStreamReader(jarFile.getInputStream(entry), StandardCharsets.UTF_8);
                    properties.load(reader);
                    Map<String, String> localeMap = new HashMap<>();
                    for (String key : properties.stringPropertyNames()) {
                        localeMap.put(key, properties.getProperty(key));
                    }
                    properties.clear();
                    result.put(getKey(name), localeMap);
                }
            }
        }
        return result;
    }

    /**
     * 直接获取文件
     *
     * @param files
     * @return
     * @throws IOException
     */
    private Map<String, Map<String, String>> load(File[] files) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();
        Properties properties = new Properties();
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
            // 指定正确的字符编码
            Reader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            properties.load(reader);
            Map<String, String> localeMap = new HashMap<>();
            for (String key : properties.stringPropertyNames()) {
                localeMap.put(key, properties.getProperty(key));
            }
            properties.clear();
            result.put(getKey(file.getName()), localeMap);
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
        } else {
            throw new RuntimeException("i18n-file's name is error.");
        }

    }
}

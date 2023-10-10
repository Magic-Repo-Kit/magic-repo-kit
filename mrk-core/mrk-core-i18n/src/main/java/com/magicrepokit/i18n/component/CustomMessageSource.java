package com.magicrepokit.i18n.component;

import com.magicrepokit.i18n.config.LoadMessageResource;
import com.magicrepokit.i18n.config.LoadMessageResourceWithProperties;
import com.magicrepokit.i18n.constant.I18nConstant;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Component("messageSource")
public class CustomMessageSource extends AbstractMessageSource {
    private final String appName;
    private final MRKRedisUtils mrkRedisUtils;
    private final LoadMessageResource loadMessageResource;

    @PostConstruct
    public void init() {
        log.info("init i18n message...");
        mrkRedisUtils.del(I18nConstant.i18nKey(appName));
    }

    @Autowired
    public CustomMessageSource(@Value("${spring.application.name}") String appName, MRKRedisUtils mrkRedisUtils, LoadMessageResource loadMessageResource) {
        this.appName = appName;
        this.mrkRedisUtils = mrkRedisUtils;
        this.loadMessageResource = loadMessageResource;
    }

    /**
     * 重新加载消息到该类的Map缓存中
     */
    public Map<String, Map<String, String>> reload() {
        //app下
        Map<String, Map<String, String>> localeAppMsgMap = (Map<String, Map<String, String>>) mrkRedisUtils.get(I18nConstant.i18nKey(appName));
        if (localeAppMsgMap == null || localeAppMsgMap.isEmpty()) {
            // 加载所有的国际化资源
            localeAppMsgMap = this.loadAppAllMessageResources();
            // 缓存到redis
            if (localeAppMsgMap != null && !localeAppMsgMap.isEmpty()) {
                mrkRedisUtils.set(I18nConstant.i18nKey(appName), localeAppMsgMap);
            }
        }
        //引用包下的基础
        Map<String, Map<String, String>> localeBaseMsgMap = (Map<String, Map<String, String>>) mrkRedisUtils.get(I18nConstant.i18nKey(I18nConstant.BASE_NAME));
        if (localeBaseMsgMap == null || localeBaseMsgMap.isEmpty()) {
            // 加载所有的国际化资源
            localeBaseMsgMap = this.loadBaseMessageResources();
            // 缓存到redis
            if (localeBaseMsgMap != null && !localeBaseMsgMap.isEmpty()) {
                mrkRedisUtils.set(I18nConstant.i18nKey(I18nConstant.BASE_NAME), localeBaseMsgMap);
            }
        }
        //合并
        return margeMap(localeAppMsgMap,localeBaseMsgMap);
    }

    private Map<String, Map<String, String>> margeMap(Map<String, Map<String, String>> localeAppMsgMap,Map<String, Map<String, String>> localeBaseMsgMap){
        Map<String, Map<String, String>> result = new HashMap<>();
        // 合并 localeAppMsgMap 到 result
        if(localeBaseMsgMap!=null){
            for (Map.Entry<String, Map<String, String>> entry : localeBaseMsgMap.entrySet()) {
                String key = entry.getKey();
                Map<String, String> value = entry.getValue();

                if (result.containsKey(key)) {
                    // 如果 key 已存在于 result 中，进行合并操作
                    Map<String, String> existingMap = result.get(key);
                    existingMap.putAll(value);
                } else {
                    // 如果 key 不存在于 result 中，添加到 result
                    result.put(key, new HashMap<>(value));
                }
            }
        }

        // 合并 localeAppMsgMap 到 result
        if(localeAppMsgMap!=null){
            for (Map.Entry<String, Map<String, String>> entry : localeAppMsgMap.entrySet()) {
                String key = entry.getKey();
                Map<String, String> value = entry.getValue();

                if (result.containsKey(key)) {
                    // 如果 key 已存在于 result 中，进行合并操作
                    Map<String, String> existingMap = result.get(key);
                    existingMap.putAll(value);
                } else {
                    // 如果 key 不存在于 result 中，添加到 result
                    result.put(key, new HashMap<>(value));
                }
            }
        }

        return result;
    }

    /**
     * 从数据源加载所有国际化消息
     *
     * @return
     */
    private Map<String, Map<String, String>> loadAppAllMessageResources() {
        //从配置文件
        return loadMessageResource.load();
    }

    /**
     * 从数据源加载基础国际化消息
     *
     * @return
     */
    private Map<String, Map<String, String>> loadBaseMessageResources() {
        //从配置文件
        return loadMessageResource.loadBase();
    }


    /**
     * 缓存Map中加载国际化资源
     *
     * @param code
     * @param locale
     * @return
     */
    private String getSourceFromCacheMap(String code, Locale locale) {
        // 判断如果没有值则会去重新加载数据
        Map<String, Map<String, String>> localeMsgMap = this.reload();
        String language = ObjectUtils.isEmpty(locale) ? LocaleContextHolder.getLocale().toString() : locale.toString();
        // 获取缓存中对应语言的所有数据项
        if(localeMsgMap!=null){
            Map<String, String> propMap = localeMsgMap.get(language);
            if (!ObjectUtils.isEmpty(propMap) && propMap.containsKey(code)) {
                // 如果对应语言中能匹配到数据项，那么直接返回
                return propMap.get(code);
            }
            // 如果找不到对应locale国际化消息,就直接返回default文件的消息
            Map<String, String> defaultMap = localeMsgMap.get("");
            if (!ObjectUtils.isEmpty(defaultMap) && defaultMap.containsKey(code)) {
                // 如果对应语言中能匹配到数据项，那么直接返回
                return defaultMap.get(code);
            }
        }
        //如果还找不到返回
        return code;
    }


    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = this.getSourceFromCacheMap(code, locale);
        return new MessageFormat(msg, locale);
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return this.getSourceFromCacheMap(code, locale);
    }
}

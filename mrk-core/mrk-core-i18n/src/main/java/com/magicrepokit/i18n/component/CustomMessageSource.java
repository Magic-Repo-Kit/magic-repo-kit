package com.magicrepokit.i18n.component;

import com.magicrepokit.i18n.config.LoadMessageResource;
import com.magicrepokit.i18n.constant.I18nConstant;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Component("messageSource")
public class CustomMessageSource extends AbstractMessageSource {

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private MRKRedisUtils mrkRedisUtils;

    @Autowired
    private LoadMessageResource loadMessageResource;

    @PostConstruct
    public void init() {
        log.info("init i18n message...");
        mrkRedisUtils.del(I18nConstant.i18nKey(appName));
    }

    /**
     * 重新加载消息到该类的Map缓存中
     */
    public Map<String, Map<String, String>> reload() {
        Map<String, Map<String, String>> localeMsgMap = (Map<String, Map<String, String>>) mrkRedisUtils.get(I18nConstant.i18nKey(appName));
        if (localeMsgMap == null || localeMsgMap.isEmpty()) {
            // 加载所有的国际化资源
            localeMsgMap = this.loadAllMessageResources();
            // 缓存到redis
            if (localeMsgMap != null && !localeMsgMap.isEmpty()) {
                mrkRedisUtils.set(I18nConstant.i18nKey(appName), localeMsgMap);
            }
        }
        return localeMsgMap;
    }

    /**
     * 从数据源加载所有国际化消息
     *
     * @return
     */
    private Map<String, Map<String, String>> loadAllMessageResources() {
        //从配置文件
        return loadMessageResource.load();
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

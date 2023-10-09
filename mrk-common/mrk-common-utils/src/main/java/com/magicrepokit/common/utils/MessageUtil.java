package com.magicrepokit.common.utils;

import com.magicrepokit.i18n.component.CustomMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Component
@Slf4j
public class MessageUtil {
    private static CustomMessageSource messageSource;

    private static MessageSource reloadableMessageSource;

    @PostConstruct
    public void initialize() {
        // 在初始化时执行的逻辑
        // 可以在这里进行其他初始化操作
        log.info("--------------i18n国际化初始化");
    }

    private static void createMessageSource(String baseName) {
        // 创建ReloadableResourceBundleMessageSource对象
        ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();

        // 设置basename，指定资源文件的位置和名称
        bundleMessageSource.setBasename("classpath:" + baseName);

        // 设置编码格式
        bundleMessageSource.setDefaultEncoding("UTF-8");

        MessageUtil.reloadableMessageSource = bundleMessageSource;
    }

    @Autowired
    public void setMessageSource(CustomMessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static String getMessage(String code, @Nullable Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }

    public static String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        return messageSource.getMessage(resolvable, locale);
    }

    public static String getMessage(String code) {
        return getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code,String defaultMessage) {
        return getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, Object args[]) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public static String getReloadMessage(String baseName,String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        createMessageSource(baseName);
        return reloadableMessageSource.getMessage(code, args, defaultMessage, locale);
    }

}

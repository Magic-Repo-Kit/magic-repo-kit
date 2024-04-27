package com.gpt.i18n.config;

import com.gpt.i18n.constant.I18nConstant;
import com.gpt.i18n.interceptor.LocaleInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class LocaleConfig implements WebMvcConfigurer {
    /**
     *	默认解析器 其中locale表示默认语言,当请求中未包含语种信息，则设置默认语种
     *	当前默认为简体中文,zh_CN
     */
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }

    /**
     *  默认拦截器
     *  拦截请求，获取请求头中包含的语种信息并重新注册语种信息
     */
    @Bean
    public WebMvcConfigurer localeInterceptor() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new LocaleInterceptor());
            }
        };
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        // 设置消息源
        bean.setValidationMessageSource(resourceBundleMessageSource());
        return bean;
    }

    @Bean
    public MessageSource resourceBundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        // 多语言文件地址
        messageSource.addBasenames(I18nConstant.BASE_RESOURCE);
        return messageSource;
    }

    @Bean
    public MethodValidationPostProcessor validationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(localValidatorFactoryBean().getValidator());
        return processor;
    }

    @Override
    public Validator getValidator() {
        return localValidatorFactoryBean();
    }
}

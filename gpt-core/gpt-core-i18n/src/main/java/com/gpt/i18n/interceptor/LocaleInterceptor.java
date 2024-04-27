package com.gpt.i18n.interceptor;

import com.gpt.i18n.constant.I18nConstant;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocaleInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String newLocale = request.getHeader(I18nConstant.LOCALE);
        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
            } catch (IllegalArgumentException ignore) {
            }
        }
        return true;
    }
}

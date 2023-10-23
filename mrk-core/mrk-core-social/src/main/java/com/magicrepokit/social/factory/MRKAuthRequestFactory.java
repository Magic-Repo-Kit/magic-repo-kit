package com.magicrepokit.social.factory;

import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthSource;
import com.xingyuv.jushauth.request.AuthRequest;
import com.xingyuv.justauth.AuthRequestFactory;
import com.xingyuv.justauth.autoconfigure.JustAuthProperties;

public class MRKAuthRequestFactory extends AuthRequestFactory {
    protected JustAuthProperties properties;
    protected AuthStateCache authStateCache;
    public MRKAuthRequestFactory(JustAuthProperties properties, AuthStateCache authStateCache) {
        super(properties, authStateCache);
        this.properties = properties;
        this.authStateCache = authStateCache;
    }

    /**
     * 返回 AuthRequest 对象
     *
     * @param source {@link AuthSource} 三方对接名：GITEE、GITHUB
     * @return {@link AuthRequest}
     */
    @Override
    public AuthRequest get(String source) {
        return super.get(source);
    }
}

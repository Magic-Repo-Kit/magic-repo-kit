package com.gpt.auth.service;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * MRK客户端管理服务
 */
@Service
public class GPTClientDetailsServiceImpl extends JdbcClientDetailsService {

    /**
     * 注入数据源
     */
    public GPTClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 自定义缓存客户端信息
     * @param clientId
     * @return
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId){
        return super.loadClientByClientId(clientId);
    }
}

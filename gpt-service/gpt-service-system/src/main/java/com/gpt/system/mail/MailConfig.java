package com.gpt.system.mail;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailAccount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@Lazy
public class MailConfig {
    private final MailProperties mailProperties;
    @Bean
    public MailAccount getDefaultAccount() {
        if(ObjectUtil.isEmpty(mailProperties.getHost())) {
            log.error("请配置邮件账户!");
            throw new RuntimeException("请配置邮件账户!");
        }
        MailAccount account = new MailAccount();
        account.setHost(mailProperties.getHost());
        account.setPort(mailProperties.getPort());
        account.setAuth(mailProperties.getAuth());
        account.setFrom(mailProperties.getFrom());
        account.setUser(mailProperties.getUser());
        account.setPass(mailProperties.getPass());
        return account;
    }
}

package com.magicrepokit.system.config.mail;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "mrk.mail")
@Component
public class MailProperties {
    /**
     * 邮件服务器SMTP地址
     */
    private String host;
    /**
     * 邮件服务器SMTP端口
     */
    private Integer port=25;
    /**
     * 邮件服务器SMTP是否需要安全连接
     */
    private Boolean auth=true;
    /**
     * 邮件服务器SMTP是否启用SSL
     */
    private String from;
    /**
     * 邮件服务器用户名
     */
    private String user;
    /**
     * 邮件服务器密码
     */
    private String pass;
}

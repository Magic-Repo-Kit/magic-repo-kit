package com.gpt.system.mail;

import cn.hutool.extra.mail.MailAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import java.io.File;

import static cn.hutool.core.util.ReUtil.isMatch;

@Component
public class MailUtil extends cn.hutool.extra.mail.MailUtil {
    private static final String  REGIST_TEMPLATE = "mail/RegistTemplate";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext){
        MailUtil.applicationContext = applicationContext;
    }

    private static MailAccount getMailAccount(){
        return applicationContext.getBean(MailAccount.class);
    }

    private static TemplateEngine getTemplateEngine(){
        return applicationContext.getBean(TemplateEngine.class);
    }


    /**
     * 发送文本邮件
     * @param to 邮件接收人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param files 附件
     * @return message-id
     */
    public static String sendText(String to, String subject, String content, File... files) {
        return send(getMailAccount(),to, subject, content, false, files);
    }

    /**
     * 发送HTML邮件
     * @param to 邮件接收人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param files 附件
     * @return message-id
     */
    public static String sendHtml(String to, String subject, String content, File... files) {
        return send(getMailAccount(), to, subject, content, true, files);
    }

    /**
     * 发送HTML邮件
     * @param to 邮件接收人
     * @param subject 邮件主题
     * @param context 邮件内容
     * @param files 附件
     * @return message-id
     */
    public static String sendHtml(String to, String subject, Context context, File... files) {
        return send(getMailAccount(), to, subject, renderTemplate(context), true, files);
    }

    /**
     * 检查邮箱格式是否正确
     * @param email 邮箱
     * @return 是否正确
     */
    public static boolean checkEmail(String email){
        return isMatch(EMAIL_REGEX,email);
    }


    /**
     * 渲染模板
     * @param context 上下文
     * @return 渲染后的模板
     */
    private static String renderTemplate(Context context){
        // 传递模板名称和上下文
        return getTemplateEngine().process(REGIST_TEMPLATE, context);
    }
}

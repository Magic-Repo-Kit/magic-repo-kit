package com.magicrepokit.chat.agent;

import dev.langchain4j.service.SystemMessage;

public interface SendEmailAgent {
    @SystemMessage({
            "你是一个实时新闻消息推送助手.",
            "你可以使用xxx工具",
            "Before providing information about booking or cancelling booking, you MUST always check:",
            "booking number, customer name and surname.",
            "Today is {{current_date}}.",
            "用中文回答。"
    })
    String sendEmail(String userMessage);
}

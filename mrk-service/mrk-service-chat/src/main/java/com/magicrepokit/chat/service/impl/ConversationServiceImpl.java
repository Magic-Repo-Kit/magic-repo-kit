package com.magicrepokit.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.magicrepokit.chat.constant.ApiConstant;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.dto.chatMessage.ChatMessagePagesDTO;
import com.magicrepokit.chat.dto.chatMessage.ChatMessageResponseDTO;
import com.magicrepokit.chat.service.IConversationService;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Map;
import java.util.function.Consumer;


@Service
@Slf4j
@AllArgsConstructor
public class ConversationServiceImpl implements IConversationService {
    private final RestTemplate restTemplate;
    private static final String AUTH_BEARER = "Bearer";

    private static final String APPLICATION_JSON = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static String token;



    private void setToken(String token){
        ConversationServiceImpl.token = token;
    }

    @Override
    public void sendMsg(String messageId,String content,String conversationId,String parentMessageId) {
        String arkoseToken = getArkoseToken();
        String requestBody = getRequest(arkoseToken,messageId,content,conversationId,parentMessageId);
        chatSendRequest(requestBody, new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                parseStream(inputStream);
            }
        });
    }

    @Override
    public void sendMsg(String token,String messageId,String content,String conversationId,String parentMessageId, Consumer<InputStream> streamProcessor) {
        setToken(token);
        String arkoseToken = getArkoseToken();
        String requestBody = getRequest(arkoseToken,messageId,content,conversationId,parentMessageId);
        chatSendRequest(requestBody, streamProcessor);
    }

    @Override
    public ChatMessagePagesDTO conversationList(Integer offset, Integer limit){
        //定义body
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.set("offset", offset);
        formData.set("limit", limit);
        formData.set("order", "updated");
        String query = WebUtil.buildQueryParams(formData);
        String urlWithQuery = ApiConstant.CONVERSATION_LIST_API  + query;
        HttpEntity<String> stringHttpEntity = getStringHttpEntity(null);
        ResponseEntity<String> exchange = null;
        try {
             exchange = restTemplate.exchange(urlWithQuery, HttpMethod.GET, stringHttpEntity, String.class);
        } catch (Exception e) {
            log.error("gpt会话列表服务异常:{}",e.getMessage());
            throw new ServiceException(ChatResultCode.SERVICE_ERROR);
        }

        String bodyJson = exchange.getBody();
        ChatMessagePagesDTO bean = JSONUtil.toBean(bodyJson, ChatMessagePagesDTO.class);
        return bean;
    }

    /**
     * 解析流
     * @param inputStream
     */
    public Map<String, ChatMessageResponseDTO> parseStream(InputStream inputStream){
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String line;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            while ((line = reader.readLine()) != null) {
                //line 去掉 data:
                if(StrUtil.isNotBlank(line) && line.startsWith("data:")){
                    line = line.substring(5);
                    if(line.contains("[DONE]")){
                        line = "{done:true}";
                    }
                }
                if(StrUtil.isNotBlank(line)){
                    JSONUtil.toBean(line, ChatMessageResponseDTO.class);
                    System.out.println(line);
                }
                // 打印SSE事件内容
            }
        } catch (IOException e) {
            // 处理异常
        }
        return null;
    }


    private void chatSendRequest(String requestBody, Consumer<InputStream> streamProcessor) {
        //获取请求
        HttpEntity<String> request = getStringHttpEntity(requestBody);
        //获取响应
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, String.class);
        ResponseExtractor<Void> responseExtractor = response -> {
            if (response.getStatusCode() == HttpStatus.OK) {
                InputStream body = response.getBody();
                //插槽处理body
                streamProcessor.accept(body);
                return null;
            } else {
                throw new RuntimeException("请求失败");
            }
        };
        try {
            restTemplate.execute(ApiConstant.CONVERSATION_API,HttpMethod.POST,requestCallback,responseExtractor);
        } catch (Exception e) {
            log.error("gpt会话服务异常:{}",e.getMessage());
            throw new ServiceException(ChatResultCode.SERVICE_ERROR);
        }

    }

    /**
     * 获取请求相关
     * @param requestBody
     * @return
     */
    private static HttpEntity<String> getStringHttpEntity(String requestBody) {
        //设置请求头
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HEADER_AUTHORIZATION, AUTH_BEARER + " " + token);
        headers.set(HEADER_CONTENT_TYPE, APPLICATION_JSON);
        headers.set("Origin", "https://chat.openai.com");
        headers.set("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36");
        return new HttpEntity<>(requestBody, headers);
    }

    /**
     * 获取请求json
     * @param arkoseToken arkoseToken
     * @param messageId 消息id 必传
     * @param content 消息内容 必传
     * @param conversationId 会话id 可选，不填开始新对话
     * @param parentMessageId 父消息id 系统回复对话id
     * @return
     */
    public String getRequest(String arkoseToken,String messageId,String content,String conversationId,String parentMessageId){
        conversationId=conversationId==null|| conversationId.isEmpty() ?"null":"\""+conversationId+"\"";
        return String.format(requestTemplate(),arkoseToken,messageId,content,conversationId,parentMessageId);
    }

    /**
     * 获取arkoseToken
     * @return
     */
    private String getArkoseToken(){
        return "'134179659be5f1561.0859810804|r=ap-southeast-1|meta=3|metabgclr=transparent|metaiconclr=%23757575|guitextcolor=%23000000|lang=en|pk=3D86FBBA-9D22-402A-B512-3420086BA6CC|at=40|rid=57|cdn_url=https%3A%2F%2Ftcr9i.chat.openai.com%2Fcdn%2Ffc|lurl=https%3A%2F%2Faudio-ap-southeast-1.arkoselabs.com|surl=https%3A%2F%2Ftcr9i.chat.openai.com|smurl=https%3A%2F%2Ftcr9i.chat.openai.com%2Fcdn%2Ffc%2Fassets%2Fstyle-manager\\n'";
    }


    /**
     * 请求模板
     * @return
     */
    private String requestTemplate(){
        return "{\n" +
                "    \"action\": \"next\",\n" +
                "  \"arkose_token\":\"%s\",\n"+
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"id\": \"%s\",\n" +
                "            \"author\": {\n" +
                "                \"role\": \"user\"\n" +
                "            },\n" +
                "            \"content\": {\n" +
                "                \"content_type\": \"text\",\n" +
                "                \"parts\": [\n" +
                "                    \"%s\"\n" +
                "                ]\n" +
                "            },\n" +
                "            \"metadata\": {}\n" +
                "        }\n" +
                "    ],\n" +
                "    \"conversation_id\": %s,\n" +
                "    \"parent_message_id\": \"%s\",\n" +
                "    \"model\": \"text-davinci-002-render-sha\",\n" +
                "    \"timezone_offset_min\": -480,\n" +
                "    \"suggestions\": [],\n" +
                "    \"history_and_training_disabled\": false,\n" +
                "    \"conversation_mode\": {\n" +
                "        \"kind\": \"primary_assistant\"\n" +
                "    },\n" +
                "    \"force_paragen\": false,\n" +
                "    \"force_rate_limit\": false\n" +
                "}";
    }


}

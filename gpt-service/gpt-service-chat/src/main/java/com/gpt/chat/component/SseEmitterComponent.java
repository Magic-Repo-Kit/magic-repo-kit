package com.gpt.chat.component;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gpt.log.exceotion.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
public class SseEmitterComponent {
    private static AtomicInteger count = new AtomicInteger(0);

    private final String KEY = "TEST";

    private static ConcurrentMap<String, SseEmitter> maps = new ConcurrentHashMap<>();


    /**
     * 建立连接
     */
    public SseEmitter SseEmitterConnect(String key){
        SseEmitter sseEmitter = maps.get(key);
        if(sseEmitter!=null){
            return sseEmitter;
        }
        //建立连接
        SseEmitter connect = connect(key);
        maps.put(key,connect);
        return connect;
    }

    /**
     * 获取用户key
     * @return
     */
    public String getKey(String userId,String realName) {
        String key = null;
        //获取用户信息
        if(userId==null){
            key = KEY;
        }else{
            key = realName+"_"+userId;
        }
        return key;
    }

    /**
     * 给用户推送消息
     * @param message
     */
    public void SseEmitterSendMessage(Object message,String key){
        SseEmitter sseEmitter = maps.get(key);
        //log.info("SSE开始给{}推送消息",key);
        if(sseEmitter==null){
            sseEmitter.completeWithError(new Exception("用户客户端不存在=>"+key));
        }
        try {
            if(message instanceof String) {
                sseEmitter.send(message+"\n", MediaType.TEXT_EVENT_STREAM);
            }else {
                JSONObject entries = JSONUtil.parseObj(message);
                sseEmitter.send(entries.toString(),MediaType.TEXT_EVENT_STREAM);
            }

        } catch (IOException e) {
            removeUser(key);
            throw new ServiceException("SSE异常=>"+e.getMessage());
        }
    }

    /**
     * 给用户推送消息
     */
    public void SseEmitterSendComplateMessage(Object message,String key){
        SseEmitter sseEmitter = maps.get(key);
        if(sseEmitter==null){
            log.info("SSE开始给{}推送消息",key);
            throw new ServiceException("[SSE]用户客户端不存在=>"+key);
        }
        try {
            if(message instanceof String) {
                sseEmitter.send(message, MediaType.TEXT_EVENT_STREAM);
            }else {
                JSONObject entries = JSONUtil.parseObj(message);
                sseEmitter.send(entries.toString());
            }
            sseEmitter.complete();
        } catch (IOException e) {
            removeUser(key);
            throw new ServiceException("SSE异常=>"+e.getMessage());
        }
    }

    /**
     * 发送最后一次关闭
     */
    public void SseEmitterSendEnd(String key){
        SseEmitter sseEmitter = maps.get(key);
        if(sseEmitter==null){
            log.info("SSE开始给{}推送消息",key);
            throw new ServiceException("[SSE]用户客户端不存在=>"+key);
        }
        try {
            sseEmitter.send(SseEmitter.event().id("last").build());
        } catch (IOException e) {
            removeUser(key);
            throw new ServiceException("SSE异常=>"+e.getMessage());
        }
    }

    /**
     * 关闭连接
     * @param
     */
    public void close(String key){
        log.info("关闭sse连接，当前用户：{}", key);
        SseEmitter sseEmitter = maps.get(key);
        if(sseEmitter!=null){
            sseEmitter.complete();
            //sseEmitter.completeWithError(new Throwable("关闭链接"));
        }
        removeUser(key);
    }

    /**
     * 创建用户连接并返回 SseEmitter
     * 使用redis里面存放每个用户的SseEmitter
     * @return SseEmitter
     */
    private SseEmitter connect(String key) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(1000L*60);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(key));
        sseEmitter.onError(errorCallBack(key));
        sseEmitter.onTimeout(timeoutCallBack(key));
        log.info("创建新的sse连接，当前用户：{}", key);
        return sseEmitter;
    }


    /**
     * 移除用户连接
     */
    private void removeUser(String key) {
        maps.remove(key);
        log.info("移除用户：{}", key);
    }



    private  Runnable completionCallBack(String key) {
        return () -> {
            SseEmitterSendMessage("数据已完成推送!",key);
            log.info("结束连接：{}", key);
            removeUser(key);
        };
    }

    private  Runnable timeoutCallBack(String key) {
        return () -> {
            log.info("连接超时：{}", key);
            removeUser(key);
        };
    }

    private Consumer<Throwable> errorCallBack(String key) {
        return throwable -> {
            log.info("连接异常：{}", key);
            removeUser(key);
        };
    }
}

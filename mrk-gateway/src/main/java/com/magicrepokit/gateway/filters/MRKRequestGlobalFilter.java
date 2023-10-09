package com.magicrepokit.gateway.filters;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局请求过滤器
 */
@Component
@Log4j2
public class MRKRequestGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求头
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (!ObjectUtils.isEmpty(authorization)) {
            log.info("enter mrkRequestGlobalFilter filter method:"+authorization);
            //头部信息转发
            exchange.getRequest().mutate().header("Authorization",authorization);
        }
        return chain.filter(exchange.mutate().build());
    }

    /**
     * bean加载的顺序
     * @return
     */
    @Override
    public int getOrder() {
        return -1000;
    }
}

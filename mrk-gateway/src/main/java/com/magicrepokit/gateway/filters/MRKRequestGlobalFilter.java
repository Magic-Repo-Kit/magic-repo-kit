package com.magicrepokit.gateway.filters;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.magicrepokit.gateway.constant.GateWayConstant;
import com.magicrepokit.gateway.constant.MRKI18nEnum;
import com.magicrepokit.gateway.provider.ResponseProvider;
import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.constant.UserType;
import com.magicrepokit.jwt.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局请求过滤器+令牌校验
 */
@Component
@Log4j2
public class MRKRequestGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获得当前路径
        String path = exchange.getRequest().getURI().getPath();
        //资源放行判断
        if (isSkip(path)) {
            return chain.filter(exchange);
        }
        //校验token
        ServerHttpResponse response = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst(GateWayConstant.AUTH_KEY);
        String userType = exchange.getRequest().getHeaders().getFirst(GateWayConstant.USER_TYPE);
        UserType userTypeEnum = UserType.getByUserType(userType);
        if (userTypeEnum == null) {
            return unAuth(response, MRKI18nEnum.NOT_FOUND_USER_TYPE.getMessage());
        }
        if (StrUtil.isBlank(token)) {
            return unAuth(response, MRKI18nEnum.NOT_FOUND_JWT.getMessage());
        }
        //解析令牌
        Claims claims = JWTUtil.parseJWT(token);
        if (claims == null) {
            return unAuth(response, MRKI18nEnum.UNAUTHORIZED.getMessage());
        }
        //判断token状态
        String userId = String.valueOf(claims.get(JWTConstant.USER_ID));
        String accessToken = JWTUtil.getAccessToken(Long.valueOf(userId),userType);
        if (!token.equalsIgnoreCase(accessToken)) {
            return unAuth(response, MRKI18nEnum.INVALID_TOKEN.getMessage());
        }

        return chain.filter(exchange);
    }

    /**
     * bean加载的顺序
     * @return
     */
    @Override
    public int getOrder() {
        return -1000;
    }

    /**
     * 资源路径放行判断
     *
     * @param path
     * @return
     */
    private boolean isSkip(String path){
        return false;
    }

    /**
     * 未认证返回
     * @param resp
     * @param msg
     * @return
     */
    private Mono<Void> unAuth(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String result = JSONUtil.toJsonStr(ResponseProvider.unAuth(msg));
        DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }
}

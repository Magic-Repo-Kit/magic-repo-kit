package com.magicrepokit.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WebUtil extends WebUtils {

    /**
     * 获取 HttpServletRequest
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取客户id和密钥
     *
     * @return 获取客户id和密钥
     */
    public static String[] BasicAuthorization(){
        HttpServletRequest request = getRequest();
        String clientId;
        String clientSecret;
        // 先从 Header 中获取
        String authorization = request.getHeader("Authorization");
        authorization = StrUtil.subAfter(authorization, "Basic ", true);
        if (StringUtils.hasText(authorization)) {
            authorization = Base64.decodeStr(authorization);
            clientId = StrUtil.subBefore(authorization, ":", false);
            clientSecret = StrUtil.subAfter(authorization, ":", false);
            // 再从 Param 中获取
        } else {
            clientId = request.getParameter("clientId");
            clientSecret = request.getParameter("clientSecret");
        }

        // 如果两者非空，则返回
        if (StrUtil.isNotEmpty(clientId) && StrUtil.isNotEmpty(clientSecret)) {
            return new String[]{clientId, clientSecret};
        }
        return null;
    }

    /**
     * 参数拼接为URL
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buildQueryParams(MultiValueMap<String, String> params){
        StringBuilder queryParams = new StringBuilder("?");
        for (String key : params.keySet()) {
            for (String value : params.get(key)) {
                if(value==null){
                    value="";
                }
                try {
                    queryParams.append(key)
                            .append("=")
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return queryParams.toString();
    }
}

package com.gpt.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
    public static String buildQueryParams(MultiValueMap<String, Object> params){
        StringBuilder queryParams = new StringBuilder("?");
        for (String key : params.keySet()) {
            for (Object value : params.get(key)) {
                if(value==null){
                    value="";
                }
                try {
                    queryParams.append(key)
                            .append("=")
                            .append(URLEncoder.encode(ObjectUtil.toString(value), StandardCharsets.UTF_8.toString()))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return queryParams.toString();
    }

    @SuppressWarnings("unchecked")
    public static String replaceUrlQuery(String url, String key, String value) {
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());
        // 先移除
        TableMap<CharSequence, CharSequence> query = (TableMap<CharSequence, CharSequence>)
                ReflectUtil.getFieldValue(builder.getQuery(), "query");
        query.remove(key);
        // 后添加
        builder.addQuery(key, value);
        return builder.build();
    }
}

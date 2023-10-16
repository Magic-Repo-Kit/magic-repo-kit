package com.magicrepokit.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

public class WebUtil extends WebUtils {
    /**
     * 获取 HttpServletRequest
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

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
}

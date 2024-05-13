package com.li.chat.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author malaka
 */
@Slf4j
public class RequestContext {

    public static final String USER_ID_KEY = "X-Chat-User-Id";

    public static Long getUserId() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userid = request.getHeader(USER_ID_KEY);
        log.info("获取上下文用户id：{}", userid);
        return Long.parseLong(userid);
    }

}

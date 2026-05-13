package com.yq.spbYq.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器 - 检查用户是否已登录
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Session
        HttpSession session = request.getSession(false);
        
        // 检查Session中是否有用户信息
        if (session == null || session.getAttribute("userId") == null) {
            // 未登录，返回401错误
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"请先登录\"}");
            return false;
        }
        
        // 已登录，放行
        return true;
    }
}

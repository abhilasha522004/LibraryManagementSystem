package com.example.LibraryManagementSystem.interceptor;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.LibraryManagementSystem.util.LoggerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
            request.setAttribute("X-Request-Id", requestId);
        } else {
            request.setAttribute("X-Request-Id", requestId);
        }
        return true;
    }

    private String getUser(HttpServletRequest request) {
        Object user = request.getAttribute("user");
        return user != null ? user.toString() : "anonymous";
    }

    private String getParams(HttpServletRequest request) {
        java.util.Map<String, String> params = new java.util.HashMap<>();
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params.toString();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute("X-Request-Id");
        String user = getUser(request);
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String params = getParams(request);
        int status = response.getStatus();
        if (ex != null) {
            LoggerUtil.error("[RequestId: " + requestId + "] [User: " + user + "] [IP: " + ip + "] [Status: " + status + "] [method: " + method + "] [url: " + url + "] [params: " + params + "] Exception: " + ex.getMessage());
        }
    }
}

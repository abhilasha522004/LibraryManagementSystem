package com.example.LibraryManagementSystem.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.LibraryManagementSystem.exception.MissingRequestIdException;
import org.springframework.web.servlet.ModelAndView;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    private String getParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params.toString();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Request received | url={} | method={} | remoteAddr={} | params={}",
                request.getRequestURL(), request.getMethod(), request.getRemoteAddr(), getParams(request));

        String acceptHeader = request.getHeader("Accept"); 

        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            String requestId = request.getHeader("X-Request-Id");
            if (requestId == null || requestId.isEmpty()) {
                throw new MissingRequestIdException("Missing X-Request-Id header");
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("PostHandle | url={} | method={} | status={}",
                request.getRequestURL(), request.getMethod(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("Request completed | url={} | method={} | status={}",
                request.getRequestURL(), request.getMethod(), response.getStatus());
        if (ex != null) {
            logger.error("Exception occurred | url={} | method={} | error={}",
                    request.getRequestURL(), request.getMethod(), ex.getMessage(), ex);
        }
    }
}

package com.example.LibraryManagementSystem.interceptor;

import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class ValidationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;

    private String getUser(HttpServletRequest request) {
        Object user = request.getAttribute("user");
        return user != null ? user.toString() : "anonymous";
    }

    private String getParams(HttpServletRequest request) {
        Map<String, String> params = new java.util.HashMap<>();
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params.toString();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().equals("/borrow-history/borrow")) {
            String studentIdStr = request.getParameter("studentId");
            String bookIdStr = request.getParameter("bookId");
            if (studentIdStr != null && bookIdStr != null) {
                try {
                    Long studentId = Long.parseLong(studentIdStr);
                    Long bookId = Long.parseLong(bookIdStr);
                    boolean alreadyBorrowed = borrowHistoryRepository
                        .findByStudent_Id(studentId)
                        .stream()
                        .anyMatch(bh -> bh.getBook().getId().equals(bookId) && bh.getReturnDate() == null);
                    if (alreadyBorrowed) {
                        String requestId = (String) request.getAttribute("X-Request-Id");
                        String user = getUser(request);
                        String ip = request.getRemoteAddr();
                        String method = request.getMethod();
                        String url = request.getRequestURL().toString();
                        String params = getParams(request);
                        logger.warn("[RequestId: {}] [User: {}] [IP: {}] [method: {}] [url: {}] [params: {}] Borrow denied | reason=already borrowed | studentId={} | bookId={}", requestId, user, ip, method, url, params, studentId, bookId);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        try {
                            response.getWriter().write("Student has already borrowed a copy of this book and not returned it.");
                        } catch (java.io.IOException ioEx) {
                            logger.error("[RequestId: {}] [User: {}] [IP: {}] IOException while writing response: {}", requestId, user, ip, ioEx.getMessage(), ioEx);
                        }
                        return false;
                    }
                } catch (NumberFormatException e) {
                    String requestId = (String) request.getAttribute("X-Request-Id");
                    String user = getUser(request);
                    String ip = request.getRemoteAddr();
                    String method = request.getMethod();
                    String url = request.getRequestURL().toString();
                    String params = getParams(request);
                    logger.warn("[RequestId: {}] [User: {}] [IP: {}] [method: {}] [url: {}] [params: {}] Borrow denied | reason=invalid parameter | studentId={} | bookId={}", requestId, user, ip, method, url, params, studentIdStr, bookIdStr);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) {
        // intentionally left blank
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            String requestId = (String) request.getAttribute("X-Request-Id");
            String user = getUser(request);
            String ip = request.getRemoteAddr();
            String method = request.getMethod();
            String url = request.getRequestURL().toString();
            String params = getParams(request);
            logger.error("[RequestId: {}] [User: {}] [IP: {}] [method: {}] [url: {}] [params: {}] ValidationInterceptor exception | error={}", requestId, user, ip, method, url, params, ex.getMessage(), ex);
        }
    }
} 
package com.example.LibraryManagementSystem.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import com.example.LibraryManagementSystem.util.LoggerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ValidationInterceptor implements HandlerInterceptor {
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
                        LoggerUtil.error("[RequestId: " + requestId + "] [User: " + user + "] [IP: " + ip + "] [method: " + method + "] [url: " + url + "] [params: " + params + "] Borrow denied | reason=already borrowed | studentId=" + studentId + " | bookId=" + bookId);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        try {
                            response.getWriter().write("Student has already borrowed a copy of this book and not returned it.");
                        } catch (java.io.IOException ioEx) {
                            LoggerUtil.error("[RequestId: " + requestId + "] [User: " + user + "] [IP: " + ip + "] IOException while writing response: " + ioEx.getMessage() + " Exception: " + ioEx);
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
                    LoggerUtil.error("[RequestId: " + requestId + "] [User: " + user + "] [IP: " + ip + "] [method: " + method + "] [url: " + url + "] [params: " + params + "] Borrow denied | reason=invalid parameter | studentId=" + studentIdStr + " | bookId=" + bookIdStr);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) {
        
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
            LoggerUtil.error("[RequestId: " + requestId + "] [User: " + user + "] [IP: " + ip + "] [method: " + method + "] [url: " + url + "] [params: " + params + "] ValidationInterceptor exception | error=" + ex.getMessage());
        }
    }
} 
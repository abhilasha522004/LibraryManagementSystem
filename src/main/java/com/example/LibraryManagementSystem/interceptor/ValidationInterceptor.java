package com.example.LibraryManagementSystem.interceptor;

import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ValidationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().equals("/borrow-history/borrow")) {
            String studentIdStr = request.getParameter("studentId");
            String bookIdStr = request.getParameter("bookId");
            logger.info("Borrow attempt | studentId={} | bookId={}", studentIdStr, bookIdStr);
            if (studentIdStr != null && bookIdStr != null) {
                try {
                    Long studentId = Long.parseLong(studentIdStr);
                    Long bookId = Long.parseLong(bookIdStr);
                    boolean alreadyBorrowed = borrowHistoryRepository
                        .findByStudent_Id(studentId)
                        .stream()
                        .anyMatch(bh -> bh.getBook().getId().equals(bookId) && bh.getReturnDate() == null);
                    if (alreadyBorrowed) {
                        logger.warn("Borrow denied | reason=already borrowed | studentId={} | bookId={}", studentId, bookId);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Student has already borrowed a copy of this book and not returned it.");
                        return false;
                    } else {
                        logger.info("Borrow allowed | studentId={} | bookId={}", studentId, bookId);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Borrow denied | reason=invalid parameter | studentId={} | bookId={}", studentIdStr, bookIdStr);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("ValidationInterceptor postHandle | url={} | method={} | status={}", request.getRequestURL(), request.getMethod(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("ValidationInterceptor afterCompletion | url={} | method={} | status={}", request.getRequestURL(), request.getMethod(), response.getStatus());
        if (ex != null) {
            logger.error("ValidationInterceptor exception | url={} | method={} | error={}", request.getRequestURL(), request.getMethod(), ex.getMessage(), ex);
        }
    }
} 
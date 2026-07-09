package com.aurora.interceptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aurora.util.PageUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.aurora.constant.CommonConstant.*;

@Component
public class PaginationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String currentPage = request.getParameter(CURRENT_PARAM);
        String pageSize = Optional.ofNullable(request.getParameter(SIZE_PARAM)).orElse(DEFAULT_SIZE);
        if (StringUtils.hasText(currentPage)) {
            try {
                PageUtil.setCurrentPage(new Page<>(Long.parseLong(currentPage), Long.parseLong(pageSize)));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PageUtil.remove();
    }

}
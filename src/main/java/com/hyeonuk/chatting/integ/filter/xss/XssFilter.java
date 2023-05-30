package com.hyeonuk.chatting.integ.filter.xss;

import com.hyeonuk.chatting.integ.service.xss.XssFilterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XssFilter implements Filter {
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig=filterConfig;
    }

    @Override
    public void destroy() {
        this.filterConfig=null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequestWrapper requestWrapper = new XssFilterRequestWrapper((HttpServletRequest) request);
        chain.doFilter(requestWrapper,response);
    }
}

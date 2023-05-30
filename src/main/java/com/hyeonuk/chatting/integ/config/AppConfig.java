package com.hyeonuk.chatting.integ.config;

import com.hyeonuk.chatting.integ.filter.xss.XssFilter;
import com.hyeonuk.chatting.integ.interceptor.SessionInterceptor;
import com.hyeonuk.chatting.integ.service.xss.XssFilterService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    public String[] excludePatterns = {"/auth/login","/auth/join"};

    private final XssFilter xssFilter;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns)
                .order(1);
    }

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean(){
        FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean
                = new FilterRegistrationBean<>();

        xssFilterFilterRegistrationBean.setFilter(this.xssFilter);
        xssFilterFilterRegistrationBean.setOrder(1);
        xssFilterFilterRegistrationBean.addUrlPatterns("/*");
        xssFilterFilterRegistrationBean.setName("xss-filter");
        return xssFilterFilterRegistrationBean;
    }
}

package org.omnione.did.base.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SpaLoginForwardConfig {

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> spaLoginForwardFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                if ("GET".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getRequestURI())) {
                    request.getRequestDispatcher("/index.html").forward(request, response);
                    return;
                }

                filterChain.doFilter(request, response);
            }
        });
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}

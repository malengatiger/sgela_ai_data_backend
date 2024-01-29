package com.boha.skunk.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class RequestFilter implements Filter {
    static final String mm = "\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35\uD83D\uDD35 " +
            "RequestFilter \uD83D\uDC9C";
    static final Logger logger = Logger.getLogger(RequestFilter.class.getSimpleName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic, if needed
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String fullUrl = getFullUrl(request);

        logger.info(mm + "Incoming missile: " + fullUrl);
        // Continue the filter chain
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }


    private String getFullUrl(HttpServletRequest request) {
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            fullUrl.append("?").append(queryString);
        }
        return fullUrl.toString();
    }

}

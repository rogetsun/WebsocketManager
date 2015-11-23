package com.uv.websocket;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by uv2sun on 15/11/22.
 * 过滤所有websocket连接,增加httpSession到UserPrincipal
 * 让websocket可以获得UserPrincipal后再获得httpSession
 */
public class WSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final Principal p = new PrincipalHasSession(request.getSession());
        HttpServletRequest requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public Principal getUserPrincipal() {
                return p;
            }
        };
        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 持有HttpSession的Principal
     */
    class PrincipalHasSession implements Principal {
        HttpSession session;

        public PrincipalHasSession(HttpSession session) {
            this.session = session;
        }

        public HttpSession getSession() {
            return session;
        }

        @Override
        public String getName() {
            return "";
        }
    }
}

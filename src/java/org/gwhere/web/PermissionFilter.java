package org.gwhere.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gwhere.constant.Const;
import org.gwhere.model.SysUser;
import org.gwhere.service.PermissionService;
import org.gwhere.utils.PermissionCache;
import org.gwhere.vo.PermissionVO;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class PermissionFilter implements Filter {

    private PermissionService permissionService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        permissionService = (PermissionService) wc.getBean("permissionServiceImpl");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (!permissionService.authentication(request)) {
            response.sendError(401);
            return;
        }
        filterChain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}

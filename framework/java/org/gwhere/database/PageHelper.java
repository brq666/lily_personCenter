package org.gwhere.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jiangtao on 16/4/11.
 */
public class PageHelper extends com.github.pagehelper.PageHelper {

    private final static Logger logger = LogManager.getLogger(PageHelper.class);

    public static void startPage() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        checkMultiPageParam(request);
    }

    public static int getCurrentPage() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return Integer.valueOf(request.getHeader("Current-Page"));
    }

    public static int getPageSize() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return Integer.valueOf(request.getHeader("Page-Size"));
    }

    private static void checkMultiPageParam(HttpServletRequest request) {
        if (request.getHeader("Multi-Page") == null) {
            return;
        }

        if ("true".equals(request.getHeader("Multi-Page").toLowerCase())) {
            if (logger.isDebugEnabled()) {
                logger.debug("require page:currentPage=" + request.getHeader("Current-Page") + ",pageSize=" + request.getHeader("Page-Size"));
            }
            com.github.pagehelper.PageHelper.startPage(Integer.valueOf(request.getHeader("Current-Page")), Integer.valueOf(request.getHeader("Page-Size")));
        }
    }
}

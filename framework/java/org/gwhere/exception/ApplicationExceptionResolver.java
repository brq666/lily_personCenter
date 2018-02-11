package org.gwhere.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by jiangtao on 16/4/11.
 */
public class ApplicationExceptionResolver implements HandlerExceptionResolver {

    private final Logger logger = LogManager.getLogger(ApplicationExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error("执行前台请求时出现系统异常", ex);
        if (!(ex instanceof SystemException)) {
            ex = new SystemException(ErrorCode.UNKNOWN, "系统异常", ex);
        }
        //TODO save

        String errorCode = String.valueOf(((SystemException) ex).errorCode.getCode());
        String errorMsg = ex.getMessage();
        try {
            response.setHeader("ERROR_CODE", errorCode);
            response.setHeader("ERROR_MSG", URLEncoder.encode(errorMsg, "UTF-8"));
            response.getWriter().append(errorMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("ERROR_CODE", errorCode);
//        jsonObject.put("ERROR_MSG", errorMsg);
//        try {
//            jsonObject.writeJSONString(response.getWriter());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}

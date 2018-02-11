package org.gwhere.message.handler.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by jiangtao on 15-7-16.
 */
public abstract class ServletReceiveHandler<I, O> extends HttpHandler<ServletReceiveMessage, I, O> {

    protected final Log logger = LogFactory.getLog(getClass());

    protected ThreadLocal<Map<String, String>> parameterMap = new ThreadLocal<>();

    public Map<String, String> getParameterMap() {
        return parameterMap.get();
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap.set(parameterMap);
    }

    @Override
    public Object handlerMessage(ServletReceiveMessage message) {
        HttpHandlerListener httpHandlerListener = message.getHttpHandlerListener();
        Object messageSource = message.getSource();
        try {
            if (httpHandlerListener != null) {
                httpHandlerListener.beforeDoHandler(messageSource);
            }
            I inputData = null;
            if (isDoInput()) {
                InputStream inputStream = message.getHttpServletRequest().getInputStream();
                try {
                    inputData = doInput(inputStream);
                    if (httpHandlerListener != null) {
                        httpHandlerListener.afterDoInput(inputData, messageSource);
                    }
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }

            O result = onHandlerMessage(inputData);

            if (isDoOutput()) {
                OutputStream outputStream = message.getHttpServletResponse().getOutputStream();
                try {
                    if (httpHandlerListener != null) {
                        httpHandlerListener.beforeDoOutput(result, messageSource);
                    }
                    doOutput(outputStream, result);
                } finally {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            }
            if (httpHandlerListener != null) {
                httpHandlerListener.afterDoHandler(messageSource);
            }
        } catch (Exception e) {
            logger.error("ServletReceiveHandler error", e);
            if (httpHandlerListener != null) {
                httpHandlerListener.onException(e, messageSource);
            }
            return false;
        }
        return true;
    }

    /**
     * 消息处理具体实现
     *
     * @param inputData
     * @return
     */
    public abstract O onHandlerMessage(I inputData);

    public abstract void onReceiveFinish();
}

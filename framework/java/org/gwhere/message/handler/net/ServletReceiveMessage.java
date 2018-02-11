package org.gwhere.message.handler.net;

import org.gwhere.message.handler.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jiangtao on 15-7-16.
 */
public class ServletReceiveMessage extends Message {

    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    protected HttpHandlerListener httpHandlerListener;

    public ServletReceiveMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public HttpHandlerListener getHttpHandlerListener() {
        return httpHandlerListener;
    }

    public void setHttpHandlerListener(HttpHandlerListener httpHandlerListener) {
        this.httpHandlerListener = httpHandlerListener;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }
}

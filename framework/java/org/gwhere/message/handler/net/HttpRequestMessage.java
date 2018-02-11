package org.gwhere.message.handler.net;

import org.gwhere.message.handler.Message;

/**
 * Created by jiangtao on 15-7-14.
 */
public class HttpRequestMessage extends Message {

    private String url;

    protected HttpHandlerListener httpHandlerListener;

    protected void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HttpHandlerListener getHttpConnectListener() {
        return httpHandlerListener;
    }

    public void setHttpHandlerListener(HttpHandlerListener httpHttpHandlerListener) {
        this.httpHandlerListener = httpHttpHandlerListener;
    }

    private RequestMsgDesc requestMsgDesc;

    public HttpRequestMessage(Enum messageInterface, String... params) {
        try {
            requestMsgDesc = messageInterface.getClass().getField(messageInterface.name()).getAnnotation(RequestMsgDesc.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        String url = requestMsgDesc.url();
        for (String param : params) {
            url = url.replaceFirst("\\*", param);
        }
        setUrl(url);
    }

    @Override
    public Message setMessageData(Object data) {
        Class reqClazz = requestMsgDesc.requestDataType();
        Class dataClazz = data.getClass();
        if (reqClazz.isAssignableFrom(dataClazz)) {
            super.setMessageData(data);
        } else {
            throw new HttpConnectException("Message data not support");
        }
        return this;
    }

    public RequestMsgDesc getRequestMsgDesc() {
        return requestMsgDesc;
    }
}

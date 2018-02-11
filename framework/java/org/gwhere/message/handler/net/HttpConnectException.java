package org.gwhere.message.handler.net;

/**
 * Created by jiangtao on 15-7-10.
 */
public class HttpConnectException extends RuntimeException {

    public HttpConnectException(String msg) {
        super(msg);
    }

    public HttpConnectException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

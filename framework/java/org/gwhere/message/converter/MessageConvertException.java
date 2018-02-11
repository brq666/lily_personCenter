package org.gwhere.message.converter;

/**
 * Created by jiangtao on 15-7-10.
 */
public class MessageConvertException extends RuntimeException {
    public MessageConvertException(String msg) {
        super(msg);
    }

    public MessageConvertException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

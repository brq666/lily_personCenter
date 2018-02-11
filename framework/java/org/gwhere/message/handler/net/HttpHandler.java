package org.gwhere.message.handler.net;

import org.gwhere.message.handler.Handler;
import org.gwhere.message.converter.MessageConverter;
import org.gwhere.message.handler.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jiangtao on 15-7-16.
 */
public abstract class HttpHandler<T extends Message, I extends Object, O extends Object> implements Handler<T> {

    protected final Log logger = LogFactory.getLog(getClass());

    protected MessageConverter messageConverter;

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /**
     * 设置处理器是否执行output操作
     *
     * @return
     */
    protected boolean isDoOutput() {
        return true;
    }

    /**
     * 设置处理器是否执行input操作
     *
     * @return
     */
    protected boolean isDoInput() {
        return true;
    }

    /**
     * output操作
     *
     * @param outputStream
     * @param outputData
     */
    protected abstract void doOutput(OutputStream outputStream, O outputData);

    /**
     * input操作
     *
     * @param inputStream
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected abstract I doInput(InputStream inputStream) throws IllegalAccessException, InstantiationException;

}

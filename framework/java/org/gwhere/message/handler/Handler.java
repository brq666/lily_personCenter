package org.gwhere.message.handler;

/**
 * Created by jiangtao on 15-7-14.
 */
public interface Handler<T extends Message> {

    /**
     * 处理消息
     *
     * @param message
     * @return
     */
    Object handlerMessage(T message);

}

package org.gwhere.message.converter;

/**
 * Created by jiangtao on 15-7-10.
 */
public interface MessageConverter<R, W, I, O> {

    boolean canRead(Class<?> clazz);

    boolean canWrite(Class<?> clazz);

    R read(Class<? extends R> clazz, I inputMessage);

    void write(W w, O outputMessage);
}

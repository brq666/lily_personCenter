package org.gwhere.message.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Created by jiangtao on 15-7-9.
 */
public abstract class AbstractMessageConverter<R, W, I, O> implements MessageConverter<R, W, I, O> {

    protected final Log logger = LogFactory.getLog(getClass());

    protected AbstractMessageConverter() {
    }

    @Override
    public final R read(Class<? extends R> clazz, I inputMessage) {
        R r;
        try {
            r = readInternal(clazz, inputMessage);
        } catch (IOException e) {
            throw new MessageConvertException("Could not read message with " + clazz.getName(), e);
        }
        return r;
    }

    @Override
    public final void write(W w, O outputMessage) {
        try {
            writeInternal(w, outputMessage);
        } catch (IOException e) {
            throw new MessageConvertException("Could not write message with " + w.getClass().getName(), e);
        }

    }

    protected abstract R readInternal(Class<? extends R> clazz, I inputMessage)
            throws IOException;

    protected abstract void writeInternal(W t, O outputMessage)
            throws IOException;

}

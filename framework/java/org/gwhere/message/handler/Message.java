package org.gwhere.message.handler;

import java.util.concurrent.Callable;

/**
 * Created by jiangtao on 15-7-14.
 */
public class Message implements Callable<Object> {

    protected Object data;

    private Handler handler;

    protected Object source;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Message setMessageData(Object data) {
        this.data = data;
        return this;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public Object getMessageData() {
        return data;
    }

    @Override
    public Object call() {
        return handler.handlerMessage(this);
    }
}

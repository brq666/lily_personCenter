package org.gwhere.message.handler.net;

/**
 * Created by jiangtao on 16/2/26.
 */
public abstract class AbsHttpHandlerListener<I, O> implements HttpHandlerListener<I, O> {

    public void beforeDoHandler(Object source) {
    }

    public void beforeDoOutput(O requestData, Object source) {
    }

    public void afterDoInput(I responseData, Object source) {
    }

    public void afterDoHandler(Object source) {
    }

    public void onException(Exception e, Object source) {
    }

}

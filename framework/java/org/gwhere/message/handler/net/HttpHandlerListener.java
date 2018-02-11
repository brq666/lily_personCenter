package org.gwhere.message.handler.net;

/**
 * Created by jiangtao on 15-7-16.
 */
public interface HttpHandlerListener<I, O> {
    void beforeDoHandler(Object source);

    void beforeDoOutput(O requestData, Object source);

    void afterDoInput(I responseData, Object source);

    void afterDoHandler(Object source);

    void onException(Exception e, Object source);
}

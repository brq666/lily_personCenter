package org.gwhere.message.handler;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jiangtao on 2015/7/20.
 */
public class SyncMessageWrapper implements Callable<Object> {

    private CountDownLatch countDownLatch;

    private Message message;

    Object object;

    public SyncMessageWrapper(Message message, CountDownLatch countDownLatch) {
        this.message = message;
        this.countDownLatch = countDownLatch;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public Object call() throws Exception {
        try {
            object = message.call();
        } finally {
            countDownLatch.countDown();
        }
        return object;
    }
}

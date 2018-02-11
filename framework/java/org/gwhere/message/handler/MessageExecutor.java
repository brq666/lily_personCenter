package org.gwhere.message.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.function.Function;
//import java.util.stream.Collectors;

/**
 * Created by jiangtao on 15-7-14.
 */
public class MessageExecutor implements Runnable {

    protected final Log logger = LogFactory.getLog(getClass());

    private final static int corePoolSize = 10;

    private ExecutorService threadPoolExecutor;

    private LinkedList<Message> asyncList;

    private LinkedList<SyncMessageWrapper> syncList;

    private final ReentrantLock takeLock = new ReentrantLock();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notEmpty = takeLock.newCondition();

    private AtomicInteger asyncCount = new AtomicInteger();

    private AtomicInteger syncCount = new AtomicInteger();

    public MessageExecutor() {
        this(corePoolSize);
    }

    private static class SingletonHolder {
        private final static MessageExecutor INSTANCE = new MessageExecutor(10);
    }

    public int getMessageQueenSize() {
        return asyncCount.get() + syncCount.get();
    }

    public static MessageExecutor getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public MessageExecutor(int poolSize) {
        syncList = new LinkedList<>();
        asyncList = new LinkedList<>();
        threadPoolExecutor = Executors.newFixedThreadPool(poolSize, new MessageThreadFactory());
        for (int index = 0; index < poolSize; index++) {
            threadPoolExecutor.execute(this);
        }
    }

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    public void shutdownNow() {
        synchronized (this) {
            threadPoolExecutor.shutdownNow();
        }
    }

    public Object sendSyncMessage(Message message) {
        return message.call();
    }

//    public List<Object> sendSyncMessage(List<Message> messages) {
//        List<Object> result = new LinkedList<>();
//        //如果执行的线程是线程池中的线程，直接用本线程直接执行，已避免线程过多时出现线程锁死，执行速度慢
//        if (Thread.currentThread().getName().startsWith(MessageThreadFactory.threadName)) {
//            result.addAll(messages.parallelStream().map(Message::call).collect(Collectors.toList()));
//        } else {
//            CountDownLatch countDownLatch = new CountDownLatch(messages.size());
//            List<SyncMessageWrapper> list = messages.parallelStream().map(new Function<Message, SyncMessageWrapper>() {
//                @Override
//                public SyncMessageWrapper apply(Message message) {
//                    return new SyncMessageWrapper(message, countDownLatch);
//                }
//            }).collect(Collectors.toCollection(LinkedList::new));
//            int listSize = -1;
//            putLock.lock();
//            try {
//                syncList.addAll(list);
//                listSize = syncCount.addAndGet(list.size());
//            } finally {
//                putLock.unlock();
//            }
//            if (listSize > 0) {
//                signalNotEmpty();
//            }
//            try {
//                countDownLatch.await();
//            } catch (InterruptedException e) {
//                logger.warn("Sync message interrupted");
//            }
//            result.addAll(list.parallelStream().map(new Function<SyncMessageWrapper, Object>() {
//                @Override
//                public Object apply(SyncMessageWrapper messageWrapper) {
//                    return messageWrapper.object;
//                }
//            }).collect(Collectors.toList()));
//        }
//        return result;
//    }

    public void sendAsyncMessage(Message message) {
        int listSize = -1;
        putLock.lock();
        try {
            asyncList.add(message);
            listSize = asyncCount.getAndIncrement();
        } finally {
            putLock.unlock();
        }
        if (listSize == 0) {
            signalNotEmpty();
        }
    }

    public void sendAsyncMessage(List<Message> messages) {
        putLock.lock();
        int listSize = -1;
        try {
            asyncList.addAll(messages);
            listSize = asyncCount.getAndAdd(asyncList.size());
        } finally {
            putLock.unlock();
        }
        if (listSize == 0) {
            signalNotEmpty();
        }
    }

    private void callMessage(Callable message) {
        try {
            message.call();
        } catch (Exception e) {
            logger.error("MessageExecutor:thread" + Thread.currentThread() + " execute error", e);
        }
    }

    @Override
    public void run() {
        logger.info("start thread=" + Thread.currentThread().getName());
        while (true) {
            Callable message = null;
            int count = -1;
            takeLock.lock();
            try {
                while (true) {
                    //同步消息的执行优先级高于异步消息
                    if (syncCount.get() > 0) {
                        message = syncList.poll();
                        count = syncCount.getAndDecrement();
                    } else if (asyncCount.get() > 0) {
                        message = asyncList.poll();
                        count = asyncCount.getAndDecrement();
                    }
                    if (message != null) {
                        if (count > 1) {
                            signalNotEmpty();
                        }
                        break;
                    } else {
                        notEmpty.await();
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("MessageExecutor:thread" + Thread.currentThread() + " is interrupted");
                return;
            } finally {
                takeLock.unlock();
            }
            callMessage(message);
        }
    }
}

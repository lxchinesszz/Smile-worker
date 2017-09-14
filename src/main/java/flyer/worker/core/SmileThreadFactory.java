package flyer.worker.core;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Package: flyer.worker.core
 * @Description: 自定义线程工厂
 * @author: liuxin
 * @date: 2017/9/14 上午11:59
 */
public class SmileThreadFactory implements ThreadFactory {

    /**
     * 原子操作保证每个线程都有唯一的
     */
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemoThread;

    private final ThreadGroup threadGroup;

    public SmileThreadFactory() {
        this("smile-threadpool-" + threadNumber.getAndIncrement(), false);
    }

    public SmileThreadFactory(String prefix) {
        this(prefix, false);
    }


    public SmileThreadFactory(String prefix, boolean daemo) {
        this.prefix = StringUtils.isNotEmpty(prefix) ? prefix + "-thread-" : "";
        daemoThread = daemo;
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(threadGroup, runnable, name, 0);
        ret.setDaemon(daemoThread);
        return ret;
    }

    public static void main(String[] args) {
        SmileThreadFactory smileThreadFactory = new SmileThreadFactory();
        testThread(smileThreadFactory);
    }


    public static void testThread(SmileThreadFactory smileThreadFactory) {
        for (int i = 0; i < 200; i++) {
            smileThreadFactory.newThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()
                    );
                }
            }).start();
        }

    }
}
package flyer.worker.core;

import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.*;

/**
 * @Package: flyer.worker
 * @Description:
 * @author: liuxin
 * @date: 2017/9/14 上午11:38
 */
public class SmileMaster<T> {
    /**
     * 任务队列
     */
    protected Queue<Object> workQueue = new ConcurrentLinkedQueue<>();
    /**
     * 非线程池方式Worker进程队列
     */
    protected Map<String, Thread> threadMap = new HashMap<>();
    /**
     * 使用线程池维护
     * 使用带缓存的线程池,处理少量任务可以任务不会被放弃会排队
     * 如果任务量比较大，请自定
     */
    protected ExecutorService executorService = Executors.newCachedThreadPool(new SmileThreadFactory());
    /**
     * 该队列目的，添加任务队列，使用线程池维护执行
     */
    protected Queue<Thread> workCurrentQueue = new ConcurrentLinkedQueue<>();
    /**
     * 子任务处理结果集
     */
    protected Map<String, Object> resultMap = new ConcurrentHashMap<>();

    /**
     * 是否所有的子任务都结束了
     *
     * @return
     */
    public boolean isComplete() {
        for (Map.Entry<String, Thread> entry : threadMap.entrySet()) {
            if (entry.getValue().getState() != Thread.State.TERMINATED) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param worker      Worker进程逻辑
     * @param countWorker Worker进程数量
     */
    public SmileMaster(SmileWorker worker, int countWorker) {
        worker.setWorkQueue(workQueue);
        worker.setResultMap(resultMap);
        ThreadFactory threadFactory = new SmileThreadFactory();
        for (int i = 0; i < countWorker; i++) {
            Thread thread = threadFactory.newThread(worker);
            threadMap.put(thread.getName(), thread);
        }
    }

    /**
     * @param worker        Worker进程逻辑
     * @param countWorker   Worker进程数量
     * @param threadFactory 自定义线程工厂
     */
    public SmileMaster(SmileWorker worker, int countWorker, ThreadFactory threadFactory) {
        worker.setWorkQueue(workQueue);
        worker.setResultMap(resultMap);
        for (int i = 0; i < countWorker; i++) {
            Thread thread = threadFactory.newThread(worker);
            threadMap.put(thread.getName(), thread);
        }
    }

    /**
     * @param worker   Worker进程逻辑
     * @param executor 线程池
     *                 使用线程池来维护工作队列
     */
    public SmileMaster(SmileWorker worker, int countWorker, ExecutorService executor) {
        worker.setWorkQueue(workQueue);
        worker.setResultMap(resultMap);
        this.executorService = executor;
        ThreadFactory threadFactory = new SmileThreadFactory();
        for (int i = 0; i < countWorker; i++) {
            Thread thread = threadFactory.newThread(worker);
            workCurrentQueue.add(thread);
        }
    }

    /**
     * 提交一个任务
     *
     * @param job
     */
    public void submit(T job) {
        workQueue.add(job);
    }


    /**
     * 获取结果之前，首先判断所有线程是否已经全部执行，完毕，
     * 执行完毕会返回结果
     * 未执行完成，进行自旋等待。
     * 此方法不适用与，大批量线程
     *
     * @return
     */
    public Map<String, Object> getResultMap() {
        while (!isComplete()) {
        }
        return resultMap;
    }


    /**
     * 获取结果之前，首先判断所有线程是否已经全部执行，完毕，
     * 执行完毕会返回结果
     * 未执行完毕，自选
     *
     * @return
     */
    public Map<String, Object> getResultMapPool() {
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                return resultMap;
            }
        }
    }

    /**
     * 开始运行所有的Worker进程，进行处理
     */
    public void execute() {
        for (Map.Entry<String, Thread> entry : threadMap.entrySet()) {
            entry.getValue().start();
        }
    }

    /**
     * 使用线程池方法
     */
    public void executePool() {
        int size = workCurrentQueue.size();
        for (int i = 0; i < size; i++) {
            Thread poll = workCurrentQueue.poll();
            executorService.execute(poll);
        }
    }


}

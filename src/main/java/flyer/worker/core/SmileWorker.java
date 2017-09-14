package flyer.worker.core;


import flyer.worker.exception.IllegalhandlerException;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Queue;

/**
 * @Package: flyer.worker
 * @Description: 任务
 * @author: liuxin
 * @date: 2017/9/14 上午11:40
 */
public class SmileWorker implements Runnable {

    /**
     * 多个worker 公用一个一个任务队列，从中获取不同的任务
     */
    protected Queue<Object> workQueue;
    /**
     * 公用结果
     */
    protected Map<String, Object> resultMap;
    /**
     * worker中处理方法接口
     * 使用者要实现改类复写 handler方法
     */
    protected SmileWorkerHandler smileWorkerHandler;

    public SmileWorker(SmileWorkerHandler smileWorkerHandler) {
        this.smileWorkerHandler = smileWorkerHandler;
    }

    public void setWorkQueue(Queue<Object> workQueue) {
        this.workQueue = workQueue;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    //子任务处理的逻辑，在子类中实现具体逻辑
    public Object handle(Object input) {
        Object handler = smileWorkerHandler.handler(input);
        return handler;
    }


    @Override
    public void run() {
//获取子任务
        while (true) {
            Object input = workQueue.poll();
            if (input == null) {
                return;
            }
            //处理子任务
            Object re = handle(input);
            if (ObjectUtils.isEmpty(re)) {
                throw new IllegalhandlerException("请复写SmileWorkerhandler中handler方法");
            }
            resultMap.put(Integer.toString(input.hashCode()), re);
        }
    }
}

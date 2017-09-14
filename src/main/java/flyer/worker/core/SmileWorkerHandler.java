package flyer.worker.core;

/**
 * @Package: flyer.worker
 * @Description: 默认处理方法
 * @author: liuxin
 * @date: 2017/9/14 上午11:45
 */
public interface SmileWorkerHandler<T> {
    default  T handler(T input) {
        return input;
    }
}

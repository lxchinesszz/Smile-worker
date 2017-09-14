package flyer.worker.core;

/**
 * @Package: flyer.worker.core
 * @Description 默认处理类
 * @author: liuxin
 * @date: 2017/9/14 下午3:46
 */
public class DefaultWorkerHandler implements SmileWorkerHandler<Integer> {
    @Override
    public Integer handler(Integer input) {
        return input;
    }

}

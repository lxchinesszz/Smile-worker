package flyer.worker.exception;

/**
 * @Package: flyer.worker.config.exception
 * @Description: ${todo}
 * @author: liuxin
 * @date: 2017/9/14 上午11:50
 */
public class IllegalhandlerException extends RuntimeException {
    public IllegalhandlerException() {

    }

    public IllegalhandlerException(String msg) {
        super(msg);
    }

    public IllegalhandlerException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}

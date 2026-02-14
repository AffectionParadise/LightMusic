package net.doge.exception.io;

/**
 * @Author Doge
 * @Description 超时异常
 * @Date 2020/12/20
 */
public class TimeoutRuntimeException extends RuntimeException {
    public TimeoutRuntimeException() {

    }

    public TimeoutRuntimeException(String msg) {
        super(msg);
    }
}

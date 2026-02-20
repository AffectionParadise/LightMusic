package net.doge.exception.io;

/**
 * @author Doge
 * @description 超时异常
 * @date 2020/12/20
 */
public class TimeoutRuntimeException extends RuntimeException {
    public TimeoutRuntimeException() {
    }

    public TimeoutRuntimeException(String msg) {
        super(msg);
    }
}

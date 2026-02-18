package net.doge.exception.io;

/**
 * @author Doge
 * @description 连接异常
 * @date 2020/12/20
 */
public class ConnectRuntimeException extends RuntimeException {
    public ConnectRuntimeException() {

    }

    public ConnectRuntimeException(String msg) {
        super(msg);
    }
}

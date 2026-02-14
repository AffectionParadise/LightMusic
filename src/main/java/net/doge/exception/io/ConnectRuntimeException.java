package net.doge.exception.io;

/**
 * @Author Doge
 * @Description 连接异常
 * @Date 2020/12/20
 */
public class ConnectRuntimeException extends RuntimeException {
    public ConnectRuntimeException() {

    }

    public ConnectRuntimeException(String msg) {
        super(msg);
    }
}

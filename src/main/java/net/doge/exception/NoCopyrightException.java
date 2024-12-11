package net.doge.exception;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/20
 */
public class NoCopyrightException extends RuntimeException {
    public NoCopyrightException() {

    }

    public NoCopyrightException(String msg) {
        super(msg);
    }
}

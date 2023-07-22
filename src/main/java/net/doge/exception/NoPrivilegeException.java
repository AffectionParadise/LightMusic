package net.doge.exception;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/20
 */
public class NoPrivilegeException extends RuntimeException {
    public NoPrivilegeException() {

    }

    public NoPrivilegeException(String msg) {
        super(msg);
    }
}

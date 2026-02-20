package net.doge.exception.media;

/**
 * @author Doge
 * @description 资源获取失败异常
 * @date 2020/12/20
 */
public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException() {
    }

    public InvalidResourceException(String msg) {
        super(msg);
    }
}

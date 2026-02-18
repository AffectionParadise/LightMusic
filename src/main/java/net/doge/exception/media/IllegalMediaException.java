package net.doge.exception.media;

/**
 * @author Doge
 * @description 媒体格式异常
 * @date 2020/12/20
 */
public class IllegalMediaException extends RuntimeException {
    public IllegalMediaException() {

    }

    public IllegalMediaException(String msg) {
        super(msg);
    }
}

package net.doge.exception.media;

/**
 * @Author Doge
 * @Description 媒体格式异常
 * @Date 2020/12/20
 */
public class IllegalMediaException extends RuntimeException {
    public IllegalMediaException() {

    }

    public IllegalMediaException(String msg) {
        super(msg);
    }
}

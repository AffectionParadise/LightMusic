package net.doge.exception.media;

/**
 * @Author Doge
 * @Description 歌曲无版权异常
 * @Date 2020/12/20
 */
public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException() {

    }

    public InvalidResourceException(String msg) {
        super(msg);
    }
}

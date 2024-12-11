package net.doge.exception;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/20
 */
public class InvalidPackageFileException extends RuntimeException {
    public InvalidPackageFileException() {

    }

    public InvalidPackageFileException(String msg) {
        super(msg);
    }
}

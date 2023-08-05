package net.doge.exception;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/20
 */
public class InValidPackageFileException extends RuntimeException {
    public InValidPackageFileException() {

    }

    public InValidPackageFileException(String msg) {
        super(msg);
    }
}

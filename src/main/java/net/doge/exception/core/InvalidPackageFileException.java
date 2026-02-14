package net.doge.exception.core;

/**
 * @Author Doge
 * @Description 无效包文件异常
 * @Date 2020/12/20
 */
public class InvalidPackageFileException extends RuntimeException {
    public InvalidPackageFileException() {

    }

    public InvalidPackageFileException(String msg) {
        super(msg);
    }
}

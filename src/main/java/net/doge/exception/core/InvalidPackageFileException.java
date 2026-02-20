package net.doge.exception.core;

/**
 * @author Doge
 * @description 无效包文件异常
 * @date 2020/12/20
 */
public class InvalidPackageFileException extends RuntimeException {
    public InvalidPackageFileException() {
    }

    public InvalidPackageFileException(String msg) {
        super(msg);
    }
}

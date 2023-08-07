package net.doge.util.system;

import info.monitorenter.cpdetector.io.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @Author Doge
 * @Description 文件工具类
 * @Date 2020/12/21
 */
public class FileUtil {
    /**
     * 获得不带后缀的文件路径
     */
    public static String getPathWithoutSuffix(File file) {
        String path = file.getPath();
        return path.substring(0, path.lastIndexOf('.'));
    }

    /**
     * 获得不带后缀的文件名
     */
    public static String getPrefix(File file) {
        return cn.hutool.core.io.FileUtil.getPrefix(file);
    }

    /**
     * 获得文件后缀名
     */
    public static String getSuffix(File file) {
        return cn.hutool.core.io.FileUtil.getSuffix(file).toLowerCase();
    }

    /**
     * 确保文件夹存在，若不存在则创建
     */
    public static void mkDir(String dirPath) {
        mkDir(new File(dirPath));
    }

    /**
     * 确保文件夹存在，若不存在则创建
     */
    public static void mkDir(File dir) {
        cn.hutool.core.io.FileUtil.mkdir(dir);
    }

    /**
     * 去掉文件名中的非法字符
     */
    public static String filterFileName(String fileName) {
        return cn.hutool.core.io.FileUtil.cleanInvalid(fileName);
    }

    /**
     * 替换文件扩展名
     */
    public static File replaceSuffix(File file, String suffix) {
        String path = file.getPath();
        int i = path.lastIndexOf('.');
        if (i == -1) return file;
        return new File(path.substring(0, i + 1) + suffix);
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    public static void delete(String path) {
        delete(new File(path));
    }

    /**
     * 删除文件
     *
     * @param f 文件
     */
    public static void delete(File f) {
        cn.hutool.core.io.FileUtil.del(f);
    }

    /**
     * 判断文件开头是不是 {
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean startsWithLeftBrace(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            int ch = fileReader.read();
            fileReader.close();
            return ch == '{';
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获得文件创建时间
     *
     * @param file
     * @return
     */
    public static long getCreationTime(File file) {
        try {
            Path path = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获得文件访问时间
     *
     * @param file
     * @return
     */
    public static long getAccessTime(File file) {
        try {
            Path path = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            return attr.lastAccessTime().toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取文件/文件夹大小
     *
     * @param file
     */
    public static long size(File file) {
        return cn.hutool.core.io.FileUtil.size(file);
    }

    /**
     * 转换大小单位，返回字符串，例如 1 B，1 K，1 M，1 G，1 T，1 P
     *
     * @param size
     * @return
     */
    public static String getUnitString(long size) {
        String us;
        if (size < 1024)
            us = String.format("%d B", size);
        else if (size < 1024 * 1024)
            us = String.format("%.1f K", (double) size / 1024);
        else if (size < 1024 * 1024 * 1024)
            us = String.format("%.1f M", (double) size / 1024 / 1024);
        else if (size < 1024L * 1024 * 1024 * 1024)
            us = String.format("%.1f G", (double) size / 1024 / 1024 / 1024);
        else if (size < 1024L * 1024 * 1024 * 1024 * 1024)
            us = String.format("%.1f T", (double) size / 1024 / 1024 / 1024 / 1024);
        else
            us = String.format("%.1f P", (double) size / 1024 / 1024 / 1024 / 1024 / 1024);
        return us.replace(".0", "");
    }

    /**
     * 清空文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static void clean(String dirPath) {
        cn.hutool.core.io.FileUtil.clean(new File(dirPath));
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dest
     */
    public static void copy(String src, String dest) {
        copy(new File(src), new File(dest));
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dest
     */
    public static void copy(File src, File dest) {
        cn.hutool.core.io.FileUtil.copy(src, dest, true);
    }

    /**
     * 从文件读取字符串
     *
     * @param file
     * @return
     */
    public static String readStr(File file) {
        return cn.hutool.core.io.FileUtil.readUtf8String(file);
    }

    /**
     * 从文件读取字符串，指定编码
     *
     * @param file
     * @return
     */
    public static String readStr(File file, String charsetName) {
        return cn.hutool.core.io.FileUtil.readString(file, Charset.forName(charsetName));
    }

    /**
     * 将字符串写入到文件
     *
     * @param content
     * @param path
     */
    public static void writeStr(String content, String path) {
        writeStr(content, new File(path));
    }

    /**
     * 将字符串写入到文件
     *
     * @param content
     * @param file
     */
    public static void writeStr(String content, File file) {
        cn.hutool.core.io.FileUtil.writeUtf8String(content, file);
    }

    /**
     * 从文件读取 bytes
     *
     * @param file
     * @return
     */
    public static byte[] readBytes(File file) {
        return cn.hutool.core.io.FileUtil.readBytes(file);
    }

    /**
     * 将 bytes 写入到文件
     *
     * @param data
     * @param file
     */
    public static void writeBytes(byte[] data, File file) {
        cn.hutool.core.io.FileUtil.writeBytes(data, file);
    }

    /**
     * 获取文件编码字符串
     *
     * @param file
     * @return
     */
    public static String getCharsetName(File file) {
        String charsetName = "UTF-8";
        try {
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
            detector.add(new ParsingDetector(false));
            detector.add(JChardetFacade.getInstance());
            detector.add(ASCIIDetector.getInstance());
            detector.add(UnicodeDetector.getInstance());
            Charset charset = detector.detectCodepage(file.toURI().toURL());
            if (charset != null) charsetName = charset.name();
            if ("windows-1252".equals(charsetName)) charsetName = "UTF-16";
            else if ("Big5".equals(charsetName)) charsetName = "GBK";
            return charsetName;
        } catch (Exception e) {
            return charsetName;
        }
    }
}

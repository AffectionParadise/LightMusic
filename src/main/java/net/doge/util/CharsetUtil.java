package net.doge.util;

import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @Author yzx
 * @Description 编码工具类
 * @Date 2020/12/9
 */
public class CharsetUtil {
    /**
     * 获取文件编码字符串
     *
     * @param file
     * @return
     */
    public static String getCharsetName(File file) throws IOException {
        String charsetName = "UTF-8";
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        Charset charset = detector.detectCodepage(file.toURI().toURL());
        if (charset != null) charsetName = charset.name();
        if("windows-1252".equals(charsetName)) charsetName = "UTF-16";
        else if("Big5".equals(charsetName)) charsetName = "GBK";
        return charsetName;
    }
}

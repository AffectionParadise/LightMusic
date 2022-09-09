package net.doge.utils;

import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/9
 */
public class CharsetUtils {
    /**
     * 转为 GB2312
     *
     * @param s
     * @return
     */
    public static String toGB2312(String s) {
        try {
            return new String(s.getBytes("ISO-8859-1"), "gb2312");
        } catch (Exception e) {
            return s;
        }
    }

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

    /**
     * 如果文件是 gbk 编码或者 gb2312 返回 true，反之 false
     *
     * @param file
     * @return
     */
    public static Boolean isGbk(File file) {
        boolean isGbk = true;
        byte[] buffer = readByteArrayData(file);
        int end = buffer.length;
        for (int i = 0; i < end; i++) {
            byte temp = buffer[i];
            if ((temp & 0x80) == 0) {
                continue;// B0A1-F7FE//A1A1-A9FE
            } else if ((Byte.toUnsignedInt(temp) < 0xAA && Byte.toUnsignedInt(temp) > 0xA0)
                    || (Byte.toUnsignedInt(temp) < 0xF8 && Byte.toUnsignedInt(temp) > 0xAF)) {
                if (i + 1 < end) {
                    if (Byte.toUnsignedInt(buffer[i + 1]) < 0xFF && Byte.toUnsignedInt(buffer[i + 1]) > 0xA0
                            && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                        i = i + 1;
                        continue;
                    }
                } // 8140-A0FE
            } else if (Byte.toUnsignedInt(temp) < 0xA1 && Byte.toUnsignedInt(temp) > 0x80) {
                if (i + 1 < end) {
                    if (Byte.toUnsignedInt(buffer[i + 1]) < 0xFF && Byte.toUnsignedInt(buffer[i + 1]) > 0x3F
                            && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                        i = i + 1;
                        continue;
                    }
                } // AA40-FEA0//A840-A9A0
            } else if ((Byte.toUnsignedInt(temp) < 0xFF && Byte.toUnsignedInt(temp) > 0xA9)
                    || (Byte.toUnsignedInt(temp) < 0xAA && Byte.toUnsignedInt(temp) > 0xA7)) {
                if (i + 1 < end) {
                    if (Byte.toUnsignedInt(buffer[i + 1]) < 0xA1 && Byte.toUnsignedInt(buffer[i + 1]) > 0x3F
                            && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                        i = i + 1;
                        continue;
                    }
                }
            }
            isGbk = false;
            break;
        }
        return isGbk;
    }

    /**
     * 从文件中直接读取字节
     *
     * @param file
     * @return
     */
    private static byte[] readByteArrayData(File file) {
        byte[] rebyte = null;
        BufferedInputStream bis;
        ByteArrayOutputStream output;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            output = new ByteArrayOutputStream();
            byte[] byt = new byte[1024 * 4];
            int len;
            try {
                while ((len = bis.read(byt)) != -1) {
                    if (len < 1024 * 4) {
                        output.write(byt, 0, len);
                    } else
                        output.write(byt);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            rebyte = output.toByteArray();
            if (bis != null) {
                bis.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rebyte;
    }

    /**
     * 判断是否为韩文字符
     *
     * @param u
     * @return
     */
    public static boolean isKnChar(int u) {
        return u >= 0xAC00 && u <= 0xD7A3 || u >= 0x3130 && u <= 0x318F;
    }
}

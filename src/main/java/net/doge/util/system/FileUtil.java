package net.doge.util.system;

import info.monitorenter.cpdetector.io.*;
import net.doge.model.lyric.Statement;
import net.doge.util.common.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @Author yzx
 * @Description 文件工具类
 * @Date 2020/12/21
 */
public class FileUtil {
    private static Pattern filePattern = Pattern.compile("[\\\\/:*?\"<>|]");

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
    public static String getNameWithoutSuffix(File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    /**
     * 获得文件后缀名
     */
    public static String getSuffix(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 确保文件夹存在，若不存在则创建
     */
    public static void makeSureDir(String dirPath) {
        makeSureDir(new File(dirPath));
    }

    /**
     * 确保文件夹存在，若不存在则创建
     */
    public static void makeSureDir(File dir) {
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * 去掉文件名中的非法字符
     */
    public static String filterFileName(String fileName) {
        return filePattern.matcher(fileName).replaceAll("");
    }

    /**
     * 替换文件扩展名
     */
    public static String replaceSuffix(String fileName, String suffix) {
        int i = fileName.lastIndexOf('.');
        if (i == -1) return fileName;
        return fileName.substring(0, i + 1) + suffix;
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
        f.delete();
    }

//    /**
//     * 删除文件夹
//     *
//     * @param dirPath 文件夹路径
//     */
//    public static void deleteDir(String dirPath) {
//        File file = new File(dirPath);
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            if (files != null) {
//                for (int i = 0, len = files.length; i < len; i++) {
//                    deleteDir(files[i].getAbsolutePath());
//                }
//            }
//        }
//        file.delete();
//    }

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
        if (file == null) return 0;

        try {
            Path path = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            long creationTime = attr.creationTime().toMillis();
            return creationTime;
        } catch (IOException e) {
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
        if (file == null) return 0;

        try {
            Path path = file.toPath();
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            long accessTime = attr.lastAccessTime().toMillis();
            return accessTime;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * 获取文件/文件夹大小
     *
     * @param file
     */
    public static long getDirOrFileSize(File file) {
        if (file == null || !file.exists()) return 0;
        if (file.isFile()) return file.length();
        File[] files = file.listFiles();
        long total = 0;
        if (files != null) {
            for (int i = 0, len = files.length; i < len; i++) {
                total += getDirOrFileSize(files[i]);
            }
        }
        return total;
    }

    /**
     * 转换大小单位，返回字符串，例如 1 B，1 KB，1 MB，1 GB，1 TB，1 PB
     *
     * @param size
     * @return
     */
    public static String getUnitString(long size) {
        if (size < 1024)
            return String.format("%s B", size);
        else if (size < 1024 * 1024)
            return String.format("%.2f K", (double) size / 1024);
        else if (size < 1024 * 1024 * 1024)
            return String.format("%.2f M", (double) size / 1024 / 1024);
        else if (size < 1024L * 1024 * 1024 * 1024)
            return String.format("%.2f G", (double) size / 1024 / 1024 / 1024);
        else if (size < 1024L * 1024 * 1024 * 1024 * 1024)
            return String.format("%.2f T", (double) size / 1024 / 1024 / 1024 / 1024);
        else
            return String.format("%.2f P", (double) size / 1024 / 1024 / 1024 / 1024 / 1024);
    }

    /**
     * 深度删除文件夹内的所有文件，但不删除文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static void deepDeleteFiles(String dirPath) {
        File file = new File(dirPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, len = files.length; i < len; i++) {
                    deepDeleteFiles(files[i].getAbsolutePath());
                }
            }
        } else file.delete();
    }

    /**
     * 复制文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copy(String source, String dest) throws IOException {
        Files.copy(Paths.get(source), new BufferedOutputStream(new FileOutputStream(dest)));
    }

    /**
     * 从文件读取字符串
     *
     * @param f
     * @throws IOException
     */
    public static String readAsStr(File f) {
        if (f == null) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将字符串写入到文件，是否追加
     *
     * @param str
     * @param dest
     * @throws IOException
     */
    public static void writeStr(String str, String dest, boolean append) {
        if (str == null) return;
        File file = new File(dest);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
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
        if ("windows-1252".equals(charsetName)) charsetName = "UTF-16";
        else if ("Big5".equals(charsetName)) charsetName = "GBK";
        return charsetName;
    }

    /**
     * 从文件或歌词字符串读取歌词（不支持滚动时）
     *
     * @param fileNameOrStr
     * @param isFile
     * @return
     * @throws IOException
     */
    public static Vector<Statement> getBadFormatStatements(String fileNameOrStr, boolean isFile) {
        Vector<Statement> statements = new Vector<>();
        try {
            BufferedReader bufferReader;
            if (isFile) {
                File f = new File(fileNameOrStr);
                FileInputStream fis = new FileInputStream(f);
                // 获取文件编码并读取歌词
                bufferReader = new BufferedReader(new InputStreamReader(fis, getCharsetName(f)));
            } else {
                bufferReader = new BufferedReader(new StringReader(fileNameOrStr));
            }
            String strLine;
            while (null != (strLine = bufferReader.readLine())) {
                if (StringUtil.isEmpty(strLine.trim())) continue;

                Statement stmt = new Statement();
                stmt.setLyric(strLine);

                statements.add(stmt);
            }
            bufferReader.close();
        } catch (IOException e) {

        } finally {
            return statements;
        }
    }
}

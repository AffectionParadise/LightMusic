package net.doge.models.lyric;

import lombok.Data;
import net.doge.utils.CharsetUtil;
import net.doge.utils.StringUtil;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class LrcData {
    // 读取文件实例
    private BufferedReader bufferReader;
    // 歌曲题目
    private String title = "";
    // 演唱者
    private String artist = "";
    // 专辑
    private String album = "";
    // 歌词制作者
    private String lrcMaker = "";
    // 歌词
    private Vector<Statement> statements = new Vector<>();
    // 翻译
    private Vector<Statement> transStatements = new Vector<>();
    // lrc 文件内容
    private String lrcStr = "";

    public boolean hasTrans() {
        return !transStatements.isEmpty();
    }

    public boolean isEmpty() {
        return statements.isEmpty();
    }

    /*
     * 实例化一个歌词数据. 歌词数据信息由指定的文件提供.
     * fileName: 指定的歌词文件.
     */
    public LrcData(String fileNameOrStr, boolean isFile) throws IOException {
        if (isFile) {
            File f = new File(fileNameOrStr);
            FileInputStream fis = new FileInputStream(f);
            // 获取文件编码并读取歌词
            bufferReader = new BufferedReader(new InputStreamReader(fis, CharsetUtil.getCharsetName(f)));
        } else {
            bufferReader = new BufferedReader(new StringReader(fileNameOrStr));
        }
        // 将文件数据读入内存
        readData();
        bufferReader.close();
    }

    /**
     * 判断是否为有效行
     *
     * @param line
     * @return
     */
    public boolean isValidLine(String line) {
        return !"[]".equals(line);
    }

    /*
     * 读取文件中数据至内存.
     */
    public void readData() throws IOException {
        statements.clear();
        String strLine;
        StringBuffer sb = new StringBuffer();
        // 循环读入所有行
        while (null != (strLine = bufferReader.readLine())) {
            sb.append(strLine + "\n");
            // 把 tab 先移除，去掉两边空格(包含特殊空格)
            strLine = StringUtil.trimStringWith(strLine.replace("\t", "").trim(), ' ');
            // 判断该行是否为有效行
            if (!isValidLine(strLine)) continue;
            // 判断该行数据是否表示歌名
            if (null == title || title.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("\\[ti:(.+?)\\]");
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.find()) {
                    title = matcher.group(1);
                    continue;
                }
            }
            // 判断该行数据是否表示演唱者
            if (null == artist || artist.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("\\[ar:(.+?)\\]");
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.find()) {
                    artist = matcher.group(1);
                    continue;
                }
            }
            // 判断该行数据是否表示专辑
            if (null == album || album.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("\\[al:(.+?)\\]");
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.find()) {
                    album = matcher.group(1);
                    continue;
                }
            }
            // 判断该行数据是否表示歌词制作者
            if (null == lrcMaker || lrcMaker.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("\\[by:(.+?)\\]");
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.find()) {
                    lrcMaker = matcher.group(1);
                    continue;
                }
            }
            // 读取并分析歌词
            int timeNum = 0;                                        // 本行含时间个数
            String str[] = strLine.split("\\]");              // 以 ] 分隔
            for (int i = 0; i < str.length; ++i) {
                String str2[] = str[i].split("\\[");          // 以 [ 分隔
                if (str2.length == 0) continue;
                str[i] = str2[str2.length - 1];
                if (isTime(str[i])) {
                    ++timeNum;
                }
            }
            for (int i = 0; i < timeNum; ++i)                    // 处理歌词复用的情况
            {
                Statement sm = new Statement();
                // 设置歌词，包含 ""
                sm.setLyric(timeNum < str.length ? str[str.length - 1].trim() : "");
                // 设置时间
                sm.setTime(str[i]);
                statements.add(sm);
            }
        }
        lrcStr = sb.toString();

        // 将读取的歌词按时间排序
        sortLyric();

        // 清洗歌词，删除空行并设置结束时间
        clean();

        // 添加歌词排头的等待点
        if (!statements.isEmpty() && statements.get(0).getTime() >= 3) statements.add(0, new Statement(0, "•••"));

        // 分离出歌词翻译
//        parseTrans();
    }

    /*
     * 判断给定的字符串是否表示时间
     * 00:00.000000 或 00:00
     */
    public boolean isTime(String string) {
        // 可能有 [] 的情况，先判空
        if (StringUtil.isEmpty(string)) return false;
        String str[] = string.split(":|\\.");
        if (3 != str.length && 2 != str.length) {
            return false;
        }
        try {
            for (int i = 0; i < str.length; ++i) {
                Integer.parseInt(str[i]);
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /*
     * 将读取的歌词按时间排序.
     */
    public void sortLyric() {
        Collections.sort(statements, Comparator.comparingDouble(Statement::getTime));
    }

    /**
     * 清洗歌词，移除空行并设置结束时间
     */
    public void clean() {
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            if (stmt.getLyric().isEmpty()) {
                if (i > 0) {
                    Statement ls = statements.get(i - 1);
                    if (!ls.hasEndTime()) ls.setEndTime(stmt.getTime());
                }
                statements.remove(i--);
            }
        }
    }

    // 提取翻译
//    void parseTrans() {
//        try {
//            boolean hasTrans = false;
//            Double lastTime = 0D;
//            for (int i = 0; i < statements.size(); i++) {
//                Statement stmt = statements.get(i);
//                Statement nextStmt = null;
//                if (i + 1 < statements.size()) nextStmt = statements.get(i + 1);
//                Double time = stmt.getTime();
//                Double nextTime = null;
//                if (nextStmt != null)
//                    nextTime = nextStmt.getTime();
//                // 歌词中带有翻译，有多个 time 相同的歌词时取重复的第一个；最后一句也是翻译
//                if (hasTrans && nextTime == null || TimeUtils.formatToLrcTime(time).equals(TimeUtils.formatToLrcTime(nextTime))) {
//                    stmt.setTime(lastTime);
//                    transStatements.add(stmt);
//                    // 从原歌词的集合中剔除翻译
//                    statements.remove(i--);
//                    hasTrans = true;
//                }
//                lastTime = time;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 得到 lrc 文件内容
     */
    public String getLrcStr() {
        return lrcStr;
    }
}
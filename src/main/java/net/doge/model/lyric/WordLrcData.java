package net.doge.model.lyric;

import cn.hutool.core.util.ReUtil;
import lombok.Data;
import net.doge.util.common.StringUtil;
import net.doge.util.system.FileUtil;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * @Author yzx
 * @Description 逐字歌词解析
 * @Date 2020/12/7
 */
@Data
public class WordLrcData {
    // 读取文件实例
    private BufferedReader bufferReader;
    // 歌词
    Vector<Statement> statements = new Vector<>();
    // 翻译
    Vector<Statement> transStatements = new Vector<>();
    // lrc 文件内容
    public String lrcStr = "";

    public boolean hasTrans() {
        return !transStatements.isEmpty();
    }

    /*
     * 实例化一个歌词数据. 歌词数据信息由指定的文件提供.
     * fileName: 指定的歌词文件.
     */
    public WordLrcData(String fileNameOrStr, boolean isFile) throws IOException {
        if (isFile) {
            File f = new File(fileNameOrStr);
            FileInputStream fis = new FileInputStream(f);
            // 获取文件编码并读取歌词
            bufferReader = new BufferedReader(new InputStreamReader(fis, FileUtil.getCharsetName(f)));
        } else {
            bufferReader = new BufferedReader(new StringReader(fileNameOrStr));
        }
        // 将文件数据读入内存
        readData();
        bufferReader.close();
    }

    private double toSeconds(String s) {
        return Double.parseDouble(s) / 1000;
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

            String startTimeStr = ReUtil.get("\\[(\\d+),\\d+\\]", strLine, 1);
            // 不是有效的歌词行
            if (StringUtil.isEmpty(startTimeStr)) continue;

            double startTime = toSeconds(startTimeStr);

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

    /**
     * 得到 lrc 文件内容
     */
    public String getLrcStr() {
        return lrcStr;
    }
}
package net.doge.model.lyric;

import lombok.Data;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import net.doge.util.system.FileUtil;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Doge
 * @Description 歌词数据
 * @Date 2020/12/7
 */
@Data
public class LrcData {
    // 歌词
    private List<Statement> statements = new LinkedList<>();
    // lrc 文件内容
    private String lrcStr;

    // 偏移
    private double offset;

    public boolean isEmpty() {
        return statements.isEmpty();
    }

    public LrcData(File lrcFile) {
        this(lrcFile, false);
    }

    public LrcData(File lrcFile, boolean badFormat) {
        this(FileUtil.readStr(lrcFile, FileUtil.getCharsetName(lrcFile)), badFormat);
    }

    public LrcData(String lrcStr) {
        this(lrcStr, false);
    }

    public LrcData(String lrcStr, boolean badFormat) {
        initData(lrcStr, badFormat);
    }

    // 初始化歌词数据
    private void initData(String lrcStr, boolean badFormat) {
        this.lrcStr = lrcStr.replaceAll("<\\d+,\\d+>", "");
        String[] lrcArray = lrcStr.split("\r?\n");
        // 不支持滚动的歌词，直接读取整行作为歌词
        if (badFormat) {
            for (String line : lrcArray) {
                line = StringUtil.cleanLrcStr(line);
                if (StringUtil.isEmpty(line)) continue;
                statements.add(new Statement(line));
            }
        }
        // 解析完整的歌词
        else {
            for (String line : lrcArray) {
                line = StringUtil.cleanLrcStr(line);
                if (StringUtil.isEmpty(line)) continue;

                // 解析 offset
                String offsetStr = RegexUtil.getGroup1("\\[offset:(-?\\d+)\\]", line);
                if (StringUtil.notEmpty(offsetStr)) offset = Double.parseDouble(offsetStr) / 1000;

                List<String> timeStrList = RegexUtil.findAllGroup1("\\[(\\d+:\\d+(?:[.:]\\d+)?)\\]", line);
                int lineLrcStart = line.lastIndexOf(']') + 1;
                String lineLrc = lineLrcStart < line.length() ? StringUtil.shortenBlank(line.substring(lineLrcStart).trim()) : "";
                // 循环是为了处理歌词复用情况
                timeStrList.forEach(timeStr -> {
                    double time = TimeUtil.lrcTimeToSeconds(timeStr) - offset;
                    statements.add(new Statement(time, lineLrc));
                });
            }
            // 将读取的歌词按时间排序
            sortLyric();
            // 清洗歌词，删除空行并设置结束时间
            clean();
            // 添加歌词排头的等待点
            if (!statements.isEmpty() && statements.get(0).getTime() >= 3) statements.add(0, new Statement(0, "···"));
        }
    }

    // 按时间排序歌词
    private void sortLyric() {
        statements.sort(Comparator.comparingDouble(Statement::getTime));
    }

    // 清洗歌词，移除空行并设置结束时间
    private void clean() {
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            if (stmt.notEmpty()) continue;
            if (i > 0) {
                Statement ls = statements.get(i - 1);
                if (!ls.hasEndTime()) ls.setEndTime(stmt.getTime());
            }
            statements.remove(i--);
        }
    }
}
package net.doge.entity.core.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.text.LyricUtil;
import net.doge.util.media.DurationUtil;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Doge
 * @description 歌词数据
 * @date 2020/12/7
 */
@Data
public class LyricData {
    // 歌词
    private List<Statement> statements = new LinkedList<>();
    // lrc 文件内容
    private String lyricStr;
    // 偏移
    private double offset;

    public LyricData(File lyricFile) {
        this(lyricFile, false);
    }

    public LyricData(File lyricFile, boolean badFormat) {
        this(FileUtil.readStr(lyricFile, FileUtil.getCharsetName(lyricFile)), badFormat);
    }

    public LyricData(String lyricStr) {
        this(lyricStr, false);
    }

    public LyricData(String lyricStr, boolean badFormat) {
        initData(lyricStr, badFormat);
    }

    public boolean isEmpty() {
        return statements.isEmpty();
    }

    // 初始化歌词数据
    private void initData(String lyricStr, boolean badFormat) {
        this.lyricStr = lyricStr.replaceAll(LyricPattern.PAIR, "");
        String[] lyricArray = lyricStr.split("\r?\n");
        // 不支持滚动的歌词，直接读取整行作为歌词
        if (badFormat) {
            for (String line : lyricArray) {
                line = LyricUtil.cleanLyricStr(line);
                if (StringUtil.isEmpty(line)) continue;
                statements.add(new Statement(line));
            }
        }
        // 解析完整的歌词
        else {
            for (String line : lyricArray) {
                line = LyricUtil.cleanLyricStr(line);
                if (StringUtil.isEmpty(line)) continue;

                // 解析 offset
                String offsetStr = RegexUtil.getGroup1("\\[offset:(-?\\d+)\\]", line);
                if (StringUtil.notEmpty(offsetStr)) offset = Double.parseDouble(offsetStr) / 1000;

                List<String> timeStrList = RegexUtil.findAllGroup1("\\[(\\d+:\\d+(?:[.:]\\d+)?)\\]", line);
                int lineLyricStart = line.lastIndexOf(']') + 1;
                String lineLyric = lineLyricStart < line.length() ? StringUtil.shortenBlank(line.substring(lineLyricStart).trim()) : "";
                // 循环是为了处理歌词复用情况
                timeStrList.forEach(timeStr -> {
                    double time = DurationUtil.lyricTimeToSeconds(timeStr) - offset;
                    statements.add(new Statement(time, lineLyric));
                });
            }
            // 将读取的歌词按时间排序
            sortLyric();
            // 清洗歌词，删除空行并设置结束时间
            clean();
            // 添加歌词排头的呼吸点
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
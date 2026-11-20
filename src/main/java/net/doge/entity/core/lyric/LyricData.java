package net.doge.entity.core.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.constant.core.lyric.LyricState;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.collection.ListUtil;
import net.doge.util.core.text.LyricUtil;
import net.doge.util.media.DurationUtil;

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
    // 歌词状态
    private int lyricState = LyricState.OK;
    // 子歌词状态
    private int subLyricState = LyricState.OK;
    // 整合后的歌词
    private List<Statement> statements;
    // lrc 文件内容
    private String lyricStr;
    // 偏移
    private double offset;

    public LyricData(String lyricStr) {
        initData(lyricStr, null);
    }

    public LyricData(String lyricStr, String subLyricStr) {
        initData(lyricStr, subLyricStr);
    }

    public boolean isEmpty() {
        return statements.isEmpty();
    }

    // 初始化歌词数据
    private void initData(String lyricStr, String subLyricStr) {
        this.lyricStr = lyricStr;
        statements = parse(lyricStr, false);
        List<Statement> subLyricStmts = parse(subLyricStr, true);
        // 合并解析的所有歌词信息
        // 子歌词不支持滚动时，只能进行一一对应
        if (subLyricState == LyricState.BAD_FORMAT) {
            for (int i = 0, s = statements.size(); i < s; i++) {
                Statement lyricStmt = statements.get(i);
                Statement subLyricStmt = subLyricStmts.get(i);
                lyricStmt.setSubLyric(subLyricStmt.getLyric());
            }
        }
        // 子歌词支持滚动时，查找匹配的歌词
        else {
            subLyricStmts.forEach(subLyricStmt -> {
                int i = ListUtil.binarySearchLowBound(statements, subLyricStmt);
                if (i >= 0) {
                    Statement lyricStmt = statements.get(i);
                    lyricStmt.setSubLyric(subLyricStmt.getLyric());
                }
            });
        }
        // 将读取的歌词按时间排序
        sortLyric();
        // 添加歌词排头的呼吸点
        if (!statements.isEmpty() && statements.get(0).getTime() >= 3) statements.add(0, new Statement(0, "···"));
    }

    private List<Statement> parse(String lyricStr, boolean isSubLyric) {
        List<Statement> stmts = new LinkedList<>();
        if (StringUtil.isEmpty(lyricStr)) {
            if (isSubLyric) subLyricState = LyricState.NOT_EXISTS;
            else lyricState = LyricState.NOT_EXISTS;
            return stmts;
        }
        // 子歌词过滤掉逐字时间轴
        if (isSubLyric) lyricStr = lyricStr.replaceAll(LyricPattern.PAIR_FIND, "");
        // 解析完整的歌词
        String[] lyricArray = ArrayUtil.removeLastEmpty(lyricStr.split("\\R", -1));
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
                stmts.add(new Statement(time, lineLyric));
            });
        }
        // 清洗歌词，删除空行并设置结束时间
        clean(stmts);
        // 解析之后若 Statement 列表为空，则为不支持滚动，直接读取整行作为歌词
        if (stmts.isEmpty()) {
            for (String line : lyricArray) {
                // 没有时间轴信息时，翻译的空行需要保留
                if (!isSubLyric) {
                    line = LyricUtil.cleanLyricStr(line);
                    if (StringUtil.isEmpty(line)) continue;
                }
                stmts.add(new Statement(line));
            }
            if (isSubLyric) subLyricState = LyricState.BAD_FORMAT;
            else lyricState = LyricState.BAD_FORMAT;
        }
        return stmts;
    }

    // 清洗歌词，移除空行并设置结束时间
    private void clean(List<Statement> stmts) {
        for (int i = 0; i < stmts.size(); i++) {
            Statement stmt = stmts.get(i);
            if (stmt.notEmpty()) continue;
            if (i > 0) {
                Statement ls = stmts.get(i - 1);
                if (!ls.hasEndTime()) ls.setEndTime(stmt.getTime());
            }
            stmts.remove(i--);
        }
    }

    // 按时间排序歌词
    private void sortLyric() {
        statements.sort(Comparator.comparingDouble(Statement::getTime));
    }
}
package net.doge.entity.core.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.constant.core.lyric.LyricState;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.text.LyricUtil;
import net.doge.util.media.DurationUtil;

import java.util.ArrayList;
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
        // 歌词与子歌词数量相同时，只能进行一一对应
        int lyricsSize = statements.size(), subLyricsSize = subLyricStmts.size();
        if (lyricsSize == subLyricsSize) {
            for (int i = 0; i < lyricsSize; i++) statements.get(i).setSubLyric(subLyricStmts.get(i).getLyric());
        }
        // 数量不一致时，根据时间轴动态规划查找匹配的歌词
        else if (!statements.isEmpty() && !subLyricStmts.isEmpty()) matchTimedLyrics(subLyricStmts);
        // 添加歌词排头的呼吸点
        if (!statements.isEmpty() && statements.get(0).getTime() >= 3) statements.add(0, new Statement(0, "···"));
    }

    private void matchTimedLyrics(List<Statement> subLyricStmts) {
        List<Statement> lyrics = new ArrayList<>(statements);
        List<Statement> subLyrics = new ArrayList<>(subLyricStmts);
        int lyricIndex = 0, subLyricsIndex = 0;
        // 两组歌词已按时间排序，跨时间组的歌词无需进入同一个动态规划表
        int lyricsSize = lyrics.size(), subLyricsSize = subLyrics.size();
        while (lyricIndex < lyricsSize && subLyricsIndex < subLyricsSize) {
            long lyricTime = centisecondKey(lyrics.get(lyricIndex));
            long translationTime = centisecondKey(subLyrics.get(subLyricsIndex));
            if (lyricTime < translationTime) lyricIndex++;
            else if (lyricTime > translationTime) subLyricsIndex++;
            else {
                int lyricEnd = timeGroupEnd(lyrics, lyricIndex, lyricTime);
                int translationEnd = timeGroupEnd(subLyrics, subLyricsIndex, translationTime);
                matchTimeGroup(lyrics.subList(lyricIndex, lyricEnd), subLyrics.subList(subLyricsIndex, translationEnd));
                lyricIndex = lyricEnd;
                subLyricsIndex = translationEnd;
            }
        }
    }

    private static long centisecondKey(Statement stmt) {
        double centiseconds = stmt.getTime() * 100;
        // 截取到百分之一秒，同时抵消浮点换算在边界上的微小误差
        return (long) (centiseconds + Math.copySign(1e-9, centiseconds));
    }

    private static int timeGroupEnd(List<Statement> stmts, int start, long time) {
        int end = start + 1;
        while (end < stmts.size() && centisecondKey(stmts.get(end)) == time) end++;
        return end;
    }

    private static void matchTimeGroup(List<Statement> lyrics, List<Statement> subLyrics) {
        int lyricsSize = lyrics.size(), subLyricsSize = subLyrics.size();
        if (lyricsSize == subLyricsSize) {
            for (int i = 0; i < lyricsSize; i++) lyrics.get(i).setSubLyric(subLyrics.get(i).getLyric());
            return;
        }

        boolean fewerLyrics = lyricsSize < subLyricsSize;
        List<Statement> shorter = fewerLyrics ? lyrics : subLyrics;
        List<Statement> longer = fewerLyrics ? subLyrics : lyrics;
        int longerSize = longer.size(), shoterSize = shorter.size();
        int extraRows = longerSize - shoterSize;
        boolean[][] matched = new boolean[shoterSize][extraRows + 1];
        double[] previous = new double[extraRows + 1], current = new double[extraRows + 1];

        // 状态 (i, skipped) 的代价是匹配前 i 行短序列、跳过长序列中 skipped 行的最小总时间差
        for (int i = 1; i <= shoterSize; i++) {
            double time = shorter.get(i - 1).getTime();
            for (int skipped = 0; skipped <= extraRows; skipped++) {
                double matchCost = previous[skipped] + Math.abs(time - longer.get(i + skipped - 1).getTime());
                double skipCost = skipped == 0 ? Double.POSITIVE_INFINITY : current[skipped - 1];
                matched[i - 1][skipped] = matchCost <= skipCost;
                current[skipped] = Math.min(matchCost, skipCost);
            }
            double[] temp = previous;
            previous = current;
            current = temp;
        }

        // 从最优状态回溯，按顺序一对一配对
        for (int i = shoterSize, skipped = extraRows; i > 0; ) {
            if (matched[i - 1][skipped]) {
                Statement shortStmt = shorter.get(i - 1);
                Statement longStmt = longer.get(i + skipped - 1);
                if (fewerLyrics) shortStmt.setSubLyric(longStmt.getLyric());
                else longStmt.setSubLyric(shortStmt.getLyric());
                i--;
            } else skipped--;
        }
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
        // 将读取的歌词按时间排序
        stmts.sort(null);
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
}

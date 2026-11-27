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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Doge
 * @description 歌词数据
 * @date 2020/12/7
 */
@Data
public class LyricData {
    private static final double MAX_TIME_DIFF_SECONDS = 2.5;
    private static final byte SKIP_LYRIC = 0;
    private static final byte SKIP_SUB_LYRIC = 1;
    private static final byte MATCH = 2;

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
        if (lyricState == LyricState.OK && subLyricState == LyricState.OK) {
            matchTimedLyrics(subLyricStmts);
        }
        // 缺少时间轴且行数一致时，按行合并
        else if (statements.size() == subLyricStmts.size()) {
            Iterator<Statement> subLyrics = subLyricStmts.iterator();
            for (Statement stmt : statements) stmt.setSubLyric(subLyrics.next().getLyric());
        }
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

    // 子歌词支持滚动时，根据时间轴查找匹配的歌词
    private void matchTimedLyrics(List<Statement> subLyricStmts) {
        List<Statement> lyrics = new ArrayList<>(statements);
        List<Statement> subLyrics = new ArrayList<>(subLyricStmts);
        int lyricsSize = lyrics.size(), subLyricsSize = subLyrics.size();
        byte[][] choices = new byte[lyricsSize + 1][subLyricsSize + 1];
        double[] previous = new double[subLyricsSize + 1], current = new double[subLyricsSize + 1];

        // 比较两边前 i、j 行的匹配方案。匹配得分为容差减去时间差，跳过任一行不得分
        for (int i = 1; i <= lyricsSize; i++) {
            double lyricTime = lyrics.get(i - 1).getTime();
            for (int j = 1; j <= subLyricsSize; j++) {
                double score = previous[j];
                byte choice = SKIP_LYRIC;
                if (current[j - 1] > score) {
                    score = current[j - 1];
                    choice = SKIP_SUB_LYRIC;
                }
                double timeDiff = Math.abs(lyricTime - subLyrics.get(j - 1).getTime());
                if (timeDiff <= MAX_TIME_DIFF_SECONDS) {
                    double matchScore = previous[j - 1] + (MAX_TIME_DIFF_SECONDS - timeDiff);
                    if (matchScore >= score) {
                        score = matchScore;
                        choice = MATCH;
                    }
                }
                choices[i][j] = choice;
                current[j] = score;
            }
            double[] temp = previous;
            previous = current;
            current = temp;
        }

        // 回溯得分最高的方案，每行最多使用一次
        for (int i = lyricsSize, j = subLyricsSize; i > 0 && j > 0; ) {
            byte choice = choices[i][j];
            if (choice == MATCH) {
                lyrics.get(i - 1).setSubLyric(subLyrics.get(j - 1).getLyric());
                i--;
                j--;
            } else if (choice == SKIP_LYRIC) i--;
            else j--;
        }
    }
}

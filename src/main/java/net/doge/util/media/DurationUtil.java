package net.doge.util.media;

import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;

import java.util.List;

/**
 * @author Doge
 * @description 时长转换工具类
 * @date 2020/12/15
 */
public class DurationUtil {
    // 标准歌词时间正则
    private static final String LYRIC_TIME_EXP = "\\[\\]";

    /**
     * 转换秒为 mm:ss 格式
     *
     * @param seconds
     * @return
     */
    public static String format(double seconds) {
        StringBuilder sb = new StringBuilder();
        int secondsIntPart = (int) seconds;
        int minutePart = secondsIntPart / 60, secondPart = secondsIntPart % 60;
        if (minutePart < 10) sb.append("0");
        sb.append(minutePart).append(":");
        if (secondPart < 10) sb.append("0");
        sb.append(secondPart);
        return sb.toString();
    }

    /**
     * 转换 hh:mm:ss / hh 时 mm 分 ss 秒 等格式为秒
     *
     * @param s
     * @return
     */
    public static double toSeconds(String s) {
        if (StringUtil.isEmpty(s)) return 0;
        List<String> groups = RegexUtil.findAllGroup0("\\d+", s);
        double res = 0, u = 1;
        for (int i = groups.size() - 1; i >= 0; i--) {
            res += Integer.parseInt(groups.get(i)) * u;
            u *= 60;
        }
        return res;
    }

    /**
     * 转换 hh 时 mm 分 ss 秒 / hh 时 ss 秒 / hh 时 mm 分 等中文格式为秒
     *
     * @param s
     * @return
     */
    public static double chineseToSeconds(String s) {
        s = s.replaceFirst("时长：", "");
        List<String> groups = RegexUtil.findAllGroup0("\\d+", s);
        if (groups.size() == 1) {
            int first = Integer.parseInt(groups.get(0));
            if (s.contains("秒")) return first;
            else if (s.contains("分")) return first * 60;
            else return first * 3600;
        } else if (groups.size() == 2) {
            int first = Integer.parseInt(groups.get(0)), second = Integer.parseInt(groups.get(1));
            if (s.contains("时") && s.contains("分")) return first * 3600 + second * 60;
            else if (s.contains("时") && s.contains("秒")) return first * 3600 + second;
            else return first * 60 + second;
        } else
            return Integer.parseInt(groups.get(0)) * 3600 + Integer.parseInt(groups.get(1)) * 60 + Integer.parseInt(groups.get(2));
    }

    /**
     * 转换秒为 [mm:ss.xx(ms)] 标准歌词时间格式
     *
     * @param seconds
     * @return
     */
    public static String formatToLyricTime(Double seconds) {
        if (seconds == null) return "";
        if (seconds < 0) seconds = 0D;
        StringBuilder sb = new StringBuilder();
        int secondsIntPart = seconds.intValue();
        String secondsStr = String.valueOf(seconds);
        int minutePart = secondsIntPart / 60, secondPart = secondsIntPart % 60;
        String millisecondsPart = secondsStr.substring(secondsStr.lastIndexOf('.'));
        sb.append("[");
        if (minutePart < 10) sb.append("0");
        sb.append(minutePart).append(":");
        if (secondPart < 10) sb.append("0");
        sb.append(secondPart);
        sb.append(millisecondsPart);
        // 毫秒部分至少保留 2 位
        if (millisecondsPart.length() == 2) sb.append("0");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 转换标准歌词时间 mm:ss.xx(ms) 为秒
     *
     * @param s
     * @return
     */
    public static double lyricTimeToSeconds(String s) {
        String[] parts = StringUtil.trimStringWith(s, '[', ']').split("[.:]");
        int secondsIntPart = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        // mm:ss
        if (parts.length == 2) return secondsIntPart;
        // mm:ss.xx(ms)
        return secondsIntPart + Double.parseDouble("0." + parts[2]);
    }
}

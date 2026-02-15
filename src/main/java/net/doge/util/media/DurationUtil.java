package net.doge.util.media;

import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;

import java.util.List;

/**
 * @Author Doge
 * @Description 时长转换工具类
 * @Date 2020/12/15
 */
public class DurationUtil {
    /**
     * 转换秒为 xx(m):xx(s) 格式
     *
     * @param seconds
     * @return
     */
    public static String format(double seconds) {
        StringBuilder sb = new StringBuilder();
        int s = (int) seconds;
        int minute = s / 60, second = s % 60;
        if (minute < 10) sb.append("0");
        sb.append(minute).append(":");
        if (second < 10) sb.append("0");
        sb.append(second);
        return sb.toString();
    }

    /**
     * 转换 xx(h):xx(m):xx(s) / xx 时 xx 分 xx 秒 等格式为秒
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
     * 转换 xx 时 xx 分 xx 秒 / xx 时 xx 秒 / xx 时 xx 分 等中文格式为秒
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
     * 转换秒为 [xx(m):xx(s).xx(ms)] 歌词时间格式
     *
     * @param seconds
     * @return
     */
    public static String formatToLyricTime(Double seconds) {
        if (seconds == null) return "";
        if (seconds < 0) seconds = 0D;
        StringBuilder sb = new StringBuilder();
        int s = seconds.intValue();
        String s1 = String.valueOf(seconds);
        int minute = s / 60, second = s % 60;
        String milliseconds = s1.substring(s1.lastIndexOf('.'));
        sb.append("[");
        if (minute < 10) sb.append("0");
        sb.append(minute).append(":");
        if (second < 10) sb.append("0");
        sb.append(second);
        sb.append(milliseconds.length() == 2 ? milliseconds + "0" : milliseconds);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 转换歌词时间 xx:xx.xxx 为秒
     *
     * @param s
     * @return
     */
    public static double lyricTimeToSeconds(String s) {
        String[] sp = StringUtil.trimStringWith(s, '[', ']').split("[.:]");
        // xx:xx
        if (sp.length == 2) return Integer.parseInt(sp[0]) * 60 + Integer.parseInt(sp[1]);
        // xx:xx.xxx
        return Integer.parseInt(sp[0]) * 60 + Integer.parseInt(sp[1]) + Double.parseDouble("0." + sp[2]);
    }
}

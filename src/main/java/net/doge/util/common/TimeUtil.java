package net.doge.util.common;

import cn.hutool.core.date.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author yzx
 * @Description 时间转换工具类
 * @Date 2020/12/15
 */
public class TimeUtil {
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dateShortTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat shortDateFormatter = new SimpleDateFormat("MM-dd");
    private static DateFormat shortDateShortTimeFormatter = new SimpleDateFormat("MM-dd HH:mm");

    /**
     * 转换毫秒为时间短语
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToPhrase(long ms) {
        TimeUnit unit = TimeUnit.MILLISECONDS;
        long now = System.currentTimeMillis();
        long l = now - ms;

        long seconds = unit.toSeconds(l);
        if (seconds < 30) return "刚刚";
        if (seconds < 60) return seconds + "秒前";
        long minutes = unit.toMinutes(l);
        if (minutes < 60) return minutes + "分钟前";
        long hours = unit.toHours(l);
        if (hours < 24) return hours + "小时前";
        long days = unit.toDays(l);
        if (days < 7) return days + "天前";
        // 同一年份省掉年的显示
        if (msToYear(ms) == msToYear(now)) return msToShortDateShortTime(ms);
        return msToDateShortTime(ms);
    }

    /**
     * 转换时间字符串为时间短语
     *
     * @param str 时间字符串
     * @return
     */
    public static String strToPhrase(String str) {
        try {
            int index = str.indexOf('-');
            if (index < 0) return str;
            // MM-dd 转为 yyyy-MM-dd
            if (index < 3) str = Calendar.getInstance().get(Calendar.YEAR) + "-" + str;
            // yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd HH:mm
            DateFormat fmt;
            if (!str.contains(" ")) fmt = dateFormatter;
            else if (str.split(":").length > 2) fmt = formatter;
            else fmt = dateShortTimeFormatter;
            Date date = fmt.parse(str);
            return msToPhrase(date.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 转换毫秒为日期时间
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToDatetime(long ms) {
        if (ms < 0) return null;
        return formatter.format(new Date(ms));
    }

    /**
     * 转换毫秒为日期短时间
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToDateShortTime(long ms) {
        if (ms < 0) return null;
        return dateShortTimeFormatter.format(new Date(ms));
    }

    /**
     * 转换毫秒为日期
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToDate(long ms) {
        if (ms < 0) return null;
        return dateFormatter.format(new Date(ms));
    }

    /**
     * 转换日期为毫秒
     *
     * @param dt 日期字符串
     * @return
     */
    public static long dateToMs(String dt) {
        try {
            return dateFormatter.parse(dt).getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 转换毫秒为短日期
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToShortDate(long ms) {
        if (ms < 0) return null;
        return shortDateFormatter.format(new Date(ms));
    }

    /**
     * 转换毫秒为短日期短时间
     *
     * @param ms 毫秒数
     * @return
     */
    public static String msToShortDateShortTime(long ms) {
        if (ms < 0) return null;
        return shortDateShortTimeFormatter.format(new Date(ms));
    }

    /**
     * 转换毫秒为天数
     *
     * @param ms 毫秒数
     * @return
     */
    public static long msToDay(long ms) {
        return TimeUnit.MILLISECONDS.toDays(ms);
    }

    /**
     * 转换毫秒为年
     *
     * @param ms 毫秒数
     * @return
     */
    public static long msToYear(long ms) {
        return DateUtil.year(new Date(ms));
    }

    /**
     * 转换年为 xx 后
     *
     * @param year 年
     * @return
     */
    public static String yearToAge(int year) {
        int i = year % 100 / 5 * 5;
        return "（" + (i < 10 ? "0" + i : i) + " 后）";
    }

    /**
     * 月日转换为星座
     *
     * @param month
     * @param day
     * @return
     */
    public static String getConstellation(int month, int day) {
        return DateUtil.getZodiac(month - 1, day);
//        return day < constellationDayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    /**
     * 获取号龄字符串
     *
     * @param ms
     * @return
     */
    public static String getAccAge(long ms) {
        if (ms < 0) return "";
        long day = msToDay(System.currentTimeMillis() - ms);
        return (day < 365 ? day + " 天" : day / 365 + " 年") + "（" + msToDate(ms) + " 注册）";
    }

    /**
     * 转换秒为 xx(m):xx(s) 格式
     *
     * @param timeSeconds
     * @return
     */
    public static String format(double timeSeconds) {
        StringBuilder res = new StringBuilder();
        int s = (int) timeSeconds;
        int minutes = s / 60, seconds = s % 60;
        if (minutes < 10) res.append("0").append(minutes).append(":");
        else res.append(minutes).append(":");
        if (seconds < 10) res.append("0").append(seconds);
        else res.append(seconds);
        return res.toString();
    }

    /**
     * 转换 xx:xx:xx / xx 时 xx 分 xx 秒 等格式为秒
     *
     * @param s
     * @return
     */
    public static double toSeconds(String s) {
        List<String> groups = RegexUtil.findAllGroup1("(\\d+)", s);
        Collections.reverse(groups);
        double res = 0, u = 1;
        for (String g : groups) {
            res += Integer.parseInt(g) * u;
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
        List<String> groups = RegexUtil.findAllGroup1("(\\d+)", s);
        if (groups.size() == 1) {
            if (s.contains("秒")) return Integer.parseInt(groups.get(0));
            else if (s.contains("分")) return Integer.parseInt(groups.get(0)) * 60;
            else return Integer.parseInt(groups.get(0)) * 3600;
        } else if (groups.size() == 2) {
            if (s.contains("时") && s.contains("分"))
                return Integer.parseInt(groups.get(0)) * 3600 + Integer.parseInt(groups.get(1)) * 60;
            else if (s.contains("时") && s.contains("秒"))
                return Integer.parseInt(groups.get(0)) * 3600 + Integer.parseInt(groups.get(1));
            else
                return Integer.parseInt(groups.get(0)) * 60 + Integer.parseInt(groups.get(1));
        } else
            return Integer.parseInt(groups.get(0)) * 3600 + Integer.parseInt(groups.get(1)) * 60 + Integer.parseInt(groups.get(2));
    }

    /**
     * 转换秒为 [xx(m):xx(s).xx(ms)] 格式
     *
     * @param timeSeconds
     * @return
     */
    public static String formatToLrcTime(Double timeSeconds) {
        if (timeSeconds == null) return null;
        StringBuilder res = new StringBuilder();
        int s = timeSeconds.intValue();
        String s1 = String.valueOf(timeSeconds);
        int minutes = s / 60, seconds = s % 60;
        String milliseconds = s1.substring(s1.lastIndexOf('.'));
        res.append("[");
        if (minutes < 10) res.append("0").append(minutes).append(":");
        else res.append(minutes).append(":");
        if (seconds < 10) res.append("0").append(seconds);
        else res.append(seconds);
        res.append(milliseconds.length() == 2 ? milliseconds + "0" : milliseconds);
        res.append("]");
        return res.toString();
    }
}

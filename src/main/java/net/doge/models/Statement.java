package net.doge.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtils;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    private double time = 0.0;                                //时间, 单位为s, 精确到0.01s
    private double endTime = 0.0;                             //结束时间, 单位为s, 精确到0.01s
    private String lyric = "";                                //歌词

    public Statement(double time, String lyric) {
        this.time = time;
        this.lyric = lyric;
    }

    /*
     * 获取时间
     */
    public double getTime() {
        return time;
    }

    public double getEndTime() {
        return endTime;
    }

    /*
     * 设置时间
     * time: 被设置成的时间
     */
    public void setTime(double time) {
        this.time = time;
    }

    /*
     * 设置时间
     * time: 被设置成的时间字符串
     */
    public void setTime(String time) {
        String str[] = time.split(":|\\.");
        // 00:00
        if (str.length == 2) {
            this.time = Integer.parseInt(str[0]) * 60 + Integer.parseInt(str[1]);
        }
        // 00:00.0000000
        else {
            this.time = Integer.parseInt(str[0]) * 60
                    + Integer.parseInt(str[1]) + Double.parseDouble("0." + str[2]);
        }
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public boolean hasEndTime() {
        return Double.valueOf(endTime).intValue() != 0;
    }

    /*
     * 获取歌词
     */
    public String getLyric() {
        return lyric;
    }

    /*
     * 设置歌词
     */
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    /**
     * 歌词是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(lyric.trim());
    }

    /*
     * 打印歌词
     */
    public void printLyric() {
        System.out.println(time + ": " + lyric);
    }

    @Override
    public String toString() {
        return lyric;
    }
}

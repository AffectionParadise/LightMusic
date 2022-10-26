package net.doge.constants;

/**
 * @Author yzx
 * @Description 频谱相关参数
 * @Date 2020/12/14
 */
public class SpectrumConstants {
    public static final String[] names = {"平地式", "悬空式", "折线式", "曲线式", "山峰式", "波浪式", "对称山峰式", "对称波浪式"};
    // 平地式
    public static final int GROUND = 0;
    // 悬空式
    public static final int ABOVE = 1;
    // 线段式
    public static final int LINE = 2;
    // 曲线式
    public static final int CURVE = 3;
    // 山峰式
    public static final int HILL = 4;
    // 波浪式
    public static final int WAVE = 5;
    // 对称山峰式
    public static final int SYM_HILL = 6;
    // 对称波浪式
    public static final int SYM_WAVE = 7;

    // 播放器更新频谱数量
    public static final int NUM_BANDS = 256;
    // 播放器更新频谱时间间隔(s)
    public static final double PLAYER_INTERVAL = 0.09;
    // UI 更新频谱 Timer 时间间隔(ms)
    public static final int TIMER_INTERVAL = 10;
    // 频谱条数量(由界面宽度决定)
    public static int BAR_NUM = 60;
    // 频谱每条宽度
    public static final int BAR_WIDTH = 7;
    // 频谱最大高度
    public static final int BAR_MAX_HEIGHT = 150;
    // 频谱条与条之间的间距
    public static final int BAR_GAP = 3;
    // 频谱阈值
    public static final int THRESHOLD = -80;
}

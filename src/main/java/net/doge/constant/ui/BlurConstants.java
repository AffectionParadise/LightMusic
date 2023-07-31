package net.doge.constant.ui;

/**
 * @Author Doge
 * @Description 模糊参数
 * @Date 2020/12/7
 */
public class BlurConstants {
    // 关闭
    public static final int OFF = 0;
    // 歌曲封面
    public static final int CV = 1;
    // 纯主色调
    public static final int MC = 2;
    // 线性渐变
    public static final int LG = 3;

    // 高斯模糊因子
    public static int gsFactorIndex = 3;
    public static final String[] gaussianFactorName = {"较小", "小", "中", "大", "较大", "极大"};
    public static final int[] gaussianFactor = {12, 10, 8, 6, 4, 2};

    // 暗角滤镜因子
    public static int darkerFactorIndex = 2;
    public static final String[] darkerFactorName = {"较小", "小", "中", "大", "较大", "极大"};
    public static final float[] darkerFactor = {0.55f, 0.6f, 0.65f, 0.7f, 0.75f, 0.8f};

    // 线性渐变色彩风格
    public static final int VIBRANT = 0;
    public static final int LIGHT_VIBRANT = 1;
    public static final int DARK_VIBRANT = 2;
    public static final int MUTED = 3;
    public static final int LIGHT_MUTED = 4;
    public static final int DARK_MUTED = 5;
    public static int gradientColorStyleIndex = VIBRANT;
    public static final String[] gradientColorStyleName = {"鲜艳", "明亮鲜艳", "暗淡鲜艳", "柔和", "明亮柔和", "暗淡柔和"};
}

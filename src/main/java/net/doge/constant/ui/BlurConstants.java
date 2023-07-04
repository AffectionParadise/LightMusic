package net.doge.constant.ui;

/**
 * @Author yzx
 * @Description 模糊类型
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
    public static int gsFactorIndex;
    public static final String[] gaussianFactorName = {"较小", "小", "中", "大", "较大", "极大"};
    public static final int[] gaussianFactor = {32, 16, 8, 4, 2, 1};

    // 暗角滤镜因子
    public static int darkerFactorIndex;
    public static final String[] darkerFactorName = {"较小", "小", "中", "大", "较大"};
    public static final float[] darkerFactor = {0.55f, 0.6f, 0.65f, 0.7f, 0.75f};
}

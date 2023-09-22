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
    // 迷幻纹理
    public static final int FBM = 4;

    // 高斯模糊因子
    public static int gsFactorIndex = 3;
    public static final String[] gaussianFactorName = {"较小 (8.3%)", "小 (10%)", "中 (12.5%)", "大 (16.7%)", "较大 (25%)", "极大 (50%)"};
    public static final int[] gaussianFactor = {12, 10, 8, 6, 4, 2};

    // 暗角滤镜因子
    public static int darkerFactorIndex = 1;
    public static final String[] darkerFactorName = {"较小 (60%)", "小 (65%)", "中 (70%)", "大 (75%)", "较大 (80%)", "极大 (85%)"};
    public static final float[] darkerFactor = {0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f};
}

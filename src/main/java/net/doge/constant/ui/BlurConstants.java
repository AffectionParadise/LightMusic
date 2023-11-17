package net.doge.constant.ui;

import net.doge.constant.lang.I18n;

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
    public static final String[] GAUSSIAN_FACTOR_NAME = {
            I18n.getText("smaller") + " (5%)",
            I18n.getText("small") + " (10%)",
            I18n.getText("medium") + " (20%)",
            I18n.getText("large") + " (30%)",
            I18n.getText("larger") + " (40%)",
            I18n.getText("huge") + " (50%)"
    };
    public static final float[] GAUSSIAN_FACTOR = {0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f};

    // 暗角滤镜因子
    public static int darkerFactorIndex = 1;
    public static final String[] DARKER_FACTOR_NAME = {
            I18n.getText("smaller") + " (60%)",
            I18n.getText("small") + " (65%)",
            I18n.getText("medium") + " (70%)",
            I18n.getText("large") + " (75%)",
            I18n.getText("larger") + " (80%)",
            I18n.getText("huge") + " (85%)"
    };
    public static final float[] DARKER_FACTOR = {0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f};
}

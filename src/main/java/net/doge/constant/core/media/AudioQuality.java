package net.doge.constant.core.media;

import net.doge.constant.core.lang.I18n;

/**
 * @Author Doge
 * @Description 音质
 * @Date 2020/12/15
 */
public class AudioQuality {
    // 普通
    public static final int STANDARD = 0;
    // 高
    public static final int HIGH = 1;
    // 超高
    public static final int SUPER = 2;
    // 无损
    public static final int LOSSLESS = 3;
    // Hi-Res
    public static final int HI_RES = 4;
    // 至臻全景声
    public static final int ATMOSPHERE = 5;
    // 至臻母带
    public static final int MASTER = 6;

    public static final String[] KEYS = {"standard", "high", "super", "lossless", "hires", "atmosphere", "master"};

    // 当前音质
    public static int quality = SUPER;
    public static final String[] NAMES = {
            I18n.getText("standard"),
            I18n.getText("high"),
            I18n.getText("super"),
            I18n.getText("lossless"),
            I18n.getText("hires"),
            I18n.getText("atmosphere"),
            I18n.getText("master"),
    };

    // Hi-Res
    public static final int HR = 0;
    // 无损
    public static final int SQ = 1;
    // 高
    public static final int HQ = 2;
    // 中
    public static final int MQ = 3;
    // 低
    public static final int LQ = 4;
    // 未知
    public static final int UNKNOWN = -1;

    public static final String[] QT_NAMES = {
            I18n.getText("hr"),
            I18n.getText("sq"),
            I18n.getText("hq"),
            I18n.getText("mq"),
            I18n.getText("lq")
    };
}

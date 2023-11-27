package net.doge.constant.system;

import net.doge.constant.lang.I18n;

/**
 * @Author Doge
 * @Description 音质
 * @Date 2020/12/15
 */
public class AudioQuality {
    // Hi-Res
    public static final int HI_RES = 0;
    // 无损
    public static final int LOSSLESS = 1;
    // 超高
    public static final int SUPER = 2;
    // 高
    public static final int HIGH = 3;
    // 普通
    public static final int NORMAL = 4;

    // 当前音质
    public static int quality = SUPER;
    public static final String[] NAMES = {
            I18n.getText("hiRes"),
            I18n.getText("lossless"),
            I18n.getText("super"),
            I18n.getText("high"),
            I18n.getText("normal")
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

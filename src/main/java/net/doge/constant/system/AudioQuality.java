package net.doge.constant.system;

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
}

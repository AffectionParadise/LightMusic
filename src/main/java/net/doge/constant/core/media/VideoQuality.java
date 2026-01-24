package net.doge.constant.core.media;

import net.doge.constant.core.lang.I18n;

/**
 * @Author Doge
 * @Description 画质
 * @Date 2020/12/15
 */
public class VideoQuality {
    // 2K
    public static final int UHD = 0;
    // 全高清
    public static final int FHD = 1;
    // 高清
    public static final int HD = 2;
    // 标清
    public static final int SD = 3;
    // 流畅
    public static final int FLUENT = 4;

    // 当前画质
    public static int quality = FHD;
    public static final String[] NAMES = {
            I18n.getText("uhd"),
            I18n.getText("fhd"),
            I18n.getText("hd"),
            I18n.getText("sd"),
            I18n.getText("fluent")
    };
}

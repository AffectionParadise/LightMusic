package net.doge.constant.core.media;

import net.doge.constant.core.lang.I18n;

/**
 * @author Doge
 * @description 画质
 * @date 2020/12/15
 */
public class VideoQuality {
    // 流畅
    public static final int FLUENT = 0;
    // 标清
    public static final int SD = 1;
    // 高清
    public static final int HD = 2;
    // 全高清
    public static final int FHD = 3;
    // 2K
    public static final int UHD = 4;

    // 当前画质
    public static int quality = FHD;
    public static final String[] NAMES = {
            I18n.getText("fluent"),
            I18n.getText("sd"),
            I18n.getText("hd"),
            I18n.getText("fhd"),
            I18n.getText("uhd")
    };
}

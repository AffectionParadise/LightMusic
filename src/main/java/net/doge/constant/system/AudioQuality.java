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
    public static final String[] NAMES = {"Hi-Res (FLAC)", "无损 (FLAC)", "超高 (MP3)", "较高 (MP3)", "普通 (MP3)"};
}

package net.doge.constant.system;

/**
 * @Author Doge
 * @Description 文件格式
 * @Date 2020/12/7
 */
public class Format {
    // 音视频文件格式
    public static final String AIF = "aif";
    public static final String AIFF = "aiff";
    public static final String M3U8 = "m3u8";
    public static final String M4A = "m4a";
    public static final String M4V = "m4v";
    public static final String MP3 = "mp3";
    public static final String MP4 = "mp4";
    public static final String WAV = "wav";
    // 不支持的格式
    public static final String FLAC = "flac";
    public static final String FLV = "flv";

    public static final String[] AUDIO_TYPE_SUPPORTED = {AIF, AIFF, M3U8, M4A, M4V, MP3, MP4, WAV};

    // 图片文件格式
    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String PNG = "png";
    public static final String GIF = "gif";
    public static final String BMP = "bmp";
    public static final String WBMP = "wbmp";
    public static final String JFIF = "jfif";
    public static final String WEBP = "webp";

    public static final String[] READ_IMAGE_TYPE_SUPPORTED = {JPG, JPEG, PNG, GIF, BMP, WBMP, JFIF};
    public static final String[] WRITE_IMAGE_TYPE_SUPPORTED = {JPG, JPEG, PNG, GIF, BMP};
}

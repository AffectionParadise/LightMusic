package net.doge.model.entity;

import lombok.Data;
import net.doge.constant.media.Format;

import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/12
 */
@Data
public class MediaInfo {
    // 标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 流派
    private String genre;
    // 注释
    private String comment;
    // 厂牌
    private String recordLabel;
    // 歌词
    private String lyrics;
    // 作词
    private String lyricist;
    // 封面图片
    private BufferedImage albumImage;
    // 格式
    private String format;

    // 判断是不是 mp3 文件
    public boolean isMp3() {
        return Format.MP3.equalsIgnoreCase(format);
    }

    public boolean isM4a() {
        return Format.M4A.equalsIgnoreCase(format);
    }

    public boolean isFlac() {
        return Format.FLAC.equalsIgnoreCase(format);
    }
}

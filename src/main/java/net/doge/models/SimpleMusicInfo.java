package net.doge.models;

import lombok.Data;
import net.doge.constants.Format;

import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class SimpleMusicInfo {
    // 文件
    private AudioFile file;
    // 音频格式
    private String format;
    // 时长(秒)
    private double duration;
    // 歌曲名称
    private String name;
    // 艺术家
    private String artist;
    // 专辑名称
    private String albumName;
    // 专辑图片
    private BufferedImage albumImage;

    // 封面图加载后的回调函数
    private Runnable invokeLater;

    public void setAlbumImage(BufferedImage albumImage) {
        this.albumImage = albumImage;
        callback();
    }

    private void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    public boolean hasFile() {
        return file != null;
    }

    public boolean hasDuration() {
        return duration != 0;
    }

    /**
     * 判断是否有封面图片
     *
     * @return
     */
    public boolean hasAlbumImage() {
        return albumImage != null;
    }

    /**
     * 判断是不是 mp3 文件
     *
     * @return
     */
    public boolean isMp3() {
        return Format.MP3.equals(format);
    }
}

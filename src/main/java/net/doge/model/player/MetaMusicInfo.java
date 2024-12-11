package net.doge.model.player;

import lombok.Data;
import net.doge.constant.system.Format;

import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class MetaMusicInfo {
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
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    public boolean hasDuration() {
        return duration != 0;
    }

    // 判断是不是 mp3 文件
    public boolean isMp3() {
        return Format.MP3.equalsIgnoreCase(format);
    }

    public boolean isFlac() {
        return Format.FLAC.equalsIgnoreCase(format);
    }
}

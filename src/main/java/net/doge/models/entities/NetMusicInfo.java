package net.doge.models.entities;

import lombok.Data;
import net.doge.constants.Format;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.utils.FileUtil;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 在线音乐
 * @Date 2020/12/7
 */
@Data
public class NetMusicInfo {
    // 来源
    private int source = NetMusicSource.NET_CLOUD;
    // 歌曲 id
    private String id;
    // 酷狗的歌曲 hash，这个参数与上面的 id 不同
    private String hash;
    // 网易云的电台节目 id，这个参数与上面的 id 不同
    private String programId;
    // MV id
    private String mvId;
    // 地址
    private String url;
    // 音频格式
    private String format = Format.MP3;
    // 时长(秒)
    private double duration;
    // 歌曲名称
    private String name;
    // 艺术家
    private String artist;
    // 艺术家 id
    private String artistId;
    // 专辑名称
    private String albumName;
    // 专辑 id
    private String albumId;
    // 歌词
    private String lrc;
    // 歌词匹配
    private String lrcMatch;
    // 歌词翻译
    private String trans;
    // 罗马音歌词
    private String roma;

    // 封面图加载后的回调函数
    private Runnable invokeLater;

    private final static String separator = " - ";

    public BufferedImage getAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return ImageUtil.read(imgFile);
    }

    public void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    /**
     * 判断歌曲信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return hasUrl() && hasLrc() && hasAlbumImage();
    }

    public boolean hasProgramId() {
        return StringUtil.isNotEmpty(programId);
    }

    /**
     * 判断是不是电台节目(网易云、猫耳和喜马拉雅)
     *
     * @return
     */
    public boolean isProgram() {
        return source == NetMusicSource.XM || source == NetMusicSource.HF || source == NetMusicSource.GG
                || source == NetMusicSource.ME || source == NetMusicSource.BI || hasProgramId();
    }

    /**
     * 判断有没有 hash(酷狗)
     *
     * @return
     */
    public boolean hasHash() {
        return StringUtil.isNotEmpty(hash);
    }

    public boolean hasArtist() {
        return StringUtil.isNotEmpty(artist);
    }

    public boolean hasArtistId() {
        return StringUtil.isNotEmpty(artistId);
    }

    public boolean hasAlbumName() {
        return StringUtil.isNotEmpty(albumName);
    }

    public boolean hasAlbumId() {
        return StringUtil.isNotEmpty(albumId);
    }

    public boolean hasAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return imgFile.exists() && imgFile.length() != 0;
    }

    public boolean hasMv() {
        return StringUtil.isNotEmpty(mvId) && !"0".equals(mvId);
    }

    public boolean hasUrl() {
        return StringUtil.isNotEmpty(url);
    }

    // 判断歌词 + 翻译 + 罗马音是否完整
    public boolean isLrcIntegrated() {
        return hasLrc() && hasTrans() && hasRoma();
    }

    public boolean hasLrc() {
        return lrc != null;
    }

    public boolean hasTrans() {
        return StringUtil.isNotEmpty(trans);
    }

    public boolean hasRoma() {
        return StringUtil.isNotEmpty(roma);
    }

    public boolean hasLrcMatch() {
        return StringUtil.isNotEmpty(lrcMatch);
    }

    public boolean hasDuration() {
        return duration != 0 && Double.isFinite(duration);
    }

    public boolean isMp3() {
        return format.equals(Format.MP3);
    }

    public boolean isFlac() {
        return format.equals(Format.FLAC);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetMusicInfo) {
            NetMusicInfo netMusicInfo = (NetMusicInfo) o;
            return hashCode() == netMusicInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toAlbumImageFileName() {
        return FileUtil.filterFileName(toSimpleString() + separator + id + "." + Format.JPG);
    }

    public String toFileName() {
        return FileUtil.filterFileName(toSimpleString() + separator + id + "." + format);
    }

    public String toSimpleFileName() {
        return FileUtil.filterFileName(toSimpleString() + "." + format);
    }

    public String toLrcFileName() {
        return FileUtil.filterFileName(toSimpleString() + separator + id + ".lrc");
    }

    public String toSimpleLrcFileName() {
        return FileUtil.filterFileName(toSimpleString() + ".lrc");
    }

    public String toLrcTransFileName() {
        return FileUtil.filterFileName(toSimpleString() + separator + id + separator + "trans.lrc");
    }

    public String toSimpleLrcTransFileName() {
        return FileUtil.filterFileName(toSimpleString() + separator + "trans.lrc");
    }

    public String toString() {
        return NetMusicSource.names[source] + separator + toSimpleString();
    }

    public String toSimpleString() {
        return StringUtil.shorten(name
                + (StringUtil.isNotEmpty(artist) ? separator + artist : ""), 230);
    }

    public String toKeywords() {
        return name
                + (StringUtil.isNotEmpty(artist) ? " " + artist.replace("、", " ") : "");
    }
}

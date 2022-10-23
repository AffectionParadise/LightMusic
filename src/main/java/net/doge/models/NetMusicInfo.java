package net.doge.models;

import net.doge.constants.Format;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.utils.FileUtils;
import lombok.Data;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
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
    // 网易云 / 酷狗 / QQ 音乐 / 酷我 / 咪咕 / 喜马拉雅 id
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
    private Double duration;
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
    // 专辑图片
//    private BufferedImage albumImage;
    // 专辑图片 url(喜马拉雅需要)
    private String albumImgUrl;
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

//    public void setAlbumImage(BufferedImage albumImage) {
//        this.albumImage = albumImage;
//        callback();
//    }

    public BufferedImage getAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return ImageUtils.read(imgFile);
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
        return StringUtils.isNotEmpty(programId);
    }

    /**
     * 判断是不是电台节目(网易云、喜马拉雅)
     *
     * @return
     */
    public boolean isProgram() {
        return source == NetMusicSource.XM || source == NetMusicSource.ME || hasProgramId();
    }

    /**
     * 判断有没有 hash(酷狗)
     *
     * @return
     */
    public boolean hasHash() {
        return StringUtils.isNotEmpty(hash);
    }

    public boolean hasArtist() {
        return StringUtils.isNotEmpty(artist);
    }

    public boolean hasArtistId() {
        return StringUtils.isNotEmpty(artistId);
    }

    public boolean hasAlbumName() {
        return StringUtils.isNotEmpty(albumName);
    }

    public boolean hasAlbumId() {
        return StringUtils.isNotEmpty(albumId);
    }

    public boolean hasAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return imgFile.exists() && imgFile.length() != 0;
    }

    public boolean hasMv() {
        return StringUtils.isNotEmpty(mvId) && !"0".equals(mvId);
    }

    public boolean hasUrl() {
        return StringUtils.isNotEmpty(url);
    }

    // 判断歌词 + 翻译 + 罗马音是否完整
    public boolean isLrcIntegrated() {
        return hasLrc() && hasTrans() && hasRoma();
    }

    public boolean hasLrc() {
        return lrc != null;
    }

    public boolean hasTrans() {
        return StringUtils.isNotEmpty(trans);
    }

    public boolean hasRoma() {
        return StringUtils.isNotEmpty(roma);
    }

    public boolean hasLrcMatch() {
        return StringUtils.isNotEmpty(lrcMatch);
    }

    public boolean hasDuration() {
        return duration != null && !Double.isNaN(duration) && !Double.isInfinite(duration) && duration.intValue() != 0;
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
        return FileUtils.filterFileName(toSimpleString() + separator + id + "." + Format.JPG);
    }

    public String toFileName() {
        return FileUtils.filterFileName(toSimpleString() + separator + id + "." + format);
    }

    public String toSimpleFileName() {
        return FileUtils.filterFileName(toSimpleString() + "." + format);
    }

    public String toLrcFileName() {
        return FileUtils.filterFileName(toSimpleString() + separator + id + ".lrc");
    }

    public String toSimpleLrcFileName() {
        return FileUtils.filterFileName(toSimpleString() + ".lrc");
    }

    public String toLrcTransFileName() {
        return FileUtils.filterFileName(toSimpleString() + separator + id + separator + "trans.lrc");
    }

    public String toSimpleLrcTransFileName() {
        return FileUtils.filterFileName(toSimpleString() + separator + "trans.lrc");
    }

    public String toString() {
        return NetMusicSource.names[source] + separator + toSimpleString();
//                + (StringUtils.isNotEmpty(albumName) ? separator + albumName : "")
//                + (duration != null ? separator + TimeUtils.format(duration) : "");
    }

    public String toSimpleString() {
        return name
                + (StringUtils.isNotEmpty(artist) ? separator + artist.replace("/", "、") : "");
    }
}

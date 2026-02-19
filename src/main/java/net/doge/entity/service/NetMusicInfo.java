package net.doge.entity.service;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.os.Format;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.base.Downloadable;
import net.doge.entity.service.base.MusicResource;
import net.doge.entity.service.base.NetResource;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * @author Doge
 * @description 在线音乐
 * @date 2020/12/7
 */
@Data
public class NetMusicInfo implements MusicResource, NetResource, Downloadable {
    // 来源
    private int source = NetMusicSource.NC;
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
    // 音质类型
    private int qualityType = AudioQuality.UNKNOWN;
    // 当前使用的音质
    private int quality;
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
    private String lyric;
    // 歌词匹配
    private String lyricMatch;
    // 歌词翻译
    private String trans;
    // 罗马音歌词
    private String roma;

    // 封面图加载后的回调函数
    private Runnable invokeLater;

    private static final String SEPARATOR = " - ";

    public BufferedImage getAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return ImageUtil.read(imgFile);
    }

    public void callback() {
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    // 判断歌曲信息是否完整
    public boolean isIntegrated() {
        return hasUrl() && hasLyric() && hasAlbumImage();
    }

    // 判断当前音质是否为设置的音质
    public boolean isQualityMatch() {
        return quality == AudioQuality.quality;
    }

    public boolean hasProgramId() {
        return StringUtil.notEmpty(programId);
    }

    // 判断是不是电台节目
    public boolean isProgram() {
        return source == NetMusicSource.XM || source == NetMusicSource.HF || source == NetMusicSource.GG
                || source == NetMusicSource.FS || source == NetMusicSource.ME || source == NetMusicSource.BI || hasProgramId();
    }

    // 判断有没有 hash(酷狗)
    public boolean hasHash() {
        return StringUtil.notEmpty(hash);
    }

    public boolean hasName() {
        return StringUtil.notEmpty(name);
    }

    public boolean hasArtist() {
        return StringUtil.notEmpty(artist);
    }

    public boolean hasArtistId() {
        return StringUtil.notEmpty(artistId);
    }

    public boolean hasAlbumName() {
        return StringUtil.notEmpty(albumName);
    }

    public boolean hasAlbumId() {
        return StringUtil.notEmpty(albumId);
    }

    public boolean hasAlbumImage() {
        File imgFile = new File(SimplePath.IMG_CACHE_PATH + toAlbumImageFileName());
        return imgFile.exists() && imgFile.length() != 0;
    }

    public boolean hasMv() {
        return StringUtil.notEmpty(mvId) && !"0".equals(mvId);
    }

    public boolean hasQualityType() {
        return qualityType != AudioQuality.UNKNOWN;
    }

    public boolean hasUrl() {
        return StringUtil.notEmpty(url);
    }

    // 判断歌词是否完整
    public boolean isLyricIntegrated() {
        return hasLyric();
    }

    public boolean hasLyric() {
        return StringUtil.notEmpty(lyric);
    }

    public boolean hasTrans() {
        return StringUtil.notEmpty(trans);
    }

    public boolean hasRoma() {
        return StringUtil.notEmpty(roma);
    }

    public boolean hasLyricMatch() {
        return StringUtil.notEmpty(lyricMatch);
    }

    public boolean hasDuration() {
        return duration != 0 && Double.isFinite(duration);
    }

    public boolean isMp3() {
        return Format.MP3.equalsIgnoreCase(format);
    }

    public boolean isM4a() {
        return Format.M4A.equalsIgnoreCase(format);
    }

    public boolean isFlac() {
        return Format.FLAC.equalsIgnoreCase(format);
    }

    // 歌词文件内容
    public String getLyricFileText() {
        return hasLyric() ? lyric.replaceAll(LyricPattern.PAIR, "") : lyric;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) o;
            return hashCode() == musicInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toAlbumImageFileName() {
        return FileUtil.filterFileName(toSimpleString() + SEPARATOR + id + "." + Format.JPG);
    }

    public String toFileName() {
        return FileUtil.filterFileName(toSimpleString() + SEPARATOR + id + "." + format);
    }

    public String toSimpleFileName() {
        return FileUtil.filterFileName(toSimpleString() + "." + format);
    }

    public String toLyricFileName() {
        return FileUtil.filterFileName(toSimpleString() + SEPARATOR + id + "." + Format.LRC);
    }

    public String toSimpleLyricFileName() {
        return FileUtil.filterFileName(toSimpleString() + "." + Format.LRC);
    }

    public String toString() {
        return NetMusicSource.NAMES[source] + SEPARATOR + toSimpleString();
    }

    public String toSimpleString() {
        return StringUtil.shorten(name + (StringUtil.notEmpty(artist) ? SEPARATOR + artist : ""), 230);
    }

    public String toKeywords() {
        return name + (StringUtil.notEmpty(artist) ? " " + artist.replace("、", " ") : "");
    }
}

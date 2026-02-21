package net.doge.entity.service;

import lombok.Data;
import net.doge.constant.core.os.Format;
import net.doge.entity.service.base.MusicResource;
import net.doge.util.core.StringUtil;
import net.doge.util.core.io.FileUtil;

import java.io.File;

@Data
public class AudioFile extends File implements MusicResource {
    // 格式
    private String format;
    // 曲名
    private String songName;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 时长
    private double duration;

    public AudioFile(String path) {
        super(path);
    }

    public AudioFile(File file) {
        super(file.getPath());
    }

    /**
     * 判断歌曲信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return hasDuration();
    }

    public String getPrefix() {
        return FileUtil.getPrefix(this);
    }

    @Override
    public String toString() {
        return hasSongName() ? songName + (hasArtist() ? " - " + artist : "") : getPrefix();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AudioFile && getAbsolutePath().equals(((AudioFile) obj).getAbsolutePath());
    }

    public boolean hasSongName() {
        return StringUtil.notEmpty(songName);
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

    public boolean hasArtist() {
        return StringUtil.notEmpty(artist);
    }

    public boolean hasAlbum() {
        return StringUtil.notEmpty(album);
    }

    public boolean hasDuration() {
        return duration != 0;
    }

    public String toKeywords() {
        return songName
                + (StringUtil.notEmpty(artist) ? " " + artist.replace("、", " ") : "");
    }

    public File toLyricFile() {
        return new File(toLyricPath());
    }

    public String toLyricPath() {
        return FileUtil.getPathWithoutSuffix(this) + "." + Format.LRC;
    }

    public File toLmlFile() {
        return new File(toLmlPath());
    }

    public String toLmlPath() {
        return FileUtil.getPathWithoutSuffix(this) + "." + Format.LML;
    }
}

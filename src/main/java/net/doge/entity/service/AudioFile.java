package net.doge.entity.service;

import lombok.Getter;
import lombok.Setter;
import net.doge.constant.core.media.Format;
import net.doge.entity.service.base.MusicResource;
import net.doge.util.common.StringUtil;
import net.doge.util.os.FileUtil;

import java.io.File;

public class AudioFile extends File implements MusicResource {
    // 格式
    @Getter
    @Setter
    private String format;
    // 曲名
    @Getter
    @Setter
    private String songName;
    // 艺术家
    @Getter
    @Setter
    private String artist;
    // 专辑
    @Getter
    @Setter
    private String album;
    // 时长
    @Getter
    @Setter
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

    public File toLrcFile() {
        return new File(FileUtil.getPathWithoutSuffix(this) + ".lrc");
    }
}

package net.doge.models;

import net.doge.constants.Format;
import net.doge.utils.FileUtils;
import net.doge.utils.StringUtils;

import java.io.File;

public class AudioFile extends File {
    // 格式
    private String format;
    // 曲名
    private String songName;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 时长
    private Double duration;
//    // 流派
//    private String genre;
//    // 注释
//    private String comment;
//    // 版权
//    private String copyright;

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
        return duration != null;
    }

    public String getNameWithoutSuffix() {
        return FileUtils.getNameWithoutSuffix(this);
    }

    @Override
    public String toString() {
        return hasSongName() ? songName + (hasArtist() ? " - " + artist : "") : getNameWithoutSuffix();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AudioFile && getAbsolutePath().equals(((AudioFile) obj).getAbsolutePath());
    }

    public boolean hasSongName() {
        return StringUtils.isNotEmpty(songName);
    }

    public boolean isMp3() {
        return Format.MP3.equals(format);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean hasArtist() {
        return StringUtils.isNotEmpty(artist);
    }

    public boolean hasAlbum() {
        return StringUtils.isNotEmpty(album);
    }

    public boolean hasDuration() {
        return duration != null && !Double.isNaN(duration) && !Double.isInfinite(duration) && duration.intValue() != 0;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

//    public String getGenre() {
//        return genre;
//    }
//
//    public void setGenre(String genre) {
//        this.genre = genre;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    public String getCopyright() {
//        return copyright;
//    }
//
//    public void setCopyright(String copyright) {
//        this.copyright = copyright;
//    }
}

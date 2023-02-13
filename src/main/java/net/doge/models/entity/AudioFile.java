package net.doge.models.entity;

import net.doge.constants.Format;
import net.doge.utils.FileUtil;
import net.doge.utils.StringUtil;

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
    private double duration;
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
        return hasDuration();
    }

    public String getNameWithoutSuffix() {
        return FileUtil.getNameWithoutSuffix(this);
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
        return StringUtil.isNotEmpty(songName);
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
        return StringUtil.isNotEmpty(artist);
    }

    public boolean hasAlbum() {
        return StringUtil.isNotEmpty(album);
    }

    public boolean hasDuration() {
        return duration != 0;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
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

    public String toKeywords() {
        return songName
                + (StringUtil.isNotEmpty(artist) ? " " + artist.replace("、", " ") : "");
    }
}

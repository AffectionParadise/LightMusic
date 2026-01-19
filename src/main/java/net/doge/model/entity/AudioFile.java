package net.doge.model.entity;

import net.doge.constant.media.Format;
import net.doge.model.entity.base.MusicResource;
import net.doge.util.common.StringUtil;
import net.doge.util.os.FileUtil;

import java.io.File;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
                + (StringUtil.notEmpty(artist) ? " " + artist.replace("、", " ") : "");
    }
}

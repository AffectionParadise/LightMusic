package net.doge.util.media;

import net.doge.constant.core.os.SimplePath;
import net.doge.entity.service.AudioFile;
import net.doge.entity.service.MediaInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.log.LogUtil;
import net.doge.util.core.os.TerminalUtil;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Doge
 * @description 音乐工具类
 * @date 2020/12/11
 */
public class MediaUtil {

    static {
        // 关闭 jaudiotagger 日志
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
    }

    /**
     * 获取音频文件时长
     *
     * @param file
     */
    public static double getDuration(AudioFile file) {
        if (file == null || !file.exists()) return 0;
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
            AudioHeader audioHeader = af.getAudioHeader();
            return getDuration(audioHeader);
        } catch (Exception e) {
            LogUtil.error(e);
            return 0;
        }
    }

    // 从头信息获取音频文件时长
    private static double getDuration(AudioHeader audioHeader) {
        return audioHeader.getPreciseTrackLength();
    }

    /**
     * 为音频文件写入信息(包含曲名、艺术家、专辑、封面图片)
     *
     * @param sourcePath
     * @param musicInfo
     */
    public static void writeAudioFileInfo(String sourcePath, NetMusicInfo musicInfo) {
        if (!musicInfo.hasAlbumImage()) musicInfo.setInvokeLater(() -> doWrite(sourcePath, musicInfo));
        else doWrite(sourcePath, musicInfo);
    }

    private static void doWrite(String sourcePath, NetMusicInfo musicInfo) {
        String name = musicInfo.getName();
        String artist = musicInfo.getArtist();
        String albumName = musicInfo.getAlbumName();
        BufferedImage albumImg = musicInfo.getAlbumImage();

        File source = new File(sourcePath);
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();

            setAlbumImageToTag(albumImg, tag);
            tag.setField(FieldKey.TITLE, name);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.ALBUM, albumName);

            af.commit();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 为音频文件写入信息(通过 MediaInfo)
     *
     * @param audioFile
     * @param mediaInfo
     */
    public static void writeAudioFileInfo(AudioFile audioFile, MediaInfo mediaInfo) {
        String title = mediaInfo.getTitle();
        String artist = mediaInfo.getArtist();
        String albumName = mediaInfo.getAlbum();
        BufferedImage albumImg = mediaInfo.getAlbumImage();
        String genre = mediaInfo.getGenre();
        String lyrics = mediaInfo.getLyrics();
        String lyricist = mediaInfo.getLyricist();
        String year = mediaInfo.getYear();
        String rating = mediaInfo.getRating();
        String bpm = mediaInfo.getBpm();
        String key = mediaInfo.getKey();
        String comment = mediaInfo.getComment();
        String recordLabel = mediaInfo.getRecordLabel();
        String mood = mediaInfo.getMood();
        String occasion = mediaInfo.getOccasion();
        String language = mediaInfo.getLanguage();
        String country = mediaInfo.getCountry();
        String version = mediaInfo.getVersion();
        String copyright = mediaInfo.getCopyright();

        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(audioFile);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();

            setAlbumImageToTag(albumImg, tag);
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.ALBUM, albumName);
            tag.setField(FieldKey.GENRE, genre);
            tag.setField(FieldKey.LYRICS, lyrics);
            tag.setField(FieldKey.LYRICIST, lyricist);
            tag.setField(FieldKey.YEAR, year);
            tag.setField(FieldKey.RATING, rating);
            tag.setField(FieldKey.BPM, bpm);
            tag.setField(FieldKey.KEY, key);
            tag.setField(FieldKey.COMMENT, comment);
            tag.setField(FieldKey.RECORD_LABEL, recordLabel);
            tag.setField(FieldKey.MOOD, mood);
            tag.setField(FieldKey.OCCASION, occasion);
            tag.setField(FieldKey.LANGUAGE, language);
            tag.setField(FieldKey.COUNTRY, country);
            tag.setField(FieldKey.VERSION, version);
            tag.setField(FieldKey.COPYRIGHT, copyright);

            af.commit();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    // 为 Tag 写入封面图片
    private static void setAlbumImageToTag(BufferedImage albumImg, Tag tag) throws FieldDataInvalidException {
        // 设置封面之前必须先清除原有的字段！
        tag.deleteArtworkField();
        if (albumImg == null) return;
        MetadataBlockDataPicture picture = new MetadataBlockDataPicture(ImageUtil.toBytes(albumImg), PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "",
                albumImg.getWidth(), albumImg.getHeight(), 24, 0);
        StandardArtwork artwork = StandardArtwork.createArtworkFromMetadataBlockDataPicture(picture);
        tag.setField(artwork);
    }

    /**
     * 补全 AudioFile 信息(格式、曲名、艺术家、专辑、时长)
     *
     * @param file
     * @return
     */
    public static void fillAudioFileInfo(AudioFile file) {
        if (file == null || !file.exists()) return;
        try {
            file.setFormat(FileUtil.getSuffix(file).toLowerCase());

            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();

            String title = StringUtil.fixEncoding(tag.getFirst(FieldKey.TITLE));
            String artist = StringUtil.fixEncoding(tag.getFirst(FieldKey.ARTIST));
            String album = StringUtil.fixEncoding(tag.getFirst(FieldKey.ALBUM));

            file.setSongName(title);
            file.setArtist(artist);
            file.setAlbum(album);
            // 从头信息获取时长
            file.setDuration(getDuration(af.getAudioHeader()));
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 从文件获取封面图片，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static BufferedImage getAlbumImage(AudioFile source) {
        try {
            BufferedImage albumImage = null;
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) albumImage = (BufferedImage) artwork.getImage();
            return albumImage;
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 获取额外的音频元信息
     *
     * @param source
     * @return
     */
    public static MediaInfo getExtraMediaInfo(AudioFile source) {
        MediaInfo mediaInfo = new MediaInfo();
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();

            String genre = StringUtil.fixEncoding(tag.getFirst(FieldKey.GENRE));
            String lyrics = StringUtil.fixEncoding(tag.getFirst(FieldKey.LYRICS));
            String lyricist = StringUtil.fixEncoding(tag.getFirst(FieldKey.LYRICIST));
            String year = StringUtil.fixEncoding(tag.getFirst(FieldKey.YEAR));
            String rating = StringUtil.fixEncoding(tag.getFirst(FieldKey.RATING));
            String bpm = StringUtil.fixEncoding(tag.getFirst(FieldKey.BPM));
            String key = StringUtil.fixEncoding(tag.getFirst(FieldKey.KEY));
            String comment = StringUtil.fixEncoding(tag.getFirst(FieldKey.COMMENT));
            String recordLabel = StringUtil.fixEncoding(tag.getFirst(FieldKey.RECORD_LABEL));
            String mood = StringUtil.fixEncoding(tag.getFirst(FieldKey.MOOD));
            String occasion = StringUtil.fixEncoding(tag.getFirst(FieldKey.OCCASION));
            String language = StringUtil.fixEncoding(tag.getFirst(FieldKey.LANGUAGE));
            String country = StringUtil.fixEncoding(tag.getFirst(FieldKey.COUNTRY));
            String version = StringUtil.fixEncoding(tag.getFirst(FieldKey.VERSION));
            String copyright = StringUtil.fixEncoding(tag.getFirst(FieldKey.COPYRIGHT));

            mediaInfo.setGenre(genre);
            mediaInfo.setLyrics(lyrics);
            mediaInfo.setLyricist(lyricist);
            mediaInfo.setYear(year);
            mediaInfo.setRating(rating);
            mediaInfo.setBpm(bpm);
            mediaInfo.setKey(key);
            mediaInfo.setComment(comment);
            mediaInfo.setRecordLabel(recordLabel);
            mediaInfo.setMood(mood);
            mediaInfo.setOccasion(occasion);
            mediaInfo.setLanguage(language);
            mediaInfo.setCountry(country);
            mediaInfo.setVersion(version);
            mediaInfo.setCopyright(copyright);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return mediaInfo;
    }

    /**
     * 读取音频文件内嵌歌词
     *
     * @param source
     * @return
     */
    public static String getEmbeddedLyric(AudioFile source) {
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
            Tag tag = af.getTagAndConvertOrCreateAndSetDefault();

            return StringUtil.fixEncoding(tag.getFirst(FieldKey.LYRICS));
        } catch (Exception e) {
            LogUtil.error(e);
            return "";
        }
    }

    /**
     * 将音频/视频文件转成指定格式
     *
     * @param source
     * @return
     */
    public static void convert(File source, File dest) {
        TerminalUtil.exec(SimplePath.PLUGIN_PATH + String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
    }
}

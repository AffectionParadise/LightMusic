package net.doge.util.media;

import net.doge.constant.system.Format;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.AudioFile;
import net.doge.model.entity.MediaInfo;
import net.doge.model.entity.NetMusicInfo;
import net.doge.util.common.LogUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.system.FileUtil;
import net.doge.util.system.TerminalUtil;
import net.doge.util.ui.ImageUtil;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Doge
 * @Description 音乐工具类
 * @Date 2020/12/11
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
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
            AudioHeader audioHeader = af.getAudioHeader();
            return getDuration(audioHeader);
        } catch (Exception e) {
            return 0;
        }
    }

    // 从头信息获取音频文件时长
    private static double getDuration(AudioHeader audioHeader) {
        // 获取精确时长
        if (audioHeader instanceof MP3AudioHeader) {
            MP3AudioHeader mp3AudioHeader = (MP3AudioHeader) audioHeader;
            return mp3AudioHeader.getPreciseTrackLength();
        } else {
            GenericAudioHeader genericAudioHeader = (GenericAudioHeader) audioHeader;
            return genericAudioHeader.getPreciseLength();
        }
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
            Tag tag = af.getTagOrCreateAndSetDefault();

            setAlbumImageToTag(albumImg, tag, musicInfo.getFormat());
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
        String genre = mediaInfo.getGenre();
        String comment = mediaInfo.getComment();
        String recordLabel = mediaInfo.getRecordLabel();
        BufferedImage albumImg = mediaInfo.getAlbumImage();

        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(audioFile);
            Tag tag = af.getTagOrCreateAndSetDefault();

            setAlbumImageToTag(albumImg, tag, mediaInfo.getFormat());
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.ALBUM, albumName);
            tag.setField(FieldKey.GENRE, genre);
            tag.setField(FieldKey.COMMENT, comment);
            tag.setField(FieldKey.RECORD_LABEL, recordLabel);

            af.commit();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    // 为 Tag 写入封面图片
    private static void setAlbumImageToTag(BufferedImage albumImg, Tag tag, String format) throws FieldDataInvalidException {
        // 设置封面之前必须先清除原有的字段！
        tag.deleteArtworkField();
        if (albumImg == null) return;
        switch (format) {
            case Format.MP3:
                MetadataBlockDataPicture picture = new MetadataBlockDataPicture(ImageUtil.toBytes(albumImg), PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "",
                        albumImg.getWidth(), albumImg.getHeight(), 24, 0);
                Artwork artwork = Artwork.createArtworkFromMetadataBlockDataPicture(picture);
                tag.setField(artwork);
                break;
            case Format.FLAC:
                FlacTag flacTag = (FlacTag) tag;
                flacTag.setField(flacTag.createArtworkField(albumImg, PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "", 24, 0));
                break;
            case Format.M4A:
                Mp4Tag mp4Tag = (Mp4Tag) tag;
                mp4Tag.setField(mp4Tag.createArtworkField(ImageUtil.toBytes(albumImg)));
                break;
        }
    }

    /**
     * 补全 AudioFile 信息(格式、曲名、艺术家、专辑、时长)
     *
     * @param file
     * @return
     */
    public static void fillAudioFileInfo(AudioFile file) {
        try {
            file.setFormat(FileUtil.getSuffix(file).toLowerCase());

            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
            Tag tag = af.getTagOrCreateAndSetDefault();

            String title = StringUtil.fixEncoding(tag.getFirst(FieldKey.TITLE));
            String artist = StringUtil.fixEncoding(tag.getFirst(FieldKey.ARTIST));
            String album = StringUtil.fixEncoding(tag.getFirst(FieldKey.ALBUM));

            file.setSongName(title);
            file.setArtist(artist);
            file.setAlbum(album);
            // 从头信息获取时长
            file.setDuration(getDuration(af.getAudioHeader()));
        } catch (Exception e) {

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
            Tag tag = af.getTagOrCreateAndSetDefault();
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) albumImage = artwork.getImage();
            return albumImage;
        } catch (Exception e) {
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
            Tag tag = af.getTagOrCreateAndSetDefault();

            String genre = StringUtil.fixEncoding(tag.getFirst(FieldKey.GENRE));
            String comment = StringUtil.fixEncoding(tag.getFirst(FieldKey.COMMENT));
            String recordLabel = StringUtil.fixEncoding(tag.getFirst(FieldKey.RECORD_LABEL));

            mediaInfo.setGenre(genre);
            mediaInfo.setComment(comment);
            mediaInfo.setRecordLabel(recordLabel);
        } catch (Exception e) {

        }
        return mediaInfo;
    }

    /**
     * 将音频/视频文件转成指定格式
     *
     * @param source
     * @return
     */
    public static void convert(File source, File dest) {
        TerminalUtil.execSync(SimplePath.PLUGIN_PATH + String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
    }
}

package net.doge.util.media;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
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
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.awt.*;
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
    /**
     * 获取音频文件时长
     *
     * @param file
     */
    public static double getDuration(AudioFile file) {
        try {
            if (file.isMp3()) {
                Mp3File f = new Mp3File(file);
                return (double) f.getLengthInMilliseconds() / 1000;
            } else if (file.isFlac() || file.isM4a()) {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
                AudioHeader ah = af.getAudioHeader();
                return ah.getTrackLength();
            } else {
                Encoder encoder = new Encoder();
                MultimediaInfo info = encoder.getInfo(file);
                return (double) info.getDuration() / 1000;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 为音频文件写入信息(包含曲名、艺术家、专辑、封面图片)
     *
     * @param sourcePath
     * @param musicInfo
     */
    public static void writeAudioFileInfo(String sourcePath, NetMusicInfo musicInfo) {
        if (!musicInfo.hasAlbumImage()) musicInfo.setInvokeLater(() -> startWrite(sourcePath, musicInfo));
        else startWrite(sourcePath, musicInfo);
    }

    private static void startWrite(String sourcePath, NetMusicInfo musicInfo) {
        String name = musicInfo.getName();
        String artist = musicInfo.getArtist();
        String albumName = musicInfo.getAlbumName();
        BufferedImage albumImg = musicInfo.getAlbumImage();

        // 创建临时文件
        File destFile = new File(sourcePath);
        try {
            // mp3
            if (musicInfo.isMp3()) {
                File tempFile = new File(SimplePath.CACHE_PATH + File.separator + "temp - " + destFile.getName());
                FileUtil.copy(sourcePath, tempFile.getAbsolutePath());
                // 将 tag 设置进 MP3 并保存文件
                Mp3File mp3file = new Mp3File(tempFile.getAbsolutePath());
                ID3v2 tag = mp3file.getId3v2Tag();
                // 注意有些歌曲没有 ID3v2 标签，需要创建一个 ID3v24 标签设置进去！
                if (tag == null) tag = new ID3v24Tag();
                if (albumImg != null) tag.setAlbumImage(ImageUtil.toBytes(albumImg), "image/png");
                if (StringUtil.notEmpty(name)) tag.setTitle(name);
                if (StringUtil.notEmpty(artist)) tag.setArtist(artist);
                if (StringUtil.notEmpty(albumName)) tag.setAlbum(albumName);
                mp3file.setId3v2Tag(tag);
                mp3file.save(destFile.getAbsolutePath());
                // 退出时将临时文件删除
                tempFile.deleteOnExit();
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(destFile);
                Tag tag = af.getTag();
                if (albumImg != null) {
                    if (musicInfo.isFlac()) {
                        FlacTag flacTag = (FlacTag) tag;
                        flacTag.setField(flacTag.createArtworkField(albumImg, PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "", 24, 0));
                    } else if (musicInfo.isM4a()) {
                        Mp4Tag mp4Tag = (Mp4Tag) tag;
                        mp4Tag.setField(mp4Tag.createArtworkField(ImageUtil.toBytes(albumImg)));
                    }
                }
                if (StringUtil.notEmpty(name)) tag.setField(FieldKey.TITLE, name);
                if (StringUtil.notEmpty(artist)) tag.setField(FieldKey.ARTIST, artist);
                if (StringUtil.notEmpty(albumName)) tag.setField(FieldKey.ALBUM, albumName);
                af.commit();
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 为音频文件写入信息(通过 MediaInfo)
     *
     * @param sourcePath
     * @param mediaInfo
     */
    public static void writeAudioFileInfo(String sourcePath, MediaInfo mediaInfo) {
        String title = mediaInfo.getTitle();
        String artist = mediaInfo.getArtist();
        String albumName = mediaInfo.getAlbum();
        String genre = mediaInfo.getGenre();
        String comment = mediaInfo.getComment();
        String copyright = mediaInfo.getCopyright();
        BufferedImage albumImg = mediaInfo.getAlbumImage();

        File destFile = new File(sourcePath);
        try {
            // mp3
            if (mediaInfo.isMp3()) {
                // 创建临时文件
                File tempFile = new File(SimplePath.CACHE_PATH + File.separator + "temp - " + destFile.getName());
                FileUtil.copy(sourcePath, tempFile.getAbsolutePath());
                // 将 tag 设置进 MP3 并保存文件
                Mp3File mp3file = new Mp3File(tempFile.getAbsolutePath());
                ID3v2 tag = mp3file.getId3v2Tag();
                // 注意有些歌曲没有 ID3v2 标签，需要创建一个 ID3v24 标签设置进去！
                if (tag == null) tag = new ID3v24Tag();
                if (albumImg != null) tag.setAlbumImage(ImageUtil.toBytes(albumImg), "image/png");
                tag.setTitle(title);
                tag.setArtist(artist);
                tag.setAlbum(albumName);
                if (StringUtil.notEmpty(genre)) tag.setGenreDescription(genre);
                tag.setComment(comment);
                tag.setCopyright(copyright);
                mp3file.setId3v2Tag(tag);
                mp3file.save(destFile.getAbsolutePath());
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(destFile);
                Tag tag = af.getTag();
                if (albumImg != null) {
                    if (mediaInfo.isFlac()) {
                        FlacTag flacTag = (FlacTag) tag;
                        flacTag.setField(flacTag.createArtworkField(albumImg, PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "", 24, 0));
                    } else if (mediaInfo.isM4a()) {
                        Mp4Tag mp4Tag = (Mp4Tag) tag;
                        mp4Tag.setField(mp4Tag.createArtworkField(ImageUtil.toBytes(albumImg)));
                    }
                }
                if (StringUtil.notEmpty(title)) tag.setField(FieldKey.TITLE, title);
                if (StringUtil.notEmpty(artist)) tag.setField(FieldKey.ARTIST, artist);
                if (StringUtil.notEmpty(albumName)) tag.setField(FieldKey.ALBUM, albumName);
                if (StringUtil.notEmpty(genre)) tag.setField(FieldKey.GENRE, genre);
                tag.setField(FieldKey.COMMENT, comment);
                // flac m4a 无版权信息，跳过
                af.commit();
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 补全 AudioFile 信息(曲名、艺术家、专辑、时长)
     *
     * @param file
     * @return
     */
    public static void fillAudioFileInfo(AudioFile file) {
        // 歌曲信息完整时跳出
//        if (file.isIntegrated()) return;
        try {
            file.setFormat(FileUtil.getSuffix(file).toLowerCase());
            // mp3
            if (file.isMp3()) {
                Mp3File f = new Mp3File(file);
                file.setDuration((double) f.getLengthInMilliseconds() / 1000);
                ID3v1 tag = null;
                // 先从 ID3v2 找信息
                if (f.hasId3v2Tag()) tag = f.getId3v2Tag();
                    // 若没有 ID3v2 ，在 ID3v1 找
                else if (f.hasId3v1Tag()) tag = f.getId3v1Tag();
                if (tag == null) return;
                String title = tag.getTitle();
                String artist = tag.getArtist();
                String album = tag.getAlbum();
                file.setSongName(title);
                file.setArtist(artist);
                file.setAlbum(album);
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(file);
                Tag tag = af.getTag();
                String title = tag.getFirst(FieldKey.TITLE);
                String artist = tag.getFirst(FieldKey.ARTIST);
                String album = tag.getFirst(FieldKey.ALBUM);
                file.setSongName(title);
                file.setArtist(artist);
                file.setAlbum(album);
            }
            file.setDuration(getDuration(file));
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
            // mp3
            if (source.isMp3()) {
                Mp3File f = new Mp3File(source);
                if (f.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = f.getId3v2Tag();
                    byte[] imageBytes = id3v2Tag.getAlbumImage();
                    if (imageBytes != null) {
                        Image img = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
                        albumImage = ImageUtil.toBufferedImage(img);
                    }
                }
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
                Tag tag = af.getTag();
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) albumImage = artwork.getImage();
            }
            return albumImage;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从文件获取流派描述信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getGenre(AudioFile source) {
        try {
            String genre = null;
            // mp3
            if (source.isMp3()) {
                Mp3File f = new Mp3File(source);
                // 先从 ID3v2 找信息
                if (f.hasId3v2Tag()) genre = f.getId3v2Tag().getGenreDescription();
                    // 若没有 ID3v2 ，在 ID3v1 找
                else if (f.hasId3v1Tag()) genre = f.getId3v1Tag().getGenreDescription();
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
                Tag tag = af.getTag();
                genre = tag.getFirst(FieldKey.GENRE);
            }
            return genre;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从文件获取注释信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getComment(AudioFile source) {
        try {
            String comment = null;
            // mp3
            if (source.isMp3()) {
                Mp3File f = new Mp3File(source);
                // 先从 ID3v2 找信息
                if (f.hasId3v2Tag()) comment = f.getId3v2Tag().getComment();
                    // 若没有 ID3v2 ，在 ID3v1 找
                else if (f.hasId3v1Tag()) comment = f.getId3v1Tag().getComment();
            }
            // flac m4a
            else {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(source);
                Tag tag = af.getTag();
                comment = tag.getFirst(FieldKey.COMMENT);
            }
            return comment;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从文件获取版权信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getCopyright(AudioFile source) {
        try {
            String copyright = null;
            // mp3
            if (source.isMp3()) {
                Mp3File f = new Mp3File(source);
                if (f.hasId3v2Tag()) copyright = f.getId3v2Tag().getCopyright();
            }
            // flac m4a 文件暂无版权字段
            return copyright;
        } catch (Exception e) {
            return null;
        }
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

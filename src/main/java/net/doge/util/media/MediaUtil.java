package net.doge.util.media;

import com.mpatric.mp3agic.*;
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
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Doge
 * @Description 音乐工具类
 * @Date 2020/12/11
 */
public class MediaUtil {

    static {

    }

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
            // flac
            else if (musicInfo.isFlac()) {
                Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
                org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(destFile);
                FlacTag tag = (FlacTag) af.getTag();
                if (albumImg != null)
                    tag.setField(tag.createArtworkField(albumImg, PictureTypes.DEFAULT_ID, ImageFormats.MIME_TYPE_PNG, "", 24, 0));
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

        // 创建临时文件
        File destFile = new File(sourcePath);
        File tempFile = new File(SimplePath.CACHE_PATH + File.separator + "temp - " + destFile.getName());
        FileUtil.copy(sourcePath, tempFile.getAbsolutePath());
        try {
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
            file.setFormat(FileUtil.getSuffix(file));
            if (!file.isMp3()) {
                file.setDuration(getDuration(file));
                return;
            }
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
        } catch (IOException | UnsupportedTagException | InvalidDataException | IllegalArgumentException e) {

        }
    }

    /**
     * 从文件获取封面图片，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static BufferedImage getAlbumImage(File source) {
        try {
            Mp3File f = new Mp3File(source);
            BufferedImage albumImage = null;
            if (f.hasId3v2Tag()) {
                ID3v2 id3v2Tag = f.getId3v2Tag();
                byte[] imageBytes = id3v2Tag.getAlbumImage();
                if (imageBytes != null) {
                    Image img = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
                    albumImage = ImageUtil.toBufferedImage(img);
                }
            }
            return albumImage;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            return null;
        }
    }

    /**
     * 从文件获取流派描述信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getGenre(File source) {
        try {
            Mp3File f = new Mp3File(source);
            String genre = null;
            // 先从 ID3v2 找信息
            if (f.hasId3v2Tag()) genre = f.getId3v2Tag().getGenreDescription();
                // 若没有 ID3v2 ，在 ID3v1 找
            else if (f.hasId3v1Tag()) genre = f.getId3v1Tag().getGenreDescription();
            return genre;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            return null;
        }
    }

    /**
     * 从文件获取注释信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getComment(File source) {
        try {
            Mp3File f = new Mp3File(source);
            String comment = null;
            // 先从 ID3v2 找信息
            if (f.hasId3v2Tag()) comment = f.getId3v2Tag().getComment();
                // 若没有 ID3v2 ，在 ID3v1 找
            else if (f.hasId3v1Tag()) comment = f.getId3v1Tag().getComment();
            return comment;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            return null;
        }
    }

    /**
     * 从文件获取版权信息，若没有，返回 null
     *
     * @param source
     * @return
     */
    public static String getCopyright(File source) {
        try {
            Mp3File f = new Mp3File(source);
            String copyright = null;
            if (f.hasId3v2Tag()) copyright = f.getId3v2Tag().getCopyright();
            return copyright;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
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

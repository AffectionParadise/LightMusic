package net.doge.util.media;

import com.mpatric.mp3agic.*;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import net.doge.constant.player.Format;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.AudioFile;
import net.doge.model.entity.MediaInfo;
import net.doge.model.entity.NetMusicInfo;
import net.doge.util.common.StringUtil;
import net.doge.util.system.FileUtil;
import net.doge.util.system.TerminateUtil;
import net.doge.util.ui.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 音乐工具类
 * @Date 2020/12/11
 */
public class MusicUtil {
    /**
     * 获取音频文件时长
     *
     * @param source
     */
    public static double getDuration(File source) {
        try {
            if (source.getName().endsWith(Format.MP3)) {
                Mp3File f = new Mp3File(source);
                return (double) f.getLengthInMilliseconds() / 1000;
            } else {
                Encoder encoder = new Encoder();
                MultimediaInfo info = encoder.getInfo(source);
                return (double) info.getDuration() / 1000;
            }
        } catch (IOException | EncoderException | UnsupportedTagException | InvalidDataException e) {
            return 0;
        }
    }

    /**
     * 为 MP3 写入信息(包含曲名、艺术家、专辑、封面图片)
     *
     * @param sourcePath
     * @param netMusicInfo
     */
    public static void writeMP3Info(String sourcePath, NetMusicInfo netMusicInfo) {
        if (!netMusicInfo.hasAlbumImage()) netMusicInfo.setInvokeLater(() -> startWrite(sourcePath, netMusicInfo));
        else startWrite(sourcePath, netMusicInfo);
    }

    private static void startWrite(String sourcePath, NetMusicInfo netMusicInfo) {
        String name = netMusicInfo.getName();
        String artist = netMusicInfo.getArtist();
        String albumName = netMusicInfo.getAlbumName();
        BufferedImage albumImg = netMusicInfo.getAlbumImage();

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
            if (albumImg != null) tag.setAlbumImage(ImageUtil.toBytes(albumImg), "image/jpeg");
            if (StringUtil.notEmpty(name)) tag.setTitle(name);
            if (StringUtil.notEmpty(artist)) tag.setArtist(artist);
            if (StringUtil.notEmpty(albumName)) tag.setAlbum(albumName);
            mp3file.setId3v2Tag(tag);
            mp3file.save(destFile.getAbsolutePath());
            // 退出时将临时文件删除
            tempFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为 MP3 写入信息(通过 MediaInfo)
     *
     * @param sourcePath
     * @param mediaInfo
     */
    public static void writeMP3Info(String sourcePath, MediaInfo mediaInfo) {
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
            if (albumImg != null) tag.setAlbumImage(ImageUtil.toBytes(albumImg), "image/jpeg");
            tag.setTitle(title);
            tag.setArtist(artist);
            tag.setAlbum(albumName);
            if (StringUtil.notEmpty(genre)) tag.setGenreDescription(genre);
            tag.setComment(comment);
            tag.setCopyright(copyright);
            mp3file.setId3v2Tag(tag);
            mp3file.save(destFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 补全 AudioFile 信息(曲名、艺术家、专辑、时长)
     *
     * @param source
     * @return
     */
    public static void fillAudioFileInfo(AudioFile source) {
        // 歌曲信息完整时跳出
//        if (source.isIntegrated()) return;
        try {
            source.setFormat(FileUtil.getSuffix(source));
            if (!source.isMp3()) {
                source.setDuration(getDuration(source));
                return;
            }
            Mp3File f = new Mp3File(source);
            source.setDuration((double) f.getLengthInMilliseconds() / 1000);
            ID3v1 tag = null;
            // 先从 ID3v2 找信息
            if (f.hasId3v2Tag()) tag = f.getId3v2Tag();
                // 若没有 ID3v2 ，在 ID3v1 找
            else if (f.hasId3v1Tag()) tag = f.getId3v1Tag();
            if (tag == null) return;
            String title = tag.getTitle();
            String artist = tag.getArtist();
            String album = tag.getAlbum();
            source.setSongName(title);
            source.setArtist(artist);
            source.setAlbum(album);
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
                    Image image = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
                    albumImage = ImageUtil.imageToBufferedImage(image);
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
     * 将音频文件转成指定格式
     *
     * @param source
     * @return
     */
    public static void convert(AudioFile source, AudioFile dest) {
        TerminateUtil.exec(String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
    }
}

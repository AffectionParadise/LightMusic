package net.doge.utils;

import cn.hutool.core.img.Img;
import cn.hutool.core.util.RuntimeUtil;
import com.mpatric.mp3agic.*;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import net.doge.constants.Format;
import net.doge.constants.SimplePath;
import net.doge.models.AudioFile;
import net.doge.models.MediaInfo;
import net.doge.models.NetMusicInfo;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 视频工具类
 * @Date 2020/12/11
 */
public class VideoUtils {
    /**
     * 将视频文件转成指定格式
     *
     * @param source
     * @return
     */
    public static void convert(File source, File dest) {
        try {
            CmdUtils.exec(String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

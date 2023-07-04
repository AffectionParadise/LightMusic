package net.doge.util;

import java.io.File;

/**
 * @Author yzx
 * @Description 视频工具类
 * @Date 2020/12/11
 */
public class VideoUtil {
    /**
     * 将视频文件转成指定格式
     *
     * @param source
     * @return
     */
    public static void convert(File source, File dest) {
        TerminateUtil.exec(String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
    }
}

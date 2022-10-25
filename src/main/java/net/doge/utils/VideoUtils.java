package net.doge.utils;

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
        TerminateUtils.exec(String.format("ffmpeg -i \"%s\" \"%s\"", source.getPath(), dest.getPath()));
    }
}

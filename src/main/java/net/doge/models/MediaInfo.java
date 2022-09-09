package net.doge.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/12
 */
@Data
@AllArgsConstructor
public class MediaInfo {
    // 标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 流派
    private String genre;
    // 注释
    private String comment;
    // 版权
    private String copyright;
    // 封面图片
    private BufferedImage albumImage;
}

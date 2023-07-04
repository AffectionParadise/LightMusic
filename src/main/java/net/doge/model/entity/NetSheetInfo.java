package net.doge.model.entity;

import lombok.Data;
import net.doge.util.common.StringUtil;

import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description 评论
 * @Date 2020/12/7
 */
@Data
public class NetSheetInfo {
    // 来源
    private int source;
    // 乐谱 id
    private String id;
    // 乐谱名
    private String name;
    // 封面
    private BufferedImage coverImg;
    // 页数
    private Integer pageSize;
    // 难度
    private String difficulty;
    // 调
    private String musicKey;
    // 版本
    private String playVersion;
    // 弦
    private String chordName;
    // 每分钟节拍数
    private Integer bpm;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public void setCoverImg(BufferedImage coverImg) {
        this.coverImg = coverImg;
        callback();
    }

    public boolean hasCoverImg() {
        return coverImg != null;
    }

    public boolean hasDifficulty() {
        return StringUtil.isNotEmpty(difficulty);
    }

    public boolean hasMusicKey() {
        return StringUtil.isNotEmpty(musicKey);
    }

    public boolean hasPlayVersion() {
        return StringUtil.isNotEmpty(playVersion);
    }

    public boolean hasChordName() {
        return StringUtil.isNotEmpty(chordName);
    }

    public boolean hasPageSize() {
        return pageSize != null && pageSize > 0;
    }

    public boolean hasBpm() {
        return bpm != null && bpm > 0;
    }

    private void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    public String toString() {
        return name;
    }
}

package net.doge.constant.core.lyric;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Doge
 * @description 歌词类型
 * @date 2020/12/7
 */
@Getter
@AllArgsConstructor
public enum LyricType {
    // 翻译
    TRANSLATION(0),
    // 罗马音
    ROMA(1),
    // 繁体
    TRADITIONAL_CN(2);

    private int index;

    public static LyricType fromIndex(int index) {
        for (LyricType type : values()) {
            if (type.index == index) return type;
        }
        return null;
    }
}

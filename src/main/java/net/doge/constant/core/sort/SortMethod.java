package net.doge.constant.core.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Doge
 * @description 排序方式
 * @date 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum SortMethod {
    // 曲名和文件名混合
    BY_SONG_AND_FILE_NAME(50),
    // 曲名
    BY_SONG_NAME(100),
    // 艺术家
    BY_ARTIST_NAME(200),
    // 专辑
    BY_ALBUM_NAME(300),
    // 文件名
    BY_FILE_NAME(400),
    // 时长
    BY_DURATION(500),
    // 创建时间
    BY_CREATION_TIME(600),
    // 修改时间
    BY_LAST_MODIFIED_TIME(700),
    // 访问时间
    BY_LAST_ACCESS_TIME(800),
    // 大小
    BY_SIZE(900);

    private int code;

    public static SortMethod fromCode(int code) {
        for (SortMethod sortMethod : values()) {
            if (sortMethod.code == code) return sortMethod;
        }
        return null;
    }
}

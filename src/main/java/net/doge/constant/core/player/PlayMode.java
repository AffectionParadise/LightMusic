package net.doge.constant.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Doge
 * @Description 播放模式
 * @Date 2020/12/7
 */
@Getter
@AllArgsConstructor
public enum PlayMode {
    // 播完暂停
    DISABLED(100),
    // 单曲循环
    SINGLE(101),
    // 顺序播放
    SEQUENCE(102),
    // 列表循环
    LIST_CYCLE(103),
    // 随机播放
    SHUFFLE(104);

    private int code;

    public static PlayMode fromCode(int code) {
        for (PlayMode playMode : values()) {
            if (playMode.code == code) return playMode;
        }
        return null;
    }
}

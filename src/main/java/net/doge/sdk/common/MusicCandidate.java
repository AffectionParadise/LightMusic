package net.doge.sdk.common;

import lombok.AllArgsConstructor;
import net.doge.model.entity.NetMusicInfo;

@AllArgsConstructor
public class MusicCandidate {
    public NetMusicInfo musicInfo;
    public double weight;

    @Override
    public String toString() {
        return musicInfo + "：" + weight;
    }
}

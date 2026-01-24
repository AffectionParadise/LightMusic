package net.doge.sdk.service.music.info.entity;

import lombok.AllArgsConstructor;
import net.doge.entity.service.NetMusicInfo;

@AllArgsConstructor
public class MusicCandidate {
    public NetMusicInfo musicInfo;
    public double weight;

    @Override
    public String toString() {
        return musicInfo + "：" + weight;
    }
}

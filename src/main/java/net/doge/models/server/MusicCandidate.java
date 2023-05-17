package net.doge.models.server;

import lombok.AllArgsConstructor;
import net.doge.models.entities.NetMusicInfo;

@AllArgsConstructor
public class MusicCandidate {
    public NetMusicInfo musicInfo;
    public double weight;
}

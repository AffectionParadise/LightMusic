package net.doge.sdk.service.music.info;

import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.os.Format;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.music.info.entity.MusicCandidate;
import net.doge.sdk.service.music.info.impl.musicurl.*;
import net.doge.sdk.service.music.search.MusicSearchReq;
import net.doge.util.core.StringUtil;

import java.util.LinkedList;
import java.util.List;

public class MusicUrlReq {
    private static MusicUrlReq instance;

    private MusicUrlReq() {
    }

    public static MusicUrlReq getInstance() {
        if (instance == null) instance = new MusicUrlReq();
        return instance;
    }

    /**
     * 补充 NetMusicInfo 的 url
     */
    public void fillMusicUrl(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的且音质与设置的音质相同
        if (musicInfo.isIntegrated() && musicInfo.isQualityMatch()) return;

        // 无链接，直接换源
        String url = fetchMusicUrl(musicInfo);
        if (StringUtil.notEmpty(url)) musicInfo.setUrl(url);
        else fillAvailableMusicUrl(musicInfo);

        String realUrl = musicInfo.getUrl();
        if (realUrl.contains(".mp3") || realUrl.contains(".wav")) musicInfo.setFormat(Format.MP3);
        else if (realUrl.contains(".flac")) musicInfo.setFormat(Format.FLAC);
        else if (realUrl.contains(".m4a")) musicInfo.setFormat(Format.M4A);

        // 更新音质
        musicInfo.setQuality(AudioQuality.quality);
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetResourceSource.NC:
                return NcMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.KG:
                return KgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.QQ:
                return QqMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.KW:
                return KwMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.MG:
                return MgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.QI:
                return QiMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.HF:
                return HfMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.GG:
                return GgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.FS:
                return FsMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.XM:
                return XmMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.ME:
                return MeMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.BI:
                return BiMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.FA:
                return FaMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.LZ:
                return LzMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            case NetResourceSource.QS:
                return QsMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            default:
                return "";
        }
    }

    /**
     * 歌曲换源
     *
     * @param musicInfo
     * @return
     */
    public void fillAvailableMusicUrl(NetMusicInfo musicInfo) {
        CommonResult<NetMusicInfo> result = MusicSearchReq.getInstance().searchMusic(NetResourceSource.ALL, 0, "默认", musicInfo.toKeywords(), 1, 20);
        List<NetMusicInfo> data = result.data;
        List<MusicCandidate> candidates = new LinkedList<>();
        MusicInfoReq musicInfoReq = MusicInfoReq.getInstance();
        for (NetMusicInfo info : data) {
            // 部分歌曲没有时长，先填充时长，准备判断
            if (!info.hasDuration()) musicInfoReq.fillDuration(info);
            double nameSimi = StringUtil.similar(info.getName(), musicInfo.getName());
            double artistSimi = StringUtil.similar(info.getArtist(), musicInfo.getArtist());
            double albumSimi = StringUtil.similar(info.getAlbumName(), musicInfo.getAlbumName());
            // 匹配依据：歌名、歌手相似度，时长之差绝对值。如果合适，纳入候选者
            if (info.equals(musicInfo)
                    || nameSimi == 0
                    || artistSimi == 0
                    || info.hasDuration() && musicInfo.hasDuration() && Math.abs(info.getDuration() - musicInfo.getDuration()) > 3)
                continue;
            double weight = nameSimi * 2 + artistSimi + albumSimi * 2;
            candidates.add(new MusicCandidate(info, weight));
        }
        // 将所有候选的匹配按照相关度排序
        candidates.sort((c1, c2) -> Double.compare(c2.weight, c1.weight));
        for (MusicCandidate candidate : candidates) {
            NetMusicInfo info = candidate.musicInfo;
            String url = fetchMusicUrl(info);
            if (StringUtil.isEmpty(url)) continue;
            musicInfo.setUrl(url);
            if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDuration());
            return;
        }
    }
}

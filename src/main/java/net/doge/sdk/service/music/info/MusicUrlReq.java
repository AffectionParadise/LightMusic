package net.doge.sdk.service.music.info;

import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.os.Format;
import net.doge.constant.service.NetMusicSource;
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
            // 网易云
            case NetMusicSource.NC:
                return NcMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 酷狗
            case NetMusicSource.KG:
                return KgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // QQ
            case NetMusicSource.QQ:
                return QqMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 酷我
            case NetMusicSource.KW:
                return KwMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 咪咕
            case NetMusicSource.MG:
                return MgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 千千
            case NetMusicSource.QI:
                return QiMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 音乐磁场
            case NetMusicSource.HF:
                return HfMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 咕咕咕音乐
            case NetMusicSource.GG:
                return GgMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 5sing
            case NetMusicSource.FS:
                return FsMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 喜马拉雅
            case NetMusicSource.XM:
                return XmMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 猫耳
            case NetMusicSource.ME:
                return MeMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 哔哩哔哩
            case NetMusicSource.BI:
                return BiMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 发姐
            case NetMusicSource.FA:
                return FaMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
            // 李志
            case NetMusicSource.LZ:
                return LzMusicUrlReq.getInstance().fetchMusicUrl(musicInfo);
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
        CommonResult<NetMusicInfo> result = MusicSearchReq.getInstance().searchMusic(NetMusicSource.ALL, 0, "默认", musicInfo.toKeywords(), 1, 20);
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

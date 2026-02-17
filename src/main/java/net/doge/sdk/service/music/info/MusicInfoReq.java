package net.doge.sdk.service.music.info;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.impl.musicinfo.*;

public class MusicInfoReq {
    private static MusicInfoReq instance;

    private MusicInfoReq() {
    }

    public static MusicInfoReq getInstance() {
        if (instance == null) instance = new MusicInfoReq();
        return instance;
    }

    /**
     * 补充 NetMusicInfo 歌曲时长
     */
    public void fillDuration(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            // 咪咕
            case NetMusicSource.MG:
                MgMusicInfoReq.getInstance().fillDuration(musicInfo);
                break;
        }
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public void fillMusicInfo(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的
        if (musicInfo.isIntegrated()) return;
        int source = musicInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                NcMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 酷狗
            case NetMusicSource.KG:
                KgMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // QQ
            case NetMusicSource.QQ:
                QqMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 酷我
            case NetMusicSource.KW:
                KwMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 咪咕
            case NetMusicSource.MG:
                MgMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 千千
            case NetMusicSource.QI:
                QiMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 音乐磁场
            case NetMusicSource.HF:
                HfMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 咕咕咕音乐
            case NetMusicSource.GG:
                GgMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 5sing
            case NetMusicSource.FS:
                FsMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 喜马拉雅
            case NetMusicSource.XM:
                XmMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 猫耳
            case NetMusicSource.ME:
                MeMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 哔哩哔哩
            case NetMusicSource.BI:
                BiMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 发姐
            case NetMusicSource.FA:
                FaMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
            // 李志
            case NetMusicSource.LZ:
                LzMusicInfoReq.getInstance().fillMusicInfo(musicInfo);
                break;
        }
    }

    /**
     * 为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public void fillLyric(NetMusicInfo musicInfo) {
        // 歌词完整
        if (musicInfo.isLyricIntegrated()) return;
        int source = musicInfo.getSource();
        switch (source) {
            // 网易云
            case NetMusicSource.NC:
                NcMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 酷狗
            case NetMusicSource.KG:
                KgMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // QQ
            case NetMusicSource.QQ:
                QqMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 酷我
            case NetMusicSource.KW:
                KwMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 咪咕
            case NetMusicSource.MG:
                MgMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 千千
            case NetMusicSource.QI:
                QiMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 音乐磁场
            case NetMusicSource.HF:
                HfMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 咕咕咕音乐
            case NetMusicSource.GG:
                GgMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 5sing
            case NetMusicSource.FS:
                FsMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 猫耳
            case NetMusicSource.ME:
                MeMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 哔哩哔哩
            case NetMusicSource.BI:
                BiMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 发姐
            case NetMusicSource.FA:
                FaMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            // 李志
            case NetMusicSource.LZ:
                LzMusicInfoReq.getInstance().fillLyric(musicInfo);
                break;
            default:
                musicInfo.setLyric("");
                musicInfo.setTrans("");
                musicInfo.setRoma("");
                break;
        }
    }
}

package net.doge.sdk.service.music.search;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.music.search.impl.musicsearch.*;

public class MusicSearchReq {
    private static MusicSearchReq instance;

    private MusicSearchReq() {
    }

    public static MusicSearchReq getInstance() {
        if (instance == null) instance = new MusicSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取歌曲
     */
    public CommonResult<NetMusicInfo> searchMusic(int src, int type, String subType, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetMusicInfo> executor = new MultiCommonResultCallableExecutor<>();
        boolean dt = "默认".equals(subType);
        switch (type) {
            // 歌词
            case 1:
                if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
                    executor.submit(() -> NcMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
                    executor.submit(() -> KgMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
                    executor.submit(() -> QqMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
                    executor.submit(() -> MgMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                break;
            // 节目
            case 2:
                if (dt) {
                    if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
                        executor.submit(() -> NcMusicSearchReq.getInstance().searchVoice(keyword, page, limit));
                    if (src == NetMusicSource.XM || src == NetMusicSource.ALL)
                        executor.submit(() -> XmMusicSearchReq.getInstance().searchProgram(keyword, page, limit));
                }
                if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
                    executor.submit(() -> MeMusicSearchReq.getInstance().searchProgram(subType, keyword, page, limit));
                break;
            // 常规
            default:
                if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
                    executor.submit(() -> NcMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
                    executor.submit(() -> KgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
                    executor.submit(() -> QqMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
                    executor.submit(() -> KwMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
                    executor.submit(() -> MgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
                    executor.submit(() -> QiMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.HF || src == NetMusicSource.ALL)
                    executor.submit(() -> HfMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.GG || src == NetMusicSource.ALL)
                    executor.submit(() -> GgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
                    executor.submit(() -> FsMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
        }
        return executor.getResult();
    }
}

package net.doge.sdk.service.music.search;

import net.doge.constant.service.source.NetResourceSource;
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
                if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
                    executor.submit(() -> NcMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
                    executor.submit(() -> KgMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
                    executor.submit(() -> QqMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                if (src == NetResourceSource.MG || src == NetResourceSource.ALL)
                    executor.submit(() -> MgMusicSearchReq.getInstance().searchMusicByLyric(keyword, page, limit));
                break;
            // 节目
            case 2:
                if (dt) {
                    if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
                        executor.submit(() -> NcMusicSearchReq.getInstance().searchVoice(keyword, page, limit));
                    if (src == NetResourceSource.XM || src == NetResourceSource.ALL)
                        executor.submit(() -> XmMusicSearchReq.getInstance().searchProgram(keyword, page, limit));
                }
                if (src == NetResourceSource.ME || src == NetResourceSource.ALL)
                    executor.submit(() -> MeMusicSearchReq.getInstance().searchProgram(subType, keyword, page, limit));
                break;
            // 常规
            default:
                if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
                    executor.submit(() -> NcMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.KG || src == NetResourceSource.ALL)
                    executor.submit(() -> KgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
                    executor.submit(() -> QqMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.KW || src == NetResourceSource.ALL)
                    executor.submit(() -> KwMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.MG || src == NetResourceSource.ALL)
                    executor.submit(() -> MgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.QI || src == NetResourceSource.ALL)
                    executor.submit(() -> QiMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.HF || src == NetResourceSource.ALL)
                    executor.submit(() -> HfMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.GG || src == NetResourceSource.ALL)
                    executor.submit(() -> GgMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.FS || src == NetResourceSource.ALL)
                    executor.submit(() -> FsMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
                if (src == NetResourceSource.QS || src == NetResourceSource.ALL)
                    executor.submit(() -> QsMusicSearchReq.getInstance().searchMusic(keyword, page, limit));
        }
        return executor.getResult();
    }
}

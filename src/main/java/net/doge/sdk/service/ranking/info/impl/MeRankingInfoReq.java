package net.doge.sdk.service.ranking.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankingInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;

import java.util.LinkedList;
import java.util.List;

public class MeRankingInfoReq {
    private static MeRankingInfoReq instance;

    private MeRankingInfoReq() {
    }

    public static MeRankingInfoReq getInstance() {
        if (instance == null) instance = new MeRankingInfoReq();
        return instance;
    }

    // 榜单信息 API (猫耳)
    private final String RANKING_DETAIL_ME_API = "https://www.missevan.com/sound/soundalllist?albumid=%s";

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankingInfo(NetRankingInfo rankingInfo) {
        String id = rankingInfo.getId();
        String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONObject data = rankingInfoJson.getJSONObject("info").getJSONObject("album");

        String description = HtmlUtil.removeHtmlLabel(data.getString("intro"));

        GlobalExecutors.imageExecutor.execute(() -> rankingInfo.setCoverImg(SdkUtil.getImageFromUrl(rankingInfo.getCoverImgUrl())));
        rankingInfo.setDescription(description);
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRanking(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject rankingInfoJson = JSONObject.parseObject(rankingInfoBody);
        JSONObject data = rankingInfoJson.getJSONObject("info");
        JSONArray songArray = data.getJSONArray("sounds");
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(page * limit, songArray.size()); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("id");
            String name = songJson.getString("soundstr");
            String artist = songJson.getString("username");
            String artistId = songJson.getString("user_id");
            Double duration = songJson.getDouble("duration") / 1000;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.ME);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setDuration(duration);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}

package net.doge.sdk.service.rank.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;

import java.util.LinkedList;
import java.util.List;

public class MeRankInfoReq {
    private static MeRankInfoReq instance;

    private MeRankInfoReq() {
    }

    public static MeRankInfoReq getInstance() {
        if (instance == null) instance = new MeRankInfoReq();
        return instance;
    }

    // 榜单信息 API (猫耳)
    private final String RANK_DETAIL_ME_API = "https://www.missevan.com/sound/soundalllist?albumid=%s";

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public void fillRankInfo(NetRankInfo rankInfo) {
        String id = rankInfo.getId();
        String rankInfoBody = HttpRequest.get(String.format(RANK_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("info").getJSONObject("album");

        String description = HtmlUtil.removeHtmlLabel(data.getString("intro"));

        GlobalExecutors.imageExecutor.execute(() -> rankInfo.setCoverImg(SdkUtil.getImageFromUrl(rankInfo.getCoverImgUrl())));
        rankInfo.setDescription(description);
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRank(String id, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String rankInfoBody = HttpRequest.get(String.format(RANK_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject rankInfoJson = JSONObject.parseObject(rankInfoBody);
        JSONObject data = rankInfoJson.getJSONObject("info");
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

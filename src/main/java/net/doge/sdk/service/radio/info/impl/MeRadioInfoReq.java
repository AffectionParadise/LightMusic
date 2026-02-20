package net.doge.sdk.service.radio.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeRadioInfoReq {
    private static MeRadioInfoReq instance;

    private MeRadioInfoReq() {
    }

    public static MeRadioInfoReq getInstance() {
        if (instance == null) instance = new MeRadioInfoReq();
        return instance;
    }

    // 电台信息 API (猫耳)
    private final String RADIO_DETAIL_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 电台节目 API (猫耳)
//    private final String RADIO_PROGRAM_ME_API = "https://www.missevan.com/dramaapi/getdramaepisodedetails?drama_id=%s&p=%s&page_size=%s";

    /**
     * 根据电台 id 获取电台
     */
    public CommonResult<NetRadioInfo> getRadioInfo(String id) {
        List<NetRadioInfo> res = new LinkedList<>();
        Integer t = 1;

        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject info = radioInfoJson.getJSONObject("info");
        JSONObject drama = info.getJSONObject("drama");
        JSONObject episodes = info.getJSONObject("episodes");

        String radioId = drama.getString("id");
        String radioName = drama.getString("name");
        String dj = drama.getString("author");
        String djId = drama.getString("user_id");
        Long playCount = drama.getLong("view_count");
        // 猫耳的电台可能有多种类型！
        int episodeSize = episodes.getJSONArray("episode").size();
        int ftSize = episodes.getJSONArray("ft").size();
        int musicSize = episodes.getJSONArray("music").size();
        Integer trackCount = episodeSize + ftSize + musicSize;
        String category = drama.getString("catalog_name");
        String coverImgThumbUrl = drama.getString("cover");

        NetRadioInfo radioInfo = new NetRadioInfo();
        radioInfo.setSource(NetResourceSource.ME);
        radioInfo.setId(radioId);
        radioInfo.setName(radioName);
        radioInfo.setDj(dj);
        radioInfo.setDjId(djId);
        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        radioInfo.setPlayCount(playCount);
        radioInfo.setTrackCount(trackCount);
        radioInfo.setCategory(category);

        GlobalExecutors.imageExecutor.execute(() -> {
            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
            radioInfo.setCoverImgThumb(coverImgThumb);
        });

        res.add(radioInfo);

        return new CommonResult<>(res, t);
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        String id = radioInfo.getId();
        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject info = radioInfoJson.getJSONObject("info");
        JSONObject drama = info.getJSONObject("drama");
        JSONObject episodes = info.getJSONObject("episodes");

        String coverImgUrl = drama.getString("cover");
        String description = drama.getString("abstract");

        if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        if (!radioInfo.hasTag()) radioInfo.setTag(SdkUtil.parseTag(drama));
        if (!radioInfo.hasDescription()) radioInfo.setDescription(HtmlUtil.removeHtmlLabel(description));
        if (!radioInfo.hasDj()) radioInfo.setDj(drama.getString("author"));
        if (!radioInfo.hasDjId()) radioInfo.setDjId(drama.getString("user_id"));
        if (!radioInfo.hasCategory()) radioInfo.setCategory(drama.getString("catalog_name"));
        if (!radioInfo.hasTrackCount()) {
            // 猫耳的电台可能有多种类型！
            int episodeSize = episodes.getJSONArray("episode").size();
            int ftSize = episodes.getJSONArray("ft").size();
            int musicSize = episodes.getJSONArray("music").size();
            Integer trackCount = episodeSize + ftSize + musicSize;
            radioInfo.setTrackCount(trackCount);
        }
        if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(drama.getLong("view_count"));
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = radioInfo.getId();
        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject info = radioInfoJson.getJSONObject("info");
        // 猫耳的电台可能有多种类型！
        JSONObject episodes = info.getJSONObject("episodes");
        JSONArray songArray = episodes.getJSONArray("episode");
        songArray.addAll(episodes.getJSONArray("ft"));
        songArray.addAll(episodes.getJSONArray("music"));
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = songArray.getJSONObject(i);

            String songId = programJson.getString("sound_id");
            String name = programJson.getString("name");
            // 艺术家与电台作者不一致！
//                String artist = radioInfo.getDj();
//                String artistId = radioInfo.getDjId();
            String albumName = radioInfo.getName();
            String albumId = radioInfo.getId();
            Double duration = programJson.getDouble("duration") / 1000;

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.ME);
            musicInfo.setId(songId);
            musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            musicInfo.setDuration(duration);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}

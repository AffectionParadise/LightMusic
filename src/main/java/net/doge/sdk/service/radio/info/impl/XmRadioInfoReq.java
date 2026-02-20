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

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class XmRadioInfoReq {
    private static XmRadioInfoReq instance;

    private XmRadioInfoReq() {
    }

    public static XmRadioInfoReq getInstance() {
        if (instance == null) instance = new XmRadioInfoReq();
        return instance;
    }

    // 电台信息 API (喜马拉雅)
    private final String RADIO_DETAIL_XM_API = "https://www.ximalaya.com/revision/album/v1/simple?albumId=%s";
    // 简短电台信息 API (喜马拉雅)
    private final String BRIEF_RADIO_DETAIL_XM_API = "https://www.ximalaya.com/tdk-web/seo/search/albumInfo?albumId=%s";
    // 电台节目 API (喜马拉雅)
    private final String RADIO_PROGRAM_XM_API = "http://www.ximalaya.com/revision/album/v1/getTracksList?albumId=%s&sort=%s&&pageNum=%s&pageSize=%s";

    /**
     * 根据电台 id 获取电台
     */
    public CommonResult<NetRadioInfo> getRadioInfo(String id) {
        List<NetRadioInfo> res = new LinkedList<>();
        Integer t = 1;

        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_XM_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("data");
        JSONObject radioJson = data.getJSONObject("albumPageMainInfo");

        String radioId = data.getString("albumId");
        String radioName = radioJson.getString("albumTitle");
//                String dj = radioJson.getString("nickname");
        String djId = radioJson.getString("anchorUid");
        Long playCount = radioJson.getLong("playCount");
//                Integer trackCount = radioJson.getIntValue("programCount");
        String coverImgThumbUrl = "https:" + radioJson.getString("cover");

        NetRadioInfo radioInfo = new NetRadioInfo();
        radioInfo.setSource(NetResourceSource.XM);
        radioInfo.setId(radioId);
        radioInfo.setName(radioName);
//                radioInfo.setDj(dj);
        radioInfo.setDjId(djId);
        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
        radioInfo.setPlayCount(playCount);
//                radioInfo.setTrackCount(trackCount);
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
        if (!radioInfo.hasTrackCount()) {
            GlobalExecutors.requestExecutor.execute(() -> {
                String radioInfoBody = HttpRequest.get(String.format(BRIEF_RADIO_DETAIL_XM_API, id))
                        .executeAsStr();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject radioJson = radioInfoJson.getJSONObject("data");

                radioInfo.setTrackCount(radioJson.getIntValue("trackCount"));
            });
        }
        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_XM_API, id))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject radioJson = radioInfoJson.getJSONObject("data").getJSONObject("albumPageMainInfo");

        String coverImgUrl = "https:" + radioJson.getString("cover");
        String tag = SdkUtil.parseTag(radioJson);
        String description = radioJson.getString("shortIntro");

        if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        if (!radioInfo.hasTag()) radioInfo.setTag(tag);
        if (!radioInfo.hasDescription()) radioInfo.setDescription(description);
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int sortType, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = radioInfo.getId();
        String radioInfoBody = HttpRequest.get(String.format(RADIO_PROGRAM_XM_API, id, sortType, page, limit))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("data");
        total = data.getIntValue("trackTotalCount");
        JSONArray songArray = data.getJSONArray("tracks");
        for (int i = 0, len = songArray.size(); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("trackId");
            String name = songJson.getString("title");
            String artist = songJson.getString("anchorName");
            String artistId = songJson.getString("anchorId");
            Double duration = songJson.getDouble("duration");
            String albumName = songJson.getString("albumTitle");
            String albumId = songJson.getString("albumId");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.XM);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setArtistId(artistId);
            musicInfo.setDuration(duration);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);
            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}

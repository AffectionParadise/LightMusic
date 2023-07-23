package net.doge.sdk.entity.sheet;

import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetSheetInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.JsonUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;

public class SheetReq {
    // 获取乐谱 API
    private final String GET_SHEETS_API = "https://interface3.music.163.com/eapi/music/sheet/list/v1";
    // 获取乐谱图片 API
    private final String GET_SHEETS_IMG_API = "https://interface3.music.163.com/eapi//music/sheet/preview/info?id=%s";

    /**
     * 获取歌曲乐谱
     */
    public CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String id = musicInfo.getId();
        LinkedList<NetSheetInfo> sheetInfos = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NET_CLOUD) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String sheetInfoBody = SdkCommon.ncRequest(Method.POST, GET_SHEETS_API, String.format("{\"id\":\"%s\",\"abTest\":\"b\"}", id), options)
                    .execute()
                    .body();
            JSONObject sheetInfoJson = JSONObject.parseObject(sheetInfoBody);
            JSONObject data = sheetInfoJson.getJSONObject("data");
            JSONArray sheetArray = data.getJSONArray("musicSheetSimpleInfoVOS");
            if (JsonUtil.notEmpty(sheetArray)) {
                total = sheetArray.size();
                for (int i = 0, len = sheetArray.size(); i < len; i++) {
                    JSONObject sheetJson = sheetArray.getJSONObject(i);

                    String sheetId = sheetJson.getString("id");
                    String name = sheetJson.getString("name");
                    String coverImgUrl = sheetJson.getString("coverImageUrl");
                    String difficulty = sheetJson.getString("difficulty");
                    String musicKey = sheetJson.getString("musicKey");
                    String playVersion = sheetJson.getString("playVersion");
                    String chordName = sheetJson.getString("chordName");
                    Integer pageSize = sheetJson.getIntValue("totalPageSize");
                    Integer bpm = sheetJson.getIntValue("bpm");

                    NetSheetInfo sheetInfo = new NetSheetInfo();
                    sheetInfo.setId(sheetId);
                    sheetInfo.setName(name);
                    sheetInfo.setDifficulty(difficulty);
                    sheetInfo.setMusicKey(musicKey);
                    sheetInfo.setPlayVersion(playVersion);
                    sheetInfo.setChordName(chordName);
                    sheetInfo.setPageSize(pageSize);
                    sheetInfo.setBpm(bpm);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImg = SdkUtil.extractCover(coverImgUrl);
                        sheetInfo.setCoverImg(coverImg);
                    });

                    sheetInfos.add(sheetInfo);
                }
            }
        }

        return new CommonResult<>(sheetInfos, total);
    }

    /**
     * 获取乐谱图片链接
     */
    public CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        int source = sheetInfo.getSource();
        String id = sheetInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NET_CLOUD) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eApi("/api//music/sheet/preview/info");
            String imgInfoBody = SdkCommon.ncRequest(Method.POST, String.format(GET_SHEETS_IMG_API, id), String.format("{\"id\":\"%s\"}", id), options)
                    .execute()
                    .body();
            JSONObject imgInfoJson = JSONObject.parseObject(imgInfoBody);
            JSONArray imgArray = imgInfoJson.getJSONArray("data");
            total = imgArray.size();
            for (int i = 0, len = imgArray.size(); i < len; i++) {
                JSONObject imgJson = imgArray.getJSONObject(i);
                imgUrls.add(imgJson.getString("url"));
            }
        }

        return new CommonResult<>(imgUrls, total);
    }
}

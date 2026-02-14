package net.doge.sdk.service.sheet;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetSheetInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.JsonUtil;
import net.doge.util.http.constant.Method;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SheetReq {
    private static SheetReq instance;

    private SheetReq() {
    }

    public static SheetReq getInstance() {
        if (instance == null) instance = new SheetReq();
        return instance;
    }

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
        List<NetSheetInfo> res = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String sheetInfoBody = SdkCommon.ncRequest(Method.POST, GET_SHEETS_API, String.format("{\"id\":\"%s\",\"abTest\":\"b\"}", id), options)
                    .executeAsStr();
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

                    res.add(sheetInfo);
                }
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取乐谱图片链接
     */
    public CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        int source = sheetInfo.getSource();
        String id = sheetInfo.getId();
        List<String> res = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api//music/sheet/preview/info");
            String imgInfoBody = SdkCommon.ncRequest(Method.POST, String.format(GET_SHEETS_IMG_API, id), String.format("{\"id\":\"%s\"}", id), options)
                    .executeAsStr();
            JSONObject imgInfoJson = JSONObject.parseObject(imgInfoBody);
            JSONArray imgArray = imgInfoJson.getJSONArray("data");
            total = imgArray.size();
            for (int i = 0, len = imgArray.size(); i < len; i++) {
                JSONObject imgJson = imgArray.getJSONObject(i);
                res.add(imgJson.getString("url"));
            }
        }

        return new CommonResult<>(res, total);
    }
}

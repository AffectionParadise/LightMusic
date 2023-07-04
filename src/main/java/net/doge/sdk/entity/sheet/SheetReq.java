package net.doge.sdk.entity.sheet;

import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetSheetInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class SheetReq {
    // 获取乐谱 API
    private final String GET_SHEETS_API = SdkCommon.prefix + "/sheet/list?id=%s";
    // 获取乐谱图片 API
    private final String GET_SHEETS_IMG_API = SdkCommon.prefix + "/sheet/preview?id=%s";

    /**
     * 获取歌曲乐谱
     */
    public CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        String id = musicInfo.getId();
        LinkedList<NetSheetInfo> sheetInfos = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NET_CLOUD) {
            String sheetInfoBody = HttpRequest.get(String.format(GET_SHEETS_API, id))
                    .execute()
                    .body();
            JSONObject sheetInfoJson = JSONObject.fromObject(sheetInfoBody);
            JSONObject data = sheetInfoJson.getJSONObject("data");
            JSONArray sheetArray = data.optJSONArray("musicSheetSimpleInfoVOS");
            if (sheetArray != null) {
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
                    Integer pageSize = sheetJson.getInt("totalPageSize");
                    Integer bpm = sheetJson.getInt("bpm");

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
            String imgInfoBody = HttpRequest.get(String.format(GET_SHEETS_IMG_API, id))
                    .execute()
                    .body();
            JSONObject imgInfoJson = JSONObject.fromObject(imgInfoBody);
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

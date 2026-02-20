package net.doge.sdk.service.radio.rcmd.impl.hotradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeHotRadioReq {
    private static MeHotRadioReq instance;

    private MeHotRadioReq() {
    }

    public static MeHotRadioReq getInstance() {
        if (instance == null) instance = new MeHotRadioReq();
        return instance;
    }

    // 周榜电台 API (猫耳)
    private final String WEEK_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=1&page=%s&page_size=%s";
    // 月榜电台 API (猫耳)
    private final String MONTH_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=2&page=%s&page_size=%s";
    // 总榜电台 API (猫耳)
    private final String ALL_TIME_RADIO_ME_API = "https://www.missevan.com/reward/drama-reward-rank?period=3&page=%s&page_size=%s";
    // 广播剧分类电台 API (猫耳)
    private final String CAT_RADIO_ME_API = "https://www.missevan.com/dramaapi/filter?filters=%s_0_%s_%s_0&page=%s&order=1&page_size=%s";

    /**
     * 周榜
     */
    public CommonResult<NetRadioInfo> getWeekRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        String radioInfoBody = HttpRequest.get(String.format(WEEK_RADIO_ME_API, page, limit))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
        JSONArray radioArray = data.getJSONArray("Datas");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = radioJson.getString("author");
            String coverImgThumbUrl = radioJson.getString("cover");
            String description = radioJson.getString("abstract");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.ME);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setDescription(description);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 月榜
     */
    public CommonResult<NetRadioInfo> getMonthRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        String radioInfoBody = HttpRequest.get(String.format(MONTH_RADIO_ME_API, page, limit))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
        JSONArray radioArray = data.getJSONArray("Datas");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = radioJson.getString("author");
            String coverImgThumbUrl = radioJson.getString("cover");
            String description = radioJson.getString("abstract");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.ME);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setDescription(description);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 总榜
     */
    public CommonResult<NetRadioInfo> getAllTimeRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        String radioInfoBody = HttpRequest.get(String.format(ALL_TIME_RADIO_ME_API, page, limit))
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
        JSONArray radioArray = data.getJSONArray("Datas");
        t = data.getJSONObject("pagination").getIntValue("count");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = radioJson.getString("author");
            String coverImgThumbUrl = radioJson.getString("cover");
            String description = radioJson.getString("abstract");

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetMusicSource.ME);
            radioInfo.setId(radioId);
            radioInfo.setName(radioName);
            radioInfo.setDj(dj);
            radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            radioInfo.setDescription(description);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                radioInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(radioInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 广播剧分类
     */
    public CommonResult<NetRadioInfo> getCatRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagType.CAT_RADIO_ME];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ");
            String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_ME_API, sp[2], sp[0], sp[1], page, limit))
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getIntValue("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String category = radioJson.getString("type_name");
                String coverImgThumbUrl = radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}

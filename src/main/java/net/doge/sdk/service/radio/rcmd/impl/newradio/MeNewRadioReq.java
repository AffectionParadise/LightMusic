package net.doge.sdk.service.radio.rcmd.impl.newradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeNewRadioReq {
    private static MeNewRadioReq instance;

    private MeNewRadioReq() {
    }

    public static MeNewRadioReq getInstance() {
        if (instance == null) instance = new MeNewRadioReq();
        return instance;
    }

    // 推荐广播剧 API (猫耳)
    private final String REC_RADIO_ME_API = "https://www.missevan.com/drama/site/recommend";
    // 夏日推荐 API (猫耳)
    private final String SUMMER_RADIO_ME_API = "https://www.missevan.com/dramaapi/summerdrama";
    // 频道 API (猫耳)
//    private final String CHANNEL_ME_API = "https://www.missevan.com/explore/channels?type=0";

    /**
     * 推荐广播剧
     */
    public CommonResult<NetRadioInfo> getRecRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t;

        String radioInfoBody = HttpRequest.get(REC_RADIO_ME_API)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("info");
        t = radioArray.size();
        for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
            JSONObject radioJson = radioArray.getJSONObject(i);

            String radioId = radioJson.getString("id");
            String radioName = radioJson.getString("name");
            String dj = radioJson.getString("author");
            String coverImgThumbUrl = "https:" + radioJson.getString("cover");
            String description = HtmlUtil.removeHtmlLabel(radioJson.getString("abstract"));

            NetRadioInfo radioInfo = new NetRadioInfo();
            radioInfo.setSource(NetResourceSource.ME);
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
     * 夏日推荐
     */
    public CommonResult<NetRadioInfo> getSummerRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;

        String radioInfoBody = HttpRequest.get(SUMMER_RADIO_ME_API)
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONArray radioArray = radioInfoJson.getJSONArray("info");
        for (int i = 0, len = radioArray.size(); i < len; i++) {
            JSONArray array = radioArray.getJSONArray(i);
            for (int j = 0, s = array.size(); j < s; j++, t++) {
                if (t >= (page - 1) * limit && t < page * limit) {
                    JSONObject radioJson = array.getJSONObject(j);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getString("author");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetResourceSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }
            }
        }
        return new CommonResult<>(r, t);
    }

//    /**
//     * 频道
//     */
//    public CommonResult<NetRadioInfo> getChannels(int page, int limit) {
    //            List<NetRadioInfo> r = new LinkedList<>();
//            Integer t = 0;
//
//            String radioInfoBody = HttpRequest.get(CHANNEL_ME_API)
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(radioInfoBody);
//            Elements radios = doc.select(".item.blk > a");
//            t = radios.size();
//            for (int i = (page - 1) * limit, len = Math.min(radios.size(), page * limit); i < len; i++) {
//                Element radio = radios.get(i);
//
//                String radioId = radio.attr("href").replace("/explore/channel/","");
//                String radioName = radio.select("b").text();
//                String coverImgThumbUrl = "https:" + radioJson.getString("cover");
//                String description = StringUtil.removeHTMLLabel(radioJson.getString("abstract"));
//
//                NetRadioInfo radioInfo = new NetRadioInfo();
//                radioInfo.setSource(NetMusicSource.ME);
//                radioInfo.setId(radioId);
//                radioInfo.setName(radioName);
//                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setDescription(description);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
//                    radioInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                r.add(radioInfo);
//            }
//            return new CommonResult<>(r, t);
//    }
}

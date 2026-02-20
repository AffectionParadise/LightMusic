package net.doge.sdk.service.radio.rcmd.impl.newradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QqNewRadioReq {
    private static QqNewRadioReq instance;

    private QqNewRadioReq() {
    }

    public static QqNewRadioReq getInstance() {
        if (instance == null) instance = new QqNewRadioReq();
        return instance;
    }

    /**
     * 推荐电台
     */
    public CommonResult<NetRadioInfo> getRecommendRadios(int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;

        String radioInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody("{\"songlist\":{\"module\":\"mb_track_radio_svr\",\"method\":\"get_radio_track\"," +
                        "\"param\":{\"id\":99,\"firstplay\":1,\"num\":15}},\"radiolist\":{\"module\":\"pf.radiosvr\"," +
                        "\"method\":\"GetRadiolist\",\"param\":{\"ct\":\"24\"}},\"comm\":{\"ct\":24,\"cv\":0}}")
                .executeAsStr();
        JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
        JSONObject data = radioInfoJson.getJSONObject("radiolist").getJSONObject("data");
        JSONArray radioList = data.getJSONArray("radio_list");
        for (int i = 0, len = radioList.size(); i < len; i++) {
            JSONArray radioArray = radioList.getJSONObject(i).getJSONArray("list");
            for (int j = 0, l = radioArray.size(); j < l; j++, t++) {
                if (t >= (page - 1) * limit && t < page * limit) {
                    JSONObject radioJson = radioArray.getJSONObject(j);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String coverImgUrl = radioJson.getString("pic_url");
                    String coverImgThumbUrl = coverImgUrl;
                    Long playCount = radioJson.getLong("listenNum");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetResourceSource.QQ);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setPlayCount(playCount);
                    // QQ 需要提前写入电台图片 url，电台信息接口不提供！
                    radioInfo.setCoverImgUrl(coverImgUrl);
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
}

package net.doge.sdk.service.rank.fetch.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetRankInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class QsRankFetchReq {
    private static QsRankFetchReq instance;

    private QsRankFetchReq() {
    }

    public static QsRankFetchReq getInstance() {
        if (instance == null) instance = new QsRankFetchReq();
        return instance;
    }

    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankInfo> getRanks() {
        List<NetRankInfo> r = new LinkedList<>();
        int t = 0;

        // 汽水官方并未提供 Rank 列表接口，只能暂时用内置
        String rankInfoBody = "[{\"ID\":\"7036274230471712007\",\"Name\":\"热歌榜\",\"Description\":\"汽水音乐内每周热度最高的50首歌，每周四更新\"," +
                "\"Cover\":\"https://p3-luna.douyinpic.com/img/tos-cn-i-b829550vbb/d0d8d48461a62748e84689cdf049b19a.png~tplv-b829550vbb-resize:960:960.png\"," +
                "\"TrackCount\":50},{\"ID\":\"7060812597884869927\",\"Name\":\"新歌榜\",\"Description\":\"近期发行的热度最高的50首新歌，每周四更新\"," +
                "\"Cover\":\"https://p3-luna.douyinpic.com/img/tos-cn-i-b829550vbb/f12f7eb5b54d0899c7c724df009668a8.png~tplv-b829550vbb-resize:960:960.png\",\"TrackCount\":50}," +
                "{\"ID\":\"7061475546400005410\",\"Name\":\"欧美榜\",\"Description\":\"汽水音乐内每周热度最高的50首外文歌曲，每周四更新\"," +
                "\"Cover\":\"https://p3-luna.douyinpic.com/img/tos-cn-i-b829550vbb/33747550ed5499b58feda42a21748637.png~tplv-b829550vbb-resize:960:960.png\",\"TrackCount\":50}," +
                "{\"ID\":\"7415959718721494311\",\"Name\":\"音乐人歌曲榜\",\"Description\":\"抖音音乐人开放平台上传歌曲，综合每周站内热度进行排序展示\"," +
                "\"Cover\":\"https://p3-luna.douyinpic.com/img/tos-cn-v-2774c002/o8FQKiQQBxHWa2hzsBNAgYOX6iEHEAibADAbfB~tplv-b829550vbb-resize:960:960.png\",\"TrackCount\":25}]";
        JSONArray rankArray = JSONArray.parseArray(rankInfoBody);
        for (int i = 0, len = rankArray.size(); i < len; i++) {
            JSONObject rankJson = rankArray.getJSONObject(i);

            String rankId = rankJson.getString("ID");
            String rankName = rankJson.getString("Name");
            String description = rankJson.getString("Description");
            String coverImgUrl = rankJson.getString("Cover");

            NetRankInfo rankInfo = new NetRankInfo();
            rankInfo.setSource(NetResourceSource.QS);
            rankInfo.setId(rankId);
            rankInfo.setName(rankName);
            rankInfo.setCoverImgUrl(coverImgUrl);
            rankInfo.setDescription(description);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                rankInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(rankInfo);
        }
        return new CommonResult<>(r, t);
    }
}

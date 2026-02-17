package net.doge.sdk.service.music.info.impl.musicurl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.fs.FiveSingReqOptEnum;
import net.doge.sdk.common.opt.fs.FiveSingReqOptsBuilder;
import net.doge.util.core.StringUtil;

import java.util.Map;
import java.util.TreeMap;

public class FsMusicUrlReq {
    private static FsMusicUrlReq instance;

    private FsMusicUrlReq() {
    }

    public static FsMusicUrlReq getInstance() {
        if (instance == null) instance = new FsMusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (5sing)
    private final String SONG_URL_FS_API = "http://service.5sing.kugou.com/song/getsongurl";

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String[] sp = id.split("_");
        Map<String, Object> params = new TreeMap<>();
        params.put("songtype", sp[0]);
        params.put("songid", sp[1]);
        params.put("version", "6.6.72");
        Map<FiveSingReqOptEnum, Object> options = FiveSingReqOptsBuilder.get(SONG_URL_FS_API);
        String songBody = SdkCommon.fsRequest(params, null, options)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
        String url = AudioQuality.quality >= AudioQuality.SUPER ? data.getString("squrl") : "";
        if (StringUtil.isEmpty(url)) url = AudioQuality.quality >= AudioQuality.HIGH ? data.getString("hqurl") : "";
        if (StringUtil.isEmpty(url)) url = data.getString("lqurl");
        return url;
    }
}

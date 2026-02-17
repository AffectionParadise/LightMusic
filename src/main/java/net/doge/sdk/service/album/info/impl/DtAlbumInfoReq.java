package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.time.TimeUtil;

public class DtAlbumInfoReq {
    private static DtAlbumInfoReq instance;

    private DtAlbumInfoReq() {
    }

    public static DtAlbumInfoReq getInstance() {
        if (instance == null) instance = new DtAlbumInfoReq();
        return instance;
    }

    // 专辑信息 API (堆糖)
    private final String ALBUM_DETAIL_DT_API = "https://www.duitang.com/napi/album/detail/?album_id=%s";

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        String id = albumInfo.getId();
        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DT_API, id))
                .executeAsStr();
        JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
        JSONObject albumJson = albumInfoJson.getJSONObject("data");

        String coverImgUrl = albumJson.getJSONArray("covers").getString(0);
        String description = albumJson.getString("desc");
        if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getIntValue("count"));
        if (!albumInfo.hasPublishTime())
            albumInfo.setPublishTime(TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000));

        if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        albumInfo.setDescription(description);
    }
}

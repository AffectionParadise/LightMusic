package net.doge.sdk.service.album.menu.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.http.HttpRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DtAlbumMenuReq {
    private static DtAlbumMenuReq instance;

    private DtAlbumMenuReq() {
    }

    public static DtAlbumMenuReq getInstance() {
        if (instance == null) instance = new DtAlbumMenuReq();
        return instance;
    }

    // 获取专辑照片 API (堆糖)
    private final String GET_ALBUMS_IMG_DT_API = "https://www.duitang.com/napi/vienna/blog/by_album/";

    /**
     * 获取专辑照片链接
     */
    public CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        String id = albumInfo.getId();
        List<String> imgUrls = new LinkedList<>();
        int total;

        Map<String, Object> params = new HashMap<>();
        params.put("album_id", id);
        params.put("after_id", cursor);
        params.put("limit", limit);
        params.put("_", System.currentTimeMillis());
        String imgInfoBody = HttpRequest.get(GET_ALBUMS_IMG_DT_API)
                .queryParams(params)
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(imgInfoBody).getJSONObject("data");
        JSONArray imgs = data.getJSONArray("object_list");
        cursor = data.getString("after");
        total = page * limit;
        if (data.getIntValue("more") == 1) total++;
        else total = (page - 1) * limit + imgs.size();
        for (int i = 0, len = imgs.size(); i < len; i++) {
            JSONObject img = imgs.getJSONObject(i);
            imgUrls.add(img.getJSONObject("photo").getString("path"));
        }

        return new CommonResult<>(imgUrls, total, cursor);
    }
}

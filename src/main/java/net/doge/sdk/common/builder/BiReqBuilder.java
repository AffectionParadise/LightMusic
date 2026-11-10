package net.doge.sdk.common.builder;

import com.alibaba.fastjson2.JSONObject;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.util.Map;
import java.util.TreeMap;

public class BiReqBuilder {
    private static BiReqBuilder instance;

    private BiReqBuilder() {
    }

    public static BiReqBuilder getInstance() {
        if (instance == null) instance = new BiReqBuilder();
        return instance;
    }

    // 认证 API
    private final String AUTH_API = "https://api.bilibili.com/x/web-interface/nav";

    private final int[] mixinKeyEncTab = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    // 计算 mixinKey
    private String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) key.append(s.charAt(mixinKeyEncTab[i]));
        return key.toString();
    }

    // 哔哩哔哩使用 WBI 鉴权的 API 使用此方法构造请求
    public HttpRequest buildRequest(String baseUrl, Map<String, Object> params) {
        // 公共 query
        if (params == null) params = new TreeMap<>();
        params.put("wts", System.currentTimeMillis() / 1000);

        // 获取 img_key 和 sub_key
        String authBody = HttpRequest.get(AUTH_API)
                .executeAsStr();
        JSONObject authJson = JSONObject.parseObject(authBody);
        JSONObject data = authJson.getJSONObject("data");
        JSONObject wbiImg = data.getJSONObject("wbi_img");
        String imgUrl = wbiImg.getString("img_url");
        String subUrl = wbiImg.getString("sub_url");
        String fileNameExp = "/([0-9a-z]+)\\.png";
        String imgKey = RegexUtil.getGroup1(fileNameExp, imgUrl);
        String subKey = RegexUtil.getGroup1(fileNameExp, subUrl);

        String query = UrlUtil.buildQuery(params);
        String mixinKey = getMixinKey(imgKey, subKey);
        String wbiSign = CryptoUtil.md5Hex(query + mixinKey);
        String url = baseUrl + "?" + query + "&w_rid=" + wbiSign;
        return HttpRequest.get(url)
                .cookie(SdkCommon.BI_COOKIE);
    }
}

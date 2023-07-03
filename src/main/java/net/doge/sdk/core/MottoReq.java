package net.doge.sdk.core;

import cn.hutool.http.HttpRequest;
import net.sf.json.JSONObject;

public class MottoReq {
    // 格言 API
    private final String MOTTO_API = "https://v1.hitokoto.cn/?encode=json&lang=cn&c=d&c=i";

    /**
     * 获取格言
     *
     * @return
     */
    public String getMotto() {
        String mottoBody = HttpRequest.get(String.format(MOTTO_API))
                .execute()
                .body();
        JSONObject mottoJson = JSONObject.fromObject(mottoBody);
        String content = mottoJson.getString("hitokoto");
        String from = mottoJson.getString("from");
        String fromWho = mottoJson.getString("from_who");
        return "「" + content + "」    —— " + ("null".equals(fromWho) ? "" : fromWho)
                + ("null".equals(from) || from.equals(fromWho) ? "" : String.format("《%s》", from.replaceAll("《|》", "")));
    }
}

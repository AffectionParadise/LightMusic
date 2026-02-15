package net.doge.sdk.service.motto;

import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

public class MottoReq {
    private static MottoReq instance;

    private MottoReq() {
    }

    public static MottoReq getInstance() {
        if (instance == null) instance = new MottoReq();
        return instance;
    }

    // 格言 API
    private final String MOTTO_API = "https://v1.hitokoto.cn/?encode=json&lang=cn&c=d&c=i";

    /**
     * 获取格言
     *
     * @return
     */
    public String getMotto() {
        String mottoBody = HttpRequest.get(MOTTO_API)
                .executeAsStr();
        JSONObject mottoJson = JSONObject.parseObject(mottoBody);
        String content = mottoJson.getString("hitokoto");
        String from = mottoJson.getString("from");
        String fromWho = mottoJson.getString("from_who");
        return "「" + content + "」    —— " + (StringUtil.isEmpty(fromWho) ? "" : fromWho)
                + (StringUtil.isEmpty(from) || from.equals(fromWho) ? "" : String.format("《%s》", from.replaceAll("《|》", "")));
    }
}

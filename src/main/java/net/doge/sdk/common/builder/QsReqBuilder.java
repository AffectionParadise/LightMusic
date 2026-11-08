package net.doge.sdk.common.builder;

import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

import java.util.HashMap;
import java.util.Map;

public class QsReqBuilder {
    private static QsReqBuilder instance;

    private QsReqBuilder() {
    }

    public static QsReqBuilder getInstance() {
        if (instance == null) instance = new QsReqBuilder();
        return instance;
    }

    public HttpRequest buildRequest(String baseUrl, Map<String, Object> params) {
        // 公共 query
        if (params == null) params = new HashMap<>();
        params.put("device_platform", "android");
        params.put("os", "android");
        params.put("ssmix", "a");
        params.put("cdid", "46556f98-1720-4248-83da-62b74b60b46a");
        params.put("channel", "xiaomi_8478_64");
        params.put("aid", "386088");
        params.put("app_name", "luna");
        params.put("version_code", "100198030");
        params.put("version_name", "19.8.0");
        params.put("manifest_version_code", "100198030");
        params.put("update_version_code", "100198030");
        params.put("resolution", "1080*1920");
        params.put("dpi", "480");
        params.put("device_type", "ABR-AL80");
        params.put("device_brand", "HUAWEI");
        params.put("language", "zh");
        params.put("os_api", "35");
        params.put("os_version", "15");
        params.put("ac", "wifi");
        params.put("device_model", "ABR-AL80");
        params.put("save_power", "0");
        params.put("font_size", "1.00");
        params.put("luna_first_launch_apk_type", "normal_apk");
        params.put("diversion_channel_name", "xiaomi_8478_64");
        params.put("is_car_play", "0");
        params.put("battery", "0.99");
        params.put("network_speed", "10156");
        params.put("hybrid_version_code", "100198030");
        params.put("tz_name", "Asia/Shanghai");
        params.put("tz_offset", "28800");
        params.put("luna_register_time", "1784311292");
        params.put("diversion_category_level_two", "Xiaomi商店-自然");
        params.put("package", "com.luna.music");
        params.put("charge", "0");
        params.put("luna_apk_type", "normal_apk");
        params.put("output_device_type", "Phone");
        params.put("volume", "1.00");
        params.put("brightness", "0.08");
        params.put("need_personal_recommend", "1");
        params.put("is_teen_mode", "0");
        params.put("sim_region", "cn");
        params.put("diversion_category_level_one", "厂商商店-自然");
        params.put("android_device_type", "default");
        params.put("iid", "2204957404569386");
        params.put("device_id", "2204957404565290");
        params.put("_rticket", System.currentTimeMillis());

        String url = baseUrl + "?" + UrlUtil.buildQuery(params);
        return HttpRequest.get(url);
    }
}

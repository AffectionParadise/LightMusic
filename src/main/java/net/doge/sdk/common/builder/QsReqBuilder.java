package net.doge.sdk.common.builder;

import net.doge.sdk.common.constant.qs.QsSearchType;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;

public class QsReqBuilder {
    private static QsReqBuilder instance;

    private QsReqBuilder() {
    }

    public static QsReqBuilder getInstance() {
        if (instance == null) instance = new QsReqBuilder();
        return instance;
    }

    // 搜索 API (汽水)
    private final String SEARCH_QS_API = "https://api.qishui.com/luna/search/%s?device_platform=android&os=android&ssmix=a&cdid=46556f98-1720-4248-83da-62b74b60b46a" +
            "&channel=xiaomi_8478_64&aid=386088&app_name=luna&version_code=100198030&version_name=19.8.0&manifest_version_code=100198030&update_version_code=100198030" +
            "&resolution=1080*1920&dpi=480&device_type=ABR-AL80&device_brand=HUAWEI&language=zh&os_api=35&os_version=15&ac=wifi&device_model=ABR-AL80&save_power=0&font_size=1.00" +
            "&luna_first_launch_apk_type=normal_apk&diversion_channel_name=xiaomi_8478_64&is_car_play=0&battery=0.99&network_speed=10156&hybrid_version_code=100198030" +
            "&tz_name=Asia/Shanghai&tz_offset=28800&luna_register_time=1784311292&diversion_category_level_two=Xiaomi商店-自然&package=com.luna.music" +
            "&charge=0&luna_apk_type=normal_apk&output_device_type=Phone&volume=1.00&brightness=0.08&need_personal_recommend=1&is_teen_mode=0&sim_region=cn" +
            "&diversion_category_level_one=厂商商店-自然&android_device_type=default&iid=2204957404569386&device_id=2204957404565290&_rticket=%s&q=%s&cursor=%s&count=%s";

    public HttpRequest buildSearchRequest(QsSearchType type, String keyword, int page, int limit) {
        String searchType;
        switch (type) {
            case SONG:
            default:
                searchType = "track";
                break;
            case PLAYLIST:
                searchType = "playlist";
                break;
        }
        return HttpRequest.get(String.format(SEARCH_QS_API, searchType, System.currentTimeMillis(), UrlUtil.encodeAll(keyword), (page - 1) * limit, limit));
    }
}

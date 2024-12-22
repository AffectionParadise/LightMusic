package net.doge.sdk.common;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import net.doge.sdk.common.builder.KugouReqBuilder;
import net.doge.sdk.common.builder.MiguReqBuilder;
import net.doge.sdk.common.builder.NeteaseReqBuilder;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

import java.util.Map;

public class SdkCommon {
    // 最大等待时间(ms)
    public static final int TIME_OUT = 6000;

    static {
        HttpRequest.setGlobalTimeout(TIME_OUT);
    }

    // 请求头
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60";
    // Cookie
    public static final String KG_COOKIE = "kg_mid=eb822d765480e1e3e6b8c6f2322019c8; kg_dfid=11TWHr0cp8jq4YKe9P3fosEo; kg_dfid_collect=d41d8cd98f00b204e9800998ecf8427e; musicwo17=kugou";
    public static final String HK_COOKIE = "BIDUPSID=BE696A0D51D343798228BD61F26D5647; PSTM=1658997928; BAIDUID=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; Hm_lvt_4aadd610dfd2f5972f1efee2653a2bc5=1659086001; BAIDUID_BFESS=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; delPer=0; PSINO=1; BA_HECTOR=20ag2g0ha42galagaga7ko601hgorth16; ZFY=gy2NQKWk6ZhA6AuDxoMpPQs6Og5GSSS7oA7XUkOHKeg:C; PC_TAB_LOG=video_details_page; COMMON_LID=2c19ef6811cbc39c8bbfaafcfcaeba64; BDRCVFR[fb3VbsUruOn]=I67x6TjHwwYf0; hkpcSearch=%u7FDF%u8000%24%24%24hello; H_PS_PSSID=36561_36461_36979_36885_37267_37135_26350_37205; ariaDefaultTheme=undefined; RT=\"z=1&dm=baidu.com&si=fxuvi7wxq45&ss=l7egnei6&sl=17&tt=19ph&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=1xs54&cl=1xt27&ul=1xt6x&hd=1xtb4\"";
    public static final String HF_COOKIE = "bbs_sid=8njuj0o2l21meohocgqj3s1lsk; c04498263b2624f231048f8aea520744=7715e2ac312b8351036f8633cd15e584";
    public static final String BI_COOKIE = "_uuid=9538DF8D-CA4D-28F5-C692-C5B4B312E44E14370infoc; buvid3=DF3C6017-C78A-23EC-7EE8-41090853ED2A14442infoc; b_nut=1695097614; home_feed_column=5; DedeUserID=381984701; DedeUserID__ckMd5=62c28371e08c50b3; CURRENT_FNVAL=4048; rpdid=|(um~Rkm|Jll0J'uYml|m~YJ); buvid_fp_plain=undefined; LIVE_BUVID=AUTO7816953009976873; buvid4=83DE81EC-A37B-1F1D-C096-DA73D7F3529D14442-023091912-wTMxKM%2BXYt%2BXSTf2UGduUA%3D%3D; hit-dyn-v2=1; enable_web_push=DISABLE; header_theme_version=CLOSE; FEED_LIVE_VERSION=V8; fingerprint=6dbfa8e3b4e9f3a86e469c16e7fc1f67; buvid_fp=6dbfa8e3b4e9f3a86e469c16e7fc1f67; browser_resolution=1920-967; SESSDATA=a3d3fad6%2C1739965348%2C5f35b%2A81CjCiSAZFqZeFEjmF3NhqbaYJ9Hh3mlf8g8Bo4TzTVmGNgw-yTwFq3eKlCw9OYESgEJYSVl9WM0l6YTFXM3paRW9RSF9tcGRvbGRpWDhkUmNHbzBLNnM5WnpDWjYwVzR3OG1QRTdWelRmWnpWUTJJTjVqWDhzbzBkaGRycUNQMS1JcEw2bUhhcGx3IIEC; bili_jct=a959bf174a665dd973733e8b41c52dbc";
    // 小丘
    public static final String QQ_MAIN_API = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    public static final String QQ_SEARCH_JSON = "{\"music.search.SearchCgiService\": {\"method\": \"DoSearchForQQMusicDesktop\",\"module\": \"music.search.SearchCgiService\",\"param\":{\"page_num\": %s,\"num_per_page\": %s,\"query\": \"%s\",\"search_type\": %s}}}";
    // 发姐
    public static final String FA_RADIO_API = "https://www.chatcyf.com/radio/";

    // 构造网易云音乐请求
    public static HttpRequest ncRequest(Method method, String url, String data, Map<NeteaseReqOptEnum, String> options) {
        return NeteaseReqBuilder.getInstance().buildRequest(method, url, data, options);
    }

    // 构造酷狗音乐请求
    public static HttpRequest kgRequest(Map<String, Object> params, String data, Map<KugouReqOptEnum, Object> options) {
        return KugouReqBuilder.getInstance().buildRequest(params, data, options);
    }

    // 构造酷我音乐请求
    public static HttpRequest kwRequest(String url) {
        return HttpRequest.get(url)
                .cookie("Hm_Iuvt_cdb524f42f23cer9b268564v7y735ewrq2324=w6nWhWQm4y2cTbFFcXi5Xxa3KtXKnjzS")
                .header("Secret", "5470ccb31c2e253cf173fea957bd5e544d0b4f6e54f88190868a0817094e920000224d1a")
                .header(Header.HOST, "kuwo.cn")
                .header(Header.REFERER, "https://kuwo.cn/");
    }

    // 构造咪咕音乐搜索请求
    public static HttpRequest mgSearchRequest(String type, String keyword, int page, int limit) {
        return MiguReqBuilder.getInstance().buildSearchRequest(type, keyword, page, limit);
    }

    // 构造千千音乐请求
    public static HttpRequest qiRequest(String url) {
        // 参数顺序按照字典顺序
        String secret = "0b50b02fd0d73a9c4c8c3a781c30845f";
        String params = url.substring(url.indexOf('?') + 1);
        // 将参数解码
        params = StringUtil.urlDecode(params);
        String sign = CryptoUtil.md5(params + secret);
        return HttpRequest.get(url + "&sign=" + sign);
    }
}

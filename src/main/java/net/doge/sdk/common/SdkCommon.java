package net.doge.sdk.common;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import net.doge.util.common.StringUtil;
import net.doge.util.common.CryptoUtil;

public class SdkCommon {
    // 最大等待时间(ms)
    public static final int TIME_OUT = 6000;

    static {
        HttpRequest.setGlobalTimeout(TIME_OUT);
    }

    // 请求头
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36 Edg/87.0.664.75";
    public static final String COOKIE = "kg_mid=eb822d765480e1e3e6b8c6f2322019c8; kg_dfid=11TWHr0cp8jq4YKe9P3fosEo; kg_dfid_collect=d41d8cd98f00b204e9800998ecf8427e; musicwo17=kugou";
    public static final String HK_COOKIE = "BIDUPSID=BE696A0D51D343798228BD61F26D5647; PSTM=1658997928; BAIDUID=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; Hm_lvt_4aadd610dfd2f5972f1efee2653a2bc5=1659086001; BAIDUID_BFESS=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; delPer=0; PSINO=1; BA_HECTOR=20ag2g0ha42galagaga7ko601hgorth16; ZFY=gy2NQKWk6ZhA6AuDxoMpPQs6Og5GSSS7oA7XUkOHKeg:C; PC_TAB_LOG=video_details_page; COMMON_LID=2c19ef6811cbc39c8bbfaafcfcaeba64; BDRCVFR[fb3VbsUruOn]=I67x6TjHwwYf0; hkpcSearch=%u7FDF%u8000%24%24%24hello; H_PS_PSSID=36561_36461_36979_36885_37267_37135_26350_37205; ariaDefaultTheme=undefined; RT=\"z=1&dm=baidu.com&si=fxuvi7wxq45&ss=l7egnei6&sl=17&tt=19ph&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=1xs54&cl=1xt27&ul=1xt6x&hd=1xtb4\"";
    public static final String HF_COOKIE = "bbs_sid=b49bvrs3efj99kui5ogaipp1gv";
    public static final String BI_COOKIE = "buvid3=F51EB532-B946-AA99-7E6B-3F0766C74A0129044infoc; i-wanna-go-back=-1; b_ut=5; CURRENT_PID=073ebfd0-eb4f-11ed-a6e3-637f2da26e5c; rpdid=|(um~RRuY|R)0J'uY)JkmY|u~; _uuid=7EB1D5C2-A8410-BD5D-B1110-2510576A922F948945infoc; PVID=1; nostalgia_conf=-1; hit-new-style-dyn=1; hit-dyn-v2=1; CURRENT_BLACKGAP=0; buvid_fp_plain=undefined; header_theme_version=CLOSE; CURRENT_FNVAL=4048; LIVE_BUVID=AUTO5916858943957456; fingerprint=59063f7bf1a66ec6fcf18dbd44b40de2; buvid_fp=8dee2d178a96c5f04ea99e061b587c02; CURRENT_QUALITY=0; b_nut=1688878222; bp_video_offset_381984701=816214846225776600; innersign=0; b_lsid=CECD36A6_18939707B67; sid=8sud6tli; FEED_LIVE_VERSION=V_LIVE_2; buvid4=0F6C5D17-B0F8-D451-14F1-3FDDFAB2E51A25016-023070914-wTMxKM%2BXYt%2FSW3zMLO3brQ%3D%3D; home_feed_column=4; browser_resolution=1103-961";

    // 域名
    public static final String HOST = "localhost";

    public static final String PREFIX = String.format("http://%s:3000", HOST);
    public static final String PREFIX_QQ = String.format("http://%s:3300", HOST);
    public static final String QQ_SEARCH_API = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    public static final String QQ_SEARCH_JSON = "{\"music.search.SearchCgiService\": {\"method\": \"DoSearchForQQMusicDesktop\",\"module\": \"music.search.SearchCgiService\",\"param\":{\"page_num\": %s,\"num_per_page\": %s,\"query\": \"%s\",\"search_type\": %s}}}";
    public static final String PREFIX_MG = String.format("http://%s:3400", HOST);

    // 构造酷我音乐请求
    public static HttpRequest kwRequest(String url) {
        return HttpRequest.get(url)
                .cookie("Hm_Iuvt_cdb524f42f0ce19b169b8072123a4727=h2APej2KpnG3SZpaFNRhhQGdkb2K3hXZ")
                .header("Secret", "366e3b3c9feedf42ce55aa73f7bce99c7bfbabe0d5bc7bef131e3e9a97d14f6102bdb130")
                .header(Header.HOST, "www.kuwo.cn")
                .header(Header.REFERER, "https://www.kuwo.cn/");
    }

    /**
     * 生成酷狗 url
     *
     * @return
     */
    public static String buildKgUrl(String s) {
        // 参数顺序按照 ASCII 码顺序
        String secret = "NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt";
        String params = s.substring(s.indexOf('?') + 1).replace("&", "");
        // 将参数解码
        params = StringUtil.urlDecode(params);
        String sign = CryptoUtil.hashMD5(secret + params + secret);
        return s + "&signature=" + sign;
    }

    /**
     * 生成千千音乐 url
     *
     * @return
     */
    public static String buildQianUrl(String s) {
        // 参数顺序按照 ASCII 码顺序
        String secret = "0b50b02fd0d73a9c4c8c3a781c30845f";
        String params = s.substring(s.indexOf('?') + 1);
        // 将参数解码
        params = StringUtil.urlDecode(params);
        String sign = CryptoUtil.hashMD5(params + secret);
        return s + "&sign=" + sign;
    }
}

package net.doge.sdk.common;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import net.doge.utils.StringUtil;

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
    public static final String BI_COOKIE = "buvid3=F51EB532-B946-AA99-7E6B-3F0766C74A0129044infoc; i-wanna-go-back=-1; b_ut=5; CURRENT_PID=073ebfd0-eb4f-11ed-a6e3-637f2da26e5c; rpdid=|(um~RRuY|R)0J'uY)JkmY|u~; _uuid=7EB1D5C2-A8410-BD5D-B1110-2510576A922F948945infoc; PVID=1; nostalgia_conf=-1; CURRENT_QUALITY=0; hit-new-style-dyn=1; hit-dyn-v2=1; b_nut=1683549091; CURRENT_BLACKGAP=0; CURRENT_FNVAL=4048; buvid_fp_plain=undefined; home_feed_column=5; fingerprint=14bce2332e84dd17597b30c7d6ed223d; browser_resolution=1920-961; buvid_fp=00d877eac672973524025e3d7f9a7502; sid=533w6lki; FEED_LIVE_VERSION=V8; header_theme_version=CLOSE; bp_video_offset_381984701=798561839388557300; b_lsid=ED381E96_1884408FEBB; innersign=1; buvid4=DF7111A0-432B-2B2D-EFE0-145ACFD72A0C51011-023050609-OERiKVBwTOo/uOaoDaaP2Q%3D%3D";

    // 域名
    public static final String host = "localhost";

    public static final String prefix = String.format("http://%s:3000", host);
    public static final String prefixQQ33 = String.format("http://%s:3300", host);
    public static final String qqSearchApi = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    public static final String qqSearchJson = "{\"music.search.SearchCgiService\": {\"method\": \"DoSearchForQQMusicDesktop\",\"module\": \"music.search.SearchCgiService\",\"param\":{\"page_num\": %s,\"num_per_page\": %s,\"query\": \"%s\",\"search_type\": %s}}}";
    public static final String prefixMg = String.format("http://%s:3400", host);

    // 构造酷我音乐请求
    public static HttpRequest kwRequest(String url) {
        return HttpRequest.get(url)
                .header(Header.COOKIE, "Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1623339177,1623339183; _ga=GA1.2.1195980605.1579367081" +
                        "; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1623339982; kw_token=3E7JFQ7MRPL; _gid=GA1.2.747985028.1623339179; _gat=1")
                .header("csrf", "3E7JFQ7MRPL")
                .header(Header.HOST, "www.kuwo.cn")
                .header(Header.REFERER, "https://www.kuwo.cn/")
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
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
        params = StringUtil.decode(params);
        String sign = StringUtil.toMD5(secret + params + secret);
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
        params = StringUtil.decode(params);
        String sign = StringUtil.toMD5(params + secret);
        return s + "&sign=" + sign;
    }
}

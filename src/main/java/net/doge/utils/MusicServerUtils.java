package net.doge.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.*;
import net.doge.constants.*;
import net.doge.models.*;
import net.doge.ui.components.LoadingPanel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/19
 */
public class MusicServerUtils {
    public static void main(String[] args) throws IOException {
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("appid", 1001);
//        map.put("clienttime", 1566798337219L);
//        map.put("clientver", 8275);
//        map.put("key", "f1f93580115bb106680d2375f8032d96");
//        map.put("mid", "21511157a05844bd085308bc76ef3343");
//        map.put("platform", "pc");
//        map.put("userid", "262643156");
//        map.put("return_min", 6);
//        map.put("return_max", 15);
//        HttpResponse resp = HttpRequest.post("http://everydayrec.service.kugou.com/guess_special_recommend")
//                .header(Header.USER_AGENT, "KuGou2012-8275-web_browser_event_handler")
//                .form(map)
//                .execute();
//        System.out.println(resp.body());
//        List<Map<String, String>> l = new LinkedList<>();
//        Map<String, String> im = new HashMap<>();
//        im.put("author_id", "3060");
//        l.add(im);
//        Map<String, Object> map = new HashMap<>();
//        map.put("clientver", "9108");
//        map.put("mid", "286974383886022203545511837994020015101");
//        map.put("clienttime", "1545746019");
//        map.put("key", "4c8b684568f03eeef985ae271561bcd8");
//        map.put("appid", "1005");
//        map.put("data", l);
//        System.out.println(map);
//        String artistInfoBody = HttpRequest.post(String.format(SIMILAR_ARTIST_KG_API))
//                .body("{\"clientver\":\"9108\",\"mid\":\"286974383886022203545511837994020015101\",\"clienttime\":\"1545746019\",\"key\":\"4c8b684568f03eeef985ae271561bcd8\",\"appid\":\"1005\",\"data\":[{\"author_id\":86747}]}")
//                .execute()
//                .body();
//        System.out.println(artistInfoBody);
        System.out.println(buildQianUrl("https://music.91q.com/v1/search?appid=16073360&timestamp=1659055580&type=1&word=2"));
        String body = HttpRequest.get(buildQianUrl("https://music.91q.com/v1/search?appid=16073360&timestamp=1659055580&type=1&word=2"))
                .execute()
                .body();
        System.out.println(body);
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
        params = StringUtils.decode(params);
        String sign = StringUtils.toMD5(secret + params + secret);
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
        params = StringUtils.decode(params);
        String sign = StringUtils.toMD5(params + secret);
        return s + "&sign=" + sign;
    }

    // 最大等待时间(ms)
    private static final int TIME_OUT = 6000;

    static {
        HttpRequest.setGlobalTimeout(TIME_OUT);
    }

    // 请求头
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36 Edg/87.0.664.75";
    private static final String COOKIE = "kg_mid=eb822d765480e1e3e6b8c6f2322019c8; kg_dfid=11TWHr0cp8jq4YKe9P3fosEo; kg_dfid_collect=d41d8cd98f00b204e9800998ecf8427e; musicwo17=kugou";
    private static final String HK_COOKIE = "BIDUPSID=BE696A0D51D343798228BD61F26D5647; PSTM=1658997928; BAIDUID=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; Hm_lvt_4aadd610dfd2f5972f1efee2653a2bc5=1659086001; BAIDUID_BFESS=41719925BFDA6FB8DAD817BC8CA07B28:SL=0:NR=10:FG=1; delPer=0; PSINO=1; BA_HECTOR=20ag2g0ha42galagaga7ko601hgorth16; ZFY=gy2NQKWk6ZhA6AuDxoMpPQs6Og5GSSS7oA7XUkOHKeg:C; PC_TAB_LOG=video_details_page; COMMON_LID=2c19ef6811cbc39c8bbfaafcfcaeba64; BDRCVFR[fb3VbsUruOn]=I67x6TjHwwYf0; hkpcSearch=%u7FDF%u8000%24%24%24hello; H_PS_PSSID=36561_36461_36979_36885_37267_37135_26350_37205; ariaDefaultTheme=undefined; RT=\"z=1&dm=baidu.com&si=fxuvi7wxq45&ss=l7egnei6&sl=17&tt=19ph&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=1xs54&cl=1xt27&ul=1xt6x&hd=1xtb4\"";

    // 域名
//    private static final String prefix = "http://musicapi.leanapp.cn";
//    private static final String prefix = "https://netease-cloud-music-api-phi-hazel.vercel.app";
    private static final String prefix = "http://localhost:3000";
    //    private static final String prefixQQ32 = "http://localhost:3200";
    private static final String prefixQQ33 = "http://localhost:3300";
    private static final String qqSearchApi = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    private static final String qqSearchJson = "{\"music.search.SearchCgiService\": {\"method\": \"DoSearchForQQMusicDesktop\",\"module\": \"music.search.SearchCgiService\",\"param\":{\"page_num\": %s,\"num_per_page\": %s,\"query\": \"%s\",\"search_type\": %s}}}";
    private static final String prefixKw = "http://localhost:7002";
    private static final String prefixMg = "http://localhost:3400";

    // 热搜 API
    private static final String HOT_SEARCH_API
            = prefix + "/search/hot";
    // 热搜 API (酷狗)
    private static final String HOT_SEARCH_KG_API
            = "http://gateway.kugou.com/api/v3/search/hot_tab?signature=ee44edb9d7155821412d220bcaf509dd&appid=1005&clientver=10026&plat=0";
    // 热搜 API (QQ)
    private static final String HOT_SEARCH_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    // 热搜 API (酷我)
    private static final String HOT_SEARCH_KW_API
            = "http://hotword.kuwo.cn/hotword.s?prod=kwplayer_ar_9.3.0.1&corp=kuwo&newver=2&vipver=9.3.0.1" +
            "&source=kwplayer_ar_9.3.0.1_40.apk&p2p=1&notrace=0&uid=0&plat=kwplayer_ar&rformat=json&encoding=utf8&tabid=1";
    // 热搜 API (咪咕)
    private static final String HOT_SEARCH_MG_API
            = "http://jadeite.migu.cn:7090/music_search/v2/search/hotword";

    // 搜索建议(简单) API
    private static final String SIMPLE_SEARCH_SUGGESTION_API
            = prefix + "/search/suggest?keywords=%s&type=mobile";
    // 搜索建议 API
    private static final String SEARCH_SUGGESTION_API
            = prefix + "/search/suggest?keywords=%s";
    // 搜索建议 API (酷狗)
    private static final String SEARCH_SUGGESTION_KG_API
            = "http://msearchcdn.kugou.com/new/app/i/search.php?cmd=302&keyword=%s";
    // 搜索建议 API (QQ)
    private static final String SEARCH_SUGGESTION_QQ_API
            = "https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?is_xml=0&format=json&key=%s" +
            "&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
    // 搜索建议 API (酷我)
    private static final String SEARCH_SUGGESTION_KW_API
            = prefixKw + "/kuwo/search/searchKey?key=%s";
    // 搜索建议 API (千千)
    private static final String SEARCH_SUGGESTION_QI_API
            = "https://music.91q.com/v1/search/sug?appid=16073360&timestamp=%s&type=&word=%s";

    // 搜索子标签 (猫耳)
    private static final String PROGRAM_SEARCH_TAG_ME_API
            = "https://www.missevan.com/sound/getcatalogleaves";

    /**
     * 加载节目搜索子标签
     *
     * @return
     */
    public static void initProgramSearchTag() {
        // 猫耳
        Tags.programSearchTag.put("默认", new String[]{""});

        final int c = 1;
        // 猫耳
        Runnable initProgramSearchTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PROGRAM_SEARCH_TAG_ME_API))
                    .execute()
                    .body();
            JSONArray tags = JSONArray.fromObject(playlistTagBody);
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject son = tags.getJSONObject(i).getJSONObject("son");
                Iterator<String> keys = son.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject obj = son.getJSONObject(key);

                    String name = obj.getString("catalog_name");
                    String id = obj.getString("id");

                    if (!Tags.programSearchTag.containsKey(name)) Tags.programSearchTag.put(name, new String[c]);
                    Tags.programSearchTag.get(name)[0] = id;
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initProgramSearchTagMe));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 关键词搜索歌曲 API
    private static final String SEARCH_MUSIC_API
            = prefix + "/cloudsearch?keywords=%s&limit=%s&offset=%s";
    // 关键词搜索声音 API
    private static final String SEARCH_VOICE_API
            = prefix + "/search?type=2000&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索歌曲 API (搜歌词)
    private static final String SEARCH_MUSIC_BY_LYRIC_API
            = prefix + "/search?type=1006&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索歌曲 API (酷狗)
    private static final String SEARCH_MUSIC_KG_API
            = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%s&page=%s&pagesize=%s&showtype=1";
    // 关键词搜索歌曲 API (搜歌词) (酷狗)
    private static final String SEARCH_MUSIC_BY_LYRIC_KG_API
            = "http://mobileservice.kugou.com/api/v3/lyric/search?keyword=%s&page=%s&pagesize=%s";
    // 关键词搜索歌曲 API (QQ 音乐)
//    private static final String SEARCH_MUSIC_QQ_API
//            = prefixQQ33 + "/search?key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌曲 API (搜歌词) (QQ 音乐)
    private static final String SEARCH_MUSIC_BY_LYRIC_QQ_API
            = prefixQQ33 + "/search?t=7&key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌曲 API (酷我)
    private static final String SEARCH_MUSIC_KW_API
            = prefixKw + "/kuwo/search/searchMusicBykeyWord?key=%s&pn=%s&rn=%s";
    // 关键词搜索歌曲 API (咪咕)
    private static final String SEARCH_MUSIC_MG_API
            = prefixMg + "/search?keyword=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌曲 API (千千)
    private static final String SEARCH_MUSIC_QI_API
            = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=1&word=%s";
    // 关键词搜索节目 API (喜马拉雅)
    private static final String SEARCH_MUSIC_XM_API
            = "https://www.ximalaya.com/revision/search/main?kw=%s&page=%s&spellchecker=true&condition=relation&rows=%s&device=iPhone&core=track&fq=&paidFilter=false";
    // 关键词搜索节目 API (猫耳)
    private static final String SEARCH_PROGRAM_ME_API
            = "https://www.missevan.com/sound/getsearch?cid=%s&s=%s&p=%s&type=3&page_size=%s";

    // 关键词搜索歌单 API
    private static final String SEARCH_PLAYLIST_API
            = prefix + "/cloudsearch?type=1000&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索歌单 API (酷狗)
    private static final String SEARCH_PLAYLIST_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/search/special?filter=0&keyword=%s&page=%s&pagesize=%s";
    // 关键词搜索歌单 API (QQ)
//    private static final String SEARCH_PLAYLIST_QQ_API
//            = prefixQQ33 + "/search?t=2&key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌单 API (酷我)
    private static final String SEARCH_PLAYLIST_KW_API
            = prefixKw + "/kuwo/search/searchPlayListBykeyWord?key=%s&pn=%s&rn=%s";
    // 关键词搜索歌单 API (咪咕)
    private static final String SEARCH_PLAYLIST_MG_API
            = prefixMg + "/search?type=playlist&keyword=%s&pageNo=%s&pageSize=%s";

    // 关键词搜索专辑 API
    private static final String SEARCH_ALBUM_API
            = prefix + "/cloudsearch?type=10&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索专辑 API (酷狗)
    private static final String SEARCH_ALBUM_KG_API
            = "http://msearch.kugou.com/api/v3/search/album?keyword=%s&page=%s&pagesize=%s";
    // 关键词搜索专辑 API (QQ)
//    private static final String SEARCH_ALBUM_QQ_API
//            = prefixQQ33 + "/search?t=8&key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索专辑 API (酷我)
    private static final String SEARCH_ALBUM_KW_API
            = prefixKw + "/kuwo/search/searchAlbumBykeyWord?key=%s&pn=%s&rn=%s";
    // 关键词搜索专辑 API (咪咕)
    private static final String SEARCH_ALBUM_MG_API
            = prefixMg + "/search?type=album&keyword=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索专辑 API (千千)
    private static final String SEARCH_ALBUM_QI_API
            = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=3&word=%s";
    // 关键词搜索专辑 API (豆瓣)
    private static final String SEARCH_ALBUM_DB_API
            = "https://www.douban.com/j/search?q=%s&start=%s&cat=1003";
    // 关键词搜索专辑 API (堆糖)
    private static final String SEARCH_ALBUM_DT_API
            = "https://www.duitang.com/napi/album/list/by_search/?include_fields=is_root,source_link,item,buyable,root_id,status,like_count,sender,album,cover" +
            "&kw=%s&start=%s&limit=%s&type=album&_type=&_=%s";

    // 关键词搜索歌手 API
    private static final String SEARCH_ARTIST_API
            = prefix + "/cloudsearch?type=100&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索歌手 API (QQ)
    private static final String SEARCH_ARTIST_QQ_API
            = prefixQQ33 + "/search?t=9&key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌手 API (酷我)
    private static final String SEARCH_ARTIST_KW_API
            = prefixKw + "/kuwo/search/searchArtistBykeyWord?key=%s&pn=%s&rn=%s";
    // 关键词搜索歌手 API (咪咕)
    private static final String SEARCH_ARTIST_MG_API
            = prefixMg + "/search?type=singer&keyword=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌手 API (千千)
    private static final String SEARCH_ARTIST_QI_API
            = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=2&word=%s";
    // 关键词搜索歌手 API (豆瓣)
    private static final String SEARCH_ARTIST_DB_API
            = "https://movie.douban.com/celebrities/search?search_text=%s&start=%s";

    // 关键词搜索电台 API
    private static final String SEARCH_RADIO_API
            = prefix + "/cloudsearch?type=1009&keywords=%s&offset=%s&limit=%s";
    // 关键词搜索电台 API(喜马拉雅)
    private static final String SEARCH_RADIO_XM_API
            = "https://www.ximalaya.com/revision/search/main?core=album&kw=%s&page=%s&spellchecker=true&rows=%s&condition=relation&device=iPhone&fq=&paidFilter=false";
    // 关键词搜索电台 API(猫耳)
    private static final String SEARCH_RADIO_ME_API
            = "https://www.missevan.com/dramaapi/search?s=%s&page=%s&page_size=%s";
    // 关键词搜索电台 API(豆瓣)
    private static final String SEARCH_RADIO_DB_API
            = "https://www.douban.com/j/search?q=%s&start=%s&cat=1002";
    // 关键词搜索图书电台 API(豆瓣)
    private static final String SEARCH_BOOK_RADIO_DB_API
            = "https://www.douban.com/j/search?q=%s&start=%s&cat=1001";

    // 关键词搜索 MV API
    private static final String SEARCH_MV_API
            = prefix + "/cloudsearch?type=1004&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索视频 API
    private static final String SEARCH_VIDEO_API
            = prefix + "/cloudsearch?type=1014&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索 MV API (酷狗)
    private static final String SEARCH_MV_KG_API
            = "http://msearch.kugou.com/api/v3/search/mv?version=9108&keyword=%s&page=%s&pagesize=%s&sver=2";
    // 关键词搜索 MV API (QQ)
    private static final String SEARCH_MV_QQ_API
            = prefixQQ33 + "/search?t=12&key=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索 MV API (酷我)
    private static final String SEARCH_MV_KW_API
            = prefixKw + "/kuwo/search/searchMvBykeyWord?key=%s&pn=%s&rn=%s";
    // 关键词搜索 MV API (好看)
    private static final String SEARCH_MV_HK_API
            = "https://haokan.baidu.com/web/search/api?query=%s&pn=%s&rn=%s&type=video";

    // 获取榜单 API
    private static final String GET_RANKING_API
            = prefix + "/toplist";
    // 获取榜单 API (酷狗)
    private static final String GET_RANKING_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/list?apiver=6&area_code=1";
    // 获取榜单 API (QQ)
    private static final String GET_RANKING_QQ_API
            = prefixQQ33 + "/top/category";
    // 获取榜单 API 2 (QQ)
    private static final String GET_RANKING_QQ_API_2
            = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=1928093487&inCharset=utf-8&outCharset=utf-8&notice=0&format=json&uin=0&needNewCode=1&platform=h5";
    // 获取榜单 API (酷我)
    private static final String GET_RANKING_KW_API
            = prefixKw + "/kuwo/rank";
    // 获取榜单 API 2 (酷我)
    private static final String GET_RANKING_KW_API_2
            = "http://qukudata.kuwo.cn/q.k?op=query&cont=tree&node=2&pn=0&rn=1000&fmt=json&level=2";
    // 获取榜单 API (咪咕)
    private static final String GET_RANKING_MG_API
            = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/rank-list";

    // 关键词搜索用户 API
    private static final String SEARCH_USER_API
            = prefix + "/cloudsearch?type=1002&keywords=%s&offset=%s&limit=%s";
    // 关键词搜索用户 API (喜马拉雅)
    private static final String SEARCH_USER_XM_API
            = "https://www.ximalaya.com/revision/search/main?kw=%s&page=%s&spellchecker=true&condition=relation&rows=%s&core=user&device=iPhone";
    // 关键词搜索用户 API (猫耳)
    private static final String SEARCH_USER_ME_API
            = "https://www.missevan.com/sound/getsearch?s=%s&type=1&p=%s&page_size=%s";
    // 关键词搜索用户 API (豆瓣)
    private static final String SEARCH_USER_DB_API
            = "https://www.douban.com/j/search?q=%s&start=%s&cat=1005";
    // 关键词搜索用户 API (堆糖)
    private static final String SEARCH_USER_DT_API
            = "https://www.duitang.com/napi/people/list/by_search/?kw=%s&start=%s&limit=%s&type=people&_type=&_=%s";

    // 获取评论 API
    private static final String NEW_GET_COMMENTS_API = prefix + "/comment/new?type=%s&id=%s&pageNo=%s&pageSize=%s&sortType=3";
    private static final String OLD_GET_COMMENTS_API = prefix + "/comment/%s?id=%s&offset=%s&limit=%s";
    // 获取热门评论 API
    private static final String GET_HOT_COMMENTS_API
            = prefix + "/comment/hot?type=%s&id=%s&offset=%s&limit=%s";
    // 获取评论 API (酷狗)
    private static final String GET_COMMENTS_KG_API
            = "https://mcomment.kugou.com/index.php?r=commentsv2/getCommentWithLike" +
            "&code=fc4be23b4e972707f36b8a828a93ba8a&extdata=%s&p=%s&pagesize=%s&kugouid=&clienttoken=";
    // 获取评论 API (QQ)
    private static final String GET_COMMENTS_QQ_API
            = prefixQQ33 + "/comment?type=%s&biztype=%s&id=%s&pageNo=%s&pageSize=%s";
    // 获取热门评论 API (酷我)
    private static final String GET_HOT_COMMENTS_KW_API
            = prefixKw + "/kuwo/comment?digest=%s&sid=%s&type=get_rec_comment&page=%s&rows=%s";
    // 获取最新评论 API (酷我)
    private static final String GET_NEW_COMMENTS_KW_API
            = prefixKw + "/kuwo/comment?digest=%s&sid=%s&type=get_comment&page=%s&rows=%s";
    // 获取电台热门评论 API (喜马拉雅)
    private static final String GET_HOT_RADIO_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=content-score-desc&pageId=%s&pageSize=%s";
    // 获取电台最新评论 API (喜马拉雅)
    private static final String GET_NEW_RADIO_COMMENTS_XM_API
            = "https://mobile.ximalaya.com/album-comment-mobile/web/album/comment/list/query/1?albumId=%s&order=time-desc&pageId=%s&pageSize=%s";
    // 获取节目评论 API (喜马拉雅)
    private static final String GET_COMMENTS_XM_API
            = "https://www.ximalaya.com/revision/comment/queryComments?trackId=%s&page=%s&pageSize=%s";
    // 获取节目评论 API (猫耳)
    private static final String GET_COMMENTS_ME_API
            = "https://www.missevan.com/site/getcomment?type=%s&order=%s&eId=%s&p=%s&pagesize=%s";
    // 获取评论 API (好看)
    private static final String GET_COMMENTS_HK_API
            = "https://haokan.baidu.com/videoui/api/commentget?url_key=%s&pn=%s&rn=%s&child_rn=1";
    // 获取电台评论 API (豆瓣)
    private static final String GET_RADIO_COMMENTS_DB_API
            = "https://movie.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 获取图书电台评论 API (豆瓣)
    private static final String GET_BOOK_RADIO_COMMENTS_DB_API
            = "https://book.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";
    // 获取专辑评论 API (豆瓣)
    private static final String GET_ALBUM_COMMENTS_DB_API
            = "https://music.douban.com/subject/%s/comments/?sort=%s&start=%s&limit=%s&status=P";

    // 获取曲谱 API
    private static final String GET_SHEETS_API
            = prefix + "/sheet/list?id=%s";
    // 获取曲谱图片 API
    private static final String GET_SHEETS_IMG_API
            = prefix + "/sheet/preview?id=%s";

    // 获取专辑照片 API (堆糖)
    private static final String GET_ALBUMS_IMG_DT_API
            = "https://www.duitang.com/napi/vienna/blog/by_album/?album_id=%s&after_id=%s&limit=%s&_=%s";
    // 获取歌手照片 API (豆瓣)
    private static final String GET_ARTISTS_IMG_DB_API
            = "https://movie.douban.com/celebrity/%s/photos/?type=C&start=%s&sortby=like&size=a&subtype=a";
    // 获取电台照片 API (豆瓣)
    private static final String GET_RADIO_IMG_DB_API
            = "https://movie.douban.com/subject/%s/photos?type=S&start=%s&sortby=like&size=a&subtype=a";
    // 获取电台海报 API (豆瓣)
    private static final String GET_RADIO_POSTER_DB_API
            = "https://movie.douban.com/subject/%s/photos?type=R&start=%s&sortby=like&size=a&subtype=a";

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public static void initRecPlaylistTag() {
        // 酷狗 QQ
        Tags.recPlaylistTag.put("默认", new String[]{" ", "10000000", " "});

        final int c = 3;

        // 酷狗
        Runnable initRecPlaylistTagKg = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_KG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
            final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[0] = id;
                }
            }
        };

        // QQ
        Runnable initRecPlaylistTagQq = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_QQ_API)
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONObject("tags").getJSONObject("data").getJSONArray("v_group");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("v_item");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[1] = id;
                }
            }
        };

        // 猫耳
        Runnable initRecPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_ME_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tags = playlistTagJson.getJSONObject("info");
            final String[] cats = new String[]{"主题", "场景", "情感"};
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONArray(j);

                    String name = tagJsonArray.getString(1);
                    String id = tagJsonArray.getString(0);

                    if (!Tags.recPlaylistTag.containsKey(name)) Tags.recPlaylistTag.put(name, new String[c]);
                    Tags.recPlaylistTag.get(name)[2] = id;
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecPlaylistTagMe));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 推荐歌单 API
    private static final String RECOMMEND_PLAYLIST_API
            = prefix + "/personalized?limit=100";
    // 推荐歌单 API (每页固定 30 条)(酷狗)
    private static final String RECOMMEND_PLAYLIST_KG_API
            = "http://m.kugou.com/plist/index?json=true&page=%s";
    // 推荐分类歌单(推荐) API (酷狗)
    private static final String RECOMMEND_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=5&c=%s&p=%s";
    // 推荐分类歌单(最新) API (酷狗)
    private static final String NEW_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=7&c=%s&p=%s";
    // 每日推荐歌单 API (QQ)
    private static final String DAILY_RECOMMEND_PLAYLIST_QQ_API
            = prefixQQ33 + "/recommend/playlist/u";
    // 推荐歌单 API (QQ)
    private static final String RECOMMEND_PLAYLIST_QQ_API
            = prefixQQ33 + "/recommend/playlist?id=%s&pageNo=1&pageSize=120";
    // 推荐歌单(最新) API (QQ)
    private static final String NEW_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";
    // 推荐歌单 API (酷我)
    private static final String RECOMMEND_PLAYLIST_KW_API
            = prefixKw + "/kuwo/rec_gedan";
    // 推荐歌单(最新) API (酷我)
    private static final String NEW_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?loginUid=0&loginSid=0&appUid=76039576&&pn=%s&rn=%s&order=new";
    // 最新歌单 API (咪咕)
    private static final String NEW_PLAYLIST_MG_API
            = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127272&startIndex=%s";
    // 推荐歌单 API (千千)
    private static final String REC_PLAYLIST_QI_API
            = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 推荐歌单 API (猫耳)
    private static final String REC_PLAYLIST_ME_API
            = "https://www.missevan.com/site/homepage";
    // 分类歌单(最新) API (猫耳)
    private static final String NEW_PLAYLIST_ME_API
            = "https://www.missevan.com/explore/tagalbum?order=1&tid=%s&p=%s&pagesize=%s";

    // 精品歌单标签 API
    private static final String HIGH_QUALITY_PLAYLIST_TAG_API
            = prefix + "/playlist/highquality/tags";
    // 网友精选碟标签 API
    private static final String PICKED_PLAYLIST_TAG_API
            = prefix + "/playlist/catlist";
    // 歌单标签 API (酷狗)
    private static final String PLAYLIST_TAG_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_smarty=1";
    // 歌单标签 API (QQ)
    private static final String PLAYLIST_TAG_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8" +
            "&notice=0&platform=wk_v15.json&needNewCode=0&data=%7B%22tags%22%3A%7B%22method%22%3A%22get_all_categories" +
            "%22%2C%22param%22%3A%7B%22qq%22%3A%22%22%7D%2C%22module%22%3A%22playlist.PlaylistAllCategoriesServer%22%7D%7D";
    // 歌单标签 API (酷我)
    private static final String PLAYLIST_TAG_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList?cmd=rcm_keyword_playlist&user=0" +
            "&prod=kwplayer_pc_9.0.5.0&vipver=9.0.5.0&source=kwplayer_pc_9.0.5.0&loginUid=0&loginSid=0&appUid=76039576";
    // 歌单标签 API (咪咕)
    private static final String PLAYLIST_TAG_MG_API
            = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/musiclistplaza-taglist/release";
    // 歌单标签 API (千千)
    private static final String PLAYLIST_TAG_QI_API
            = "https://music.91q.com/v1/tracklist/category?appid=16073360&timestamp=%s";
    // 歌单标签 API (猫耳)
    private static final String PLAYLIST_TAG_ME_API
            = "https://www.missevan.com/malbum/recommand";
    // 探索歌单标签 API (猫耳)
    private static final String EXP_PLAYLIST_TAG_ME_API
            = "https://www.missevan.com/explore";

    /**
     * 加载歌单标签
     *
     * @return
     */
    public static void initPlaylistTag() {
        // 网易云精品歌单 网易云网友精选碟 酷狗 QQ 酷我 咪咕 千千 猫耳 猫耳探索
        Tags.playlistTag.put("默认", new String[]{"全部", "全部", " ", "10000000", "", "", " ", " ", ""});

        final int c = 9;
        // 网易云
        // 精品歌单标签
        Runnable initHighQualityPlaylistTag = () -> {
            String playlistTagBody = HttpRequest.get(String.format(HIGH_QUALITY_PLAYLIST_TAG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("tags");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[0] = StringUtils.encode(name);
            }
        };
        // 网友精选碟标签
        Runnable initPickedPlaylistTag = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PICKED_PLAYLIST_TAG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("sub");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[1] = StringUtils.encode(name);
            }
        };

        // 酷狗
        Runnable initPlaylistTagKg = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_KG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tagIds = playlistTagJson.getJSONObject("data").getJSONObject("tagids");
            final String[] cats = new String[]{"主题", "语种", "风格", "年代", "心情", "场景"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tagIds.getJSONObject(cats[i]).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[2] = id;
                }
            }
        };

        // QQ
        Runnable initPlaylistTagQq = () -> {
            String playlistTagBody = HttpRequest.get(PLAYLIST_TAG_QQ_API)
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONObject("tags").getJSONObject("data").getJSONArray("v_group");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("v_item");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[3] = id;
                }
            }
        };

        // 酷我
        Runnable initPlaylistTagKw = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_KW_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("data");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id") + " " + tagJson.getString("digest");

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[4] = id;
                }
            }
        };

        // 咪咕
        Runnable initPlaylistTagMg = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_MG_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("content");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONObject(j).getJSONArray("texts");

                    String name = tagJsonArray.getString(0);
                    String id = tagJsonArray.getString(1);

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[5] = id;
                }
            }
        };

        // 千千
        Runnable initPlaylistTagQi = () -> {
            String playlistTagBody = HttpRequest.get(buildQianUrl(String.format(PLAYLIST_TAG_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONArray tags = playlistTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONObject(i).getJSONArray("subCate");
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("categoryName");
                    String id = tagJson.getString("id");

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[6] = id;
                }
            }
        };

        // 猫耳
        Runnable initPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(PLAYLIST_TAG_ME_API))
                    .execute()
                    .body();
            JSONObject playlistTagJson = JSONObject.fromObject(playlistTagBody);
            JSONObject tags = playlistTagJson.getJSONObject("info");
            final String[] cats = new String[]{"主题", "场景", "情感"};
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONArray tagJsonArray = tagArray.getJSONArray(j);

                    String name = tagJsonArray.getString(1);
                    String id = tagJsonArray.getString(0);

                    if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                    Tags.playlistTag.get(name)[7] = id;
                }
            }
        };
        // 猫耳探索
        Runnable initExpPlaylistTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(EXP_PLAYLIST_TAG_ME_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);

            Elements tags = doc.select(".explore-tag");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element t = tags.get(i);

                String id = t.attr("data-tagid");
                String name = t.getElementsByTag("a").text().trim();

                if (!Tags.playlistTag.containsKey(name)) Tags.playlistTag.put(name, new String[c]);
                Tags.playlistTag.get(name)[8] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHighQualityPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPickedPlaylistTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(initPlaylistTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initExpPlaylistTagMe));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 精品歌单 API
    private static final String HIGH_QUALITY_PLAYLIST_API
            = prefix + "/top/playlist/highquality?cat=%s&limit=100";
    // 网友精选碟(最热) API
    private static final String HOT_PICKED_PLAYLIST_API
            = prefix + "/top/playlist?cat=%s&limit=%s&offset=%s";
    // 网友精选碟(最新) API
    private static final String NEW_PICKED_PLAYLIST_API
            = prefix + "/top/playlist?order=new&cat=%s&limit=%s&offset=%s";
    // 推荐分类歌单(最热) API (酷狗)
    private static final String CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=6&c=%s&p=%s";
    // 推荐分类歌单(热藏) API (酷狗)
    private static final String HOT_COLLECTED_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=3&c=%s&p=%s";
    // 推荐分类歌单(飙升) API (酷狗)
    private static final String UP_CAT_PLAYLIST_KG_API
            = "http://www2.kugou.kugou.com/yueku/v9/special/getSpecial?is_ajax=1&cdn=cdn&t=8&c=%s&p=%s";
    // 热门歌单 API (酷狗)
    private static final String HOT_PLAYLIST_KG_API
            = "http://mobilecdnbj.kugou.com/api/v5/special/recommend?recommend_expire=0&sign=52186982747e1404d426fa3f2a1e8ee4&plat=0&uid=0&version=9108&page=1&area_code=1&appid=1005&mid=286974383886022203545511837994020015101&_t=1545746286";
    // 分类歌单 API (QQ)
    private static final String CAT_PLAYLIST_QQ_API
            = "https://u.y.qq.com/cgi-bin/musicu.fcg?loginUin=0&hostUin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=wk_v15.json&needNewCode=0&data=";
    // 默认歌单(热门) API (酷我)
    private static final String DEFAULT_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList?loginUid=0&loginSid=0&appUid=76039576&&pn=%s&rn=%s&order=hot";
    // 分类歌单 API (酷我)
    private static final String CAT_PLAYLIST_KW_API
            = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList?loginUid=0&loginSid=0&appUid=76039576&id=%s&pn=%s&rn=%s";
    // 分类歌单 API 2 (酷我)
    private static final String CAT_PLAYLIST_KW_API_2
            = "http://mobileinterfaces.kuwo.cn/er.s?type=get_pc_qz_data&f=web&id=%s&prod=pc";
    // 推荐歌单(最热) API (咪咕)
    private static final String RECOMMEND_PLAYLIST_MG_API
            = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&columnId=15127315&startIndex=%s";
    // 分类歌单 API (咪咕)
    private static final String CAT_PLAYLIST_MG_API
            = "https://m.music.migu.cn/migu/remoting/playlist_bycolumnid_tag?playListType=2&type=1&tagId=%s&startIndex=%s";
    // 分类歌单 API (千千)
    private static final String CAT_PLAYLIST_QI_API
            = "https://music.91q.com/v1/tracklist/list?appid=16073360&pageNo=%s&pageSize=%s&subCateId=%s&timestamp=%s";
    // 分类歌单 API (猫耳)
    private static final String CAT_PLAYLIST_ME_API
            = "https://www.missevan.com/explore/tagalbum?order=0&tid=%s&p=%s&pagesize=%s";
    // 探索歌单 API (猫耳)
    private static final String EXP_PLAYLIST_ME_API
            = "https://www.missevan.com/explore/getAlbumFromTag/%s";

    // 飙升榜 API (酷狗)
    private static final String UP_MUSIC_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=6666&page=%s&pagesize=%s";
    // TOP500 API (酷狗)
    private static final String TOP500_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=8888&page=%s&pagesize=%s";
    // 流行指数榜 API (QQ)
    private static final String POPULAR_MUSIC_QQ_API
            = prefixQQ33 + "/top?id=4&pageNo=%s&pageSize=%s";
    // 热歌榜 API (QQ)
    private static final String HOT_MUSIC_QQ_API
            = prefixQQ33 + "/top?id=26&pageNo=%s&pageSize=%s";
    // 飙升榜 API (酷我)
    private static final String UP_MUSIC_KW_API
            = prefixKw + "/kuwo/rank/musicList?bangId=93&pn=%s&rn=%s";
    // 热歌榜 API (酷我)
    private static final String HOT_MUSIC_KW_API
            = prefixKw + "/kuwo/rank/musicList?bangId=16&pn=%s&rn=%s";
    // 尖叫热歌榜 API (咪咕)
    private static final String HOT_MUSIC_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=27186466";

    /**
     * 加载新歌标签
     *
     * @return
     */
    public static void initNewSongTag() {
        Tags.newSongTag.put("默认", new String[]{"0", "1", "0"});

        Tags.newSongTag.put("华语", new String[]{"7", "1", "0"});
        Tags.newSongTag.put("内地", new String[]{"", "", "1"});
        Tags.newSongTag.put("港台", new String[]{"", "", "2"});
        Tags.newSongTag.put("欧美", new String[]{"96", "2", "3"});
        Tags.newSongTag.put("韩国", new String[]{"16", "4", "4"});
        Tags.newSongTag.put("日本", new String[]{"8", "5", "5"});
        Tags.newSongTag.put("日韩", new String[]{"", "3", ""});
    }

    // 推荐新歌 API
    private static final String RECOMMEND_NEW_SONG_API
            = prefix + "/personalized/newsong?limit=100";
    // 新歌速递 API
    private static final String FAST_NEW_SONG_API = prefix + "/top/song?type=%s";
    // 推荐新歌(华语) API (酷狗)
    private static final String RECOMMEND_NEW_SONG_KG_API
            = "http://mobilecdnbj.kugou.com/api/v3/rank/newsong?version=9108&type=%s&page=%s&pagesize=%s";
    // 推荐新歌 API (QQ)
    private static final String RECOMMEND_NEW_SONG_QQ_API
            = prefixQQ33 + "/new/songs?type=%s";
    // 新歌榜 API (酷我)
    private static final String NEW_SONG_KW_API
            = prefixKw + "/kuwo/rank/musicList?bangId=16&pn=%s&rn=%s";
    // 推荐新歌 API (咪咕)
    private static final String RECOMMEND_NEW_SONG_MG_API
            = prefixMg + "/new/songs?pageNo=%s&pageSize=%s";
    // 推荐新歌 API (千千)
    private static final String RECOMMEND_NEW_SONG_QI_API
            = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";

    // 专辑标签 API (豆瓣)
    private static final String ALBUM_TAG_DB_API
            = "https://music.douban.com/tag/";

    /**
     * 加载新碟标签
     *
     * @return
     */
    public static void initNewAlbumTag() {
        Tags.newAlbumTag.put("默认", new String[]{"ALL", "", "1", ""});
        Tags.newAlbumTag.put("华语", new String[]{"ZH", "Z_H", "", ""});
        Tags.newAlbumTag.put("内地", new String[]{"", "", "1", ""});
        Tags.newAlbumTag.put("港台", new String[]{"", "", "2", ""});
        Tags.newAlbumTag.put("欧美", new String[]{"EA", "E_A", "3", ""});
        Tags.newAlbumTag.put("韩国", new String[]{"KR", "KR", "4", ""});
        Tags.newAlbumTag.put("日本", new String[]{"JP", "JP", "5", ""});
        Tags.newAlbumTag.put("其他", new String[]{"", "", "6", ""});

        final int c = 4;
        // 豆瓣
        // 分类专辑标签
        Runnable initAlbumTagDb = () -> {
            String albumTagBody = HttpRequest.get(String.format(ALBUM_TAG_DB_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumTagBody);
            Elements tags = doc.select("tbody tr td a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.text();

                if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
                Tags.newAlbumTag.get(name)[3] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initAlbumTagDb));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 新碟上架 API
    private static final String NEW_ALBUM_API
            = prefix + "/top/album?area=%s";
    // 新碟上架(热门) API
//    private static final String HOT_ALBUM_API
//            = prefix + "/top/album?type=hot&area=%s";
    // 全部新碟 API
    private static final String ALL_NEW_ALBUM_API
            = prefix + "/album/new?area=%s&offset=%s&limit=%s";
    // 最新专辑 API
    private static final String NEWEST_ALBUM_API
            = prefix + "/album/newest";
    // 数字新碟上架 API
    private static final String NEWEST_DI_ALBUM_API
            = prefix + "/album/list?limit=200";
    // 数字专辑语种风格馆 API
    private static final String LANG_DI_ALBUM_API
            = prefix + "/album/list/style?area=%s&limit=50";
    // 新碟推荐 API (QQ)
    private static final String NEW_ALBUM_QQ_API
            = prefixQQ33 + "/new/album?type=%s&num=100";
    // 新碟推荐 API (咪咕)
    private static final String NEW_ALBUM_MG_API
            = prefixMg + "/new/albums?pageNo=%s&pageSize=%s";
    // 新专辑榜 API (咪咕)
    private static final String NEW_ALBUM_RANKING_MG_API
            = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=23218151";
    // 秀动发行 API (千千)
    private static final String XD_ALBUM_QI_API
            = "https://music.91q.com/v1//album/xdpublish?appid=16073360&module_name=秀动发行&moreApi=v1%%2Falbum%%2Fxdpublish" +
            "&pageNo=%s&pageSize=%s&timestamp=%s&type=showstart";
    // 新专辑推荐 API (千千)
    private static final String NEW_ALBUM_QI_API
            = "https://music.91q.com/v1/album/list?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s";
    // Top 250 专辑 API (豆瓣)
    private static final String TOP_ALBUM_DB_API
            = "https://music.douban.com/top250?start=%s";
    // 分类专辑 API (豆瓣)
    private static final String CAT_ALBUM_DB_API
            = "https://music.douban.com/tag/%s?start=%s&type=T";

    // 歌手标签 API (QQ)
    private static final String ARTIST_TAG_QQ_API
            = prefixQQ33 + "/singer/category";

    /**
     * 加载歌手标签
     *
     * @return
     */
    public static void initArtistTag() {
        Tags.artistTag.put("默认", new String[]{"1", "", "0 0", "-100 -100 -100 -100", "11", "", "  "});

        Tags.artistTag.put("男", new String[]{"", "1 -1 -1", "1 0", "0 -100 -100 -100", "", "", "  男"});
        Tags.artistTag.put("女", new String[]{"", "2 -1 -1", "2 0", "1 -100 -100 -100", "", "", "  女"});
        Tags.artistTag.put("组合", new String[]{"", "3 -1 -1", "3 0", "2 -100 -100 -100", "16", "", "  组合"});
        Tags.artistTag.put("乐队", new String[]{"", "", "", "", "", "", "  乐队"});
        Tags.artistTag.put("华语", new String[]{"1", "-1 7 -1", "0 1", "", "11", "0", ""});
        Tags.artistTag.put("华语男", new String[]{"", "1 7 -1", "1 1", "", "", "1", ""});
        Tags.artistTag.put("华语女", new String[]{"", "2 7 -1", "2 1", "", "", "2", ""});
        Tags.artistTag.put("华语组合", new String[]{"", "3 7 -1", "3 1", "", "", "3", ""});
        Tags.artistTag.put("内地", new String[]{"", "", "", "-100 -100 -100 200", "", "", " 内地 "});
        Tags.artistTag.put("内地男", new String[]{"", "", "", "0 -100 -100 200", "", "", " 内地 男"});
        Tags.artistTag.put("内地女", new String[]{"", "", "", "1 -100 -100 200", "", "", " 内地 女"});
        Tags.artistTag.put("内地组合", new String[]{"", "", "", "2 -100 -100 200", "", "", " 内地 组合"});
        Tags.artistTag.put("内地乐队", new String[]{"", "", "", "", "", "", " 内地 乐队"});
        Tags.artistTag.put("港台", new String[]{"", "", "", "-100 -100 -100 2", "", "", " 港台 "});
        Tags.artistTag.put("港台男", new String[]{"", "", "", "0 -100 -100 2", "", "", " 港台 男"});
        Tags.artistTag.put("港台女", new String[]{"", "", "", "1 -100 -100 2", "", "", " 港台 女"});
        Tags.artistTag.put("港台组合", new String[]{"", "", "", "2 -100 -100 2", "", "", " 港台 组合"});
        Tags.artistTag.put("港台乐队", new String[]{"", "", "", "", "", "", " 港台 乐队"});
        Tags.artistTag.put("欧美", new String[]{"2", "-1 96 -1", "0 2", "-100 -100 -100 5", "13", "", " 欧美 "});
        Tags.artistTag.put("欧美男", new String[]{"", "1 96 -1", "1 2", "0 -100 -100 5", "", "7", " 欧美 男"});
        Tags.artistTag.put("欧美女", new String[]{"", "2 96 -1", "2 2", "1 -100 -100 5", "", "8", " 欧美 女"});
        Tags.artistTag.put("欧美组合", new String[]{"", "3 96 -1", "3 2", "2 -100 -100 5", "", "9", " 欧美 组合"});
        Tags.artistTag.put("欧美乐队", new String[]{"", "", "", "", "", "", " 欧美 乐队"});
        Tags.artistTag.put("韩国", new String[]{"3", "-1 16 -1", "0 6", "-100 -100 -100 3", "", "", " 韩国 "});
        Tags.artistTag.put("韩国男", new String[]{"", "1 16 -1", "1 6", "0 -100 -100 3", "", "", " 韩国 男"});
        Tags.artistTag.put("韩国女", new String[]{"", "2 16 -1", "2 6", "1 -100 -100 3", "", "", " 韩国 女"});
        Tags.artistTag.put("韩国组合", new String[]{"", "3 16 -1", "3 6", "2 -100 -100 3", "", "", " 韩国 组合"});
        Tags.artistTag.put("韩国乐队", new String[]{"", "", "", "", "", "", " 韩国 乐队"});
        Tags.artistTag.put("日本", new String[]{"4", "-1 8 -1", "0 5", "-100 -100 -100 4", "", "", " 日本 "});
        Tags.artistTag.put("日本男", new String[]{"", "1 8 -1", "1 5", "0 -100 -100 4", "", "", " 日本 男"});
        Tags.artistTag.put("日本女", new String[]{"", "2 8 -1", "2 5", "1 -100 -100 4", "", "", " 日本 女"});
        Tags.artistTag.put("日本组合", new String[]{"", "3 8 -1", "3 5", "2 -100 -100 4", "", "", " 日本 组合"});
        Tags.artistTag.put("日本乐队", new String[]{"", "", "", "", "", "", " 日本 乐队"});
        Tags.artistTag.put("日韩", new String[]{"", "", "0 3", "", "12", "", ""});
        Tags.artistTag.put("日韩男", new String[]{"", "", "1 3", "", "", "4", ""});
        Tags.artistTag.put("日韩女", new String[]{"", "", "2 3", "", "", "5", ""});
        Tags.artistTag.put("日韩组合", new String[]{"", "", "3 3", "", "", "6", ""});
        Tags.artistTag.put("其他", new String[]{"", "-1 0 -1", "0 4", "-100 -100 -100 6", "", "10", " 其他 "});
        Tags.artistTag.put("其他男", new String[]{"", "1 0 -1", "1 4", "0 -100 -100 6", "", "", " 其他 男"});
        Tags.artistTag.put("其他女", new String[]{"", "2 0 -1", "2 4", "1 -100 -100 6", "", "", " 其他 女"});
        Tags.artistTag.put("其他组合", new String[]{"", "3 0 -1", "3 4", "2 -100 -100 6", "", "", " 其他 组合"});
        Tags.artistTag.put("其他乐队", new String[]{"", "", "", "", "", "", " 其他 乐队"});

        final int c = 7;
        // QQ + 网易云 + 千千
        // 分类歌手标签
        Runnable initArtistTagQq = () -> {
            String artistTagBody = HttpRequest.get(String.format(ARTIST_TAG_QQ_API))
                    .execute()
                    .body();
            JSONObject artistTagJson = JSONObject.fromObject(artistTagBody);
            JSONObject data = artistTagJson.getJSONObject("data");
            // 流派
            JSONArray genre = data.getJSONArray("genre");
            for (int i = 0, len = genre.size(); i < len; i++) {
                JSONObject tagJson = genre.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("全部".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[3] = String.format("-100 %s -100 -100", id);
            }
            // 首字母
            JSONArray index = data.getJSONArray("index");
            for (int i = 0, len = index.size(); i < len; i++) {
                JSONObject tagJson = index.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("热门".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.artistTag.containsKey(name)) Tags.artistTag.put(name, new String[c]);
                Tags.artistTag.get(name)[3] = String.format("-100 -100 %s -100", id);

                // 网易云
                Tags.artistTag.get(name)[1] = String.format("-1 -1 %s", "#".equals(name) ? "0" : name);

                // 千千
                Tags.artistTag.get(name)[6] = String.format("%s  ", "#".equals(name) ? "other" : name);
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initArtistTagQq));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 歌手榜 API
    private static final String ARTIST_RANKING_LIST_API = prefix + "/toplist/artist?type=%s";
    // 热门歌手 API
    private static final String HOT_ARTIST_LIST_API = prefix + "/top/artists?offset=%s&limit=%s";
    // 分类歌手 API
    private static final String CAT_ARTIST_API = prefix + "/artist/list?type=%s&area=%s&initial=%s&offset=%s&limit=%s";
    // 热门歌手推荐 API (酷狗)
    private static final String HOT_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=1&page=%s&pagesize=%s";
    // 飙升歌手推荐 API (酷狗)
    private static final String UP_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=2&page=%s&pagesize=%s";
    // 推荐歌手 API (QQ)
    private static final String ARTIST_LIST_QQ_API = prefixQQ33 + "/singer/list?sex=%s&genre=%s&index=%s&area=%s&pageNo=%s";
    // 歌手推荐 API (酷我)
    private static final String ARTIST_LIST_KW_API = prefixKw + "/kuwo/rec_singer?category=%s&pn=%s&rn=%s";
    // 全部歌手 API (酷我)
    private static final String ALL_ARTISTS_LIST_KW_API = prefixKw + "/kuwo/singer?category=%s&pn=%s&rn=%s";
    // 来电新声榜 API (咪咕)
    private static final String ARTIST_LIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425062";
    // 来电唱作榜 API (咪咕)
    private static final String ARTIST_LIST_MG_API_2 = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425072";
    // 推荐歌手 API (千千)
    private static final String REC_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 分类歌手 API (千千)
    private static final String CAT_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/artist/list?appid=16073360&" +
            "artistFristLetter=%s&artistGender=%s&artistRegion=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    // 新晋电台 API
    private static final String NEW_RADIO_API
            = prefix + "/dj/toplist?type=new&limit=200";
    // 推荐个性电台 API
    private static final String PERSONALIZED_RADIO_API
            = prefix + "/personalized/djprogram";
    // 推荐电台 API
    private static final String RECOMMEND_RADIO_API
            = prefix + "/dj/recommend";
    // 付费精品电台 API
    private static final String PAY_RADIO_API
            = prefix + "/dj/toplist/pay?limit=100";
    // 付费精选电台 API
    private static final String PAY_GIFT_RADIO_API
            = prefix + "/dj/paygift?offset=%s&limit=%s";
    // 推荐电台 API (QQ)
    private static final String RECOMMEND_RADIO_QQ_API
            = prefixQQ33 + "/radio/category";
    // 推荐广播剧 API (猫耳)
    private static final String REC_RADIO_ME_API
            = "https://www.missevan.com/drama/site/recommend";
    // 夏日推荐 API (猫耳)
    private static final String SUMMER_RADIO_ME_API
            = "https://www.missevan.com/dramaapi/summerdrama";


    // 分类热门电台标签 API
    private static final String HOT_RADIO_TAG_API
            = prefix + "/dj/category/recommend";
    // 分类推荐电台标签 API
    private static final String REC_RADIO_TAG_API
            = prefix + "/dj/catelist";
    // 电台分类标签 API (喜马拉雅)
    private static final String RADIO_TAG_XM_API
            = "https://www.ximalaya.com/revision/category/allCategoryInfo";
    // 排行榜标签 API (喜马拉雅)
    private static final String RADIO_RANKING_TAG_XM_API
            = "https://www.ximalaya.com/revision/rank/v3/cluster";
    // 广播剧标签 API (猫耳)
    private static final String RADIO_TAG_ME_API
            = "https://www.missevan.com/dramaapi/tag";
    // 电台标签 API (豆瓣)
    private static final String RADIO_TAG_DB_API
            = "https://movie.douban.com/chart";

    /**
     * 加载电台标签
     *
     * @return
     */
    public static void initRadioTag() {
        Tags.radioTag.put("默认", new String[]{"", "", "", "", "", "0 0 0", ""});

        // 喜马拉雅频道
        Tags.radioTag.put("小说", new String[]{"", "", "", "", "7", "", ""});
        Tags.radioTag.put("儿童", new String[]{"", "", "", "", "11", "", ""});
        Tags.radioTag.put("相声小品", new String[]{"", "", "", "", "9", "", ""});
        Tags.radioTag.put("评书", new String[]{"", "", "", "", "10", "", ""});
        Tags.radioTag.put("娱乐", new String[]{"", "", "", "", "13", "", ""});
        Tags.radioTag.put("悬疑", new String[]{"", "", "", "", "14", "", ""});
        Tags.radioTag.put("人文", new String[]{"", "", "", "", "17", "", ""});
        Tags.radioTag.put("国学", new String[]{"", "", "", "", "18", "", ""});
        Tags.radioTag.put("头条", new String[]{"", "", "", "", "24", "", ""});
        Tags.radioTag.put("音乐", new String[]{"", "", "", "", "19", "", ""});
        Tags.radioTag.put("历史", new String[]{"", "", "", "", "16", "", ""});
        Tags.radioTag.put("情感", new String[]{"", "", "", "", "20", "", ""});
        Tags.radioTag.put("投资理财", new String[]{"", "", "", "", "26", "", ""});
        Tags.radioTag.put("个人提升", new String[]{"", "", "", "", "31", "", ""});
        Tags.radioTag.put("健康", new String[]{"", "", "", "", "22", "", ""});
        Tags.radioTag.put("生活", new String[]{"", "", "", "", "21", "", ""});
        Tags.radioTag.put("影视", new String[]{"", "", "", "", "15", "", ""});
        Tags.radioTag.put("商业管理", new String[]{"", "", "", "", "27", "", ""});
        Tags.radioTag.put("英语", new String[]{"", "", "", "", "29", "", ""});
        Tags.radioTag.put("少儿素养", new String[]{"", "", "", "", "12", "", ""});
        Tags.radioTag.put("科技", new String[]{"", "", "", "", "28", "", ""});
        Tags.radioTag.put("教育考试", new String[]{"", "", "", "", "32", "", ""});
        Tags.radioTag.put("体育", new String[]{"", "", "", "", "25", "", ""});
        Tags.radioTag.put("小语种", new String[]{"", "", "", "", "30", "", ""});
        Tags.radioTag.put("广播剧", new String[]{"", "", "", "", "8", "", ""});
        Tags.radioTag.put("汽车", new String[]{"", "", "", "", "23", "", ""});

        final int c = 7;
        // 网易云
        // 分类热门电台标签
        Runnable initHotRadioTag = () -> {
            String radioTagBody = HttpRequest.get(String.format(HOT_RADIO_TAG_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("categoryName");
                String id = tagJson.getString("categoryId");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[0] = id;
            }
        };
        // 分类推荐电台标签
        Runnable initRecRadioTag = () -> {
            String radioTagBody = HttpRequest.get(String.format(REC_RADIO_TAG_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONArray("categories");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                String id = tagJson.getString("id");

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[1] = id;
            }
        };

        // 喜马拉雅
        // 电台分类标签
        Runnable initRadioTagXm = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_XM_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray fTags = radioTagJson.getJSONArray("data");
            for (int i = 0, len = fTags.size(); i < len; i++) {
                JSONArray sTags = fTags.getJSONObject(i).getJSONArray("categories");
                for (int j = 0, s = sTags.size(); j < s; j++) {
                    JSONObject tagJson = sTags.getJSONObject(j);

                    String name = tagJson.getString("displayName");
                    String pinyin = tagJson.getString("pinyin");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    Tags.radioTag.get(name)[3] = pinyin;
                }
            }
        };
        // 排行榜标签
        Runnable initRankingTagXm = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_RANKING_TAG_XM_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray fTags = radioTagJson.getJSONObject("data").getJSONArray("clusterType");
            for (int i = 0, len = fTags.size(); i < len; i++) {
                JSONObject sJson = fTags.getJSONObject(i);
                if (sJson.getInt("rankType") != 2) continue;
                String n = sJson.getString("rankClusterTypeTitle") + " - ";
                String t = sJson.getString("rankClusterTypeId") + " ";
                JSONArray sTags = sJson.getJSONArray("rankClusterCategories");
                for (int j = 0, s = sTags.size(); j < s; j++) {
                    JSONObject tagJson = sTags.getJSONObject(j);

                    String name = n + tagJson.getString("rankClusterTitle");
                    String id = t + tagJson.getString("rankClusterId");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    Tags.radioTag.get(name)[2] = id;
                }
            }
        };

        // 猫耳
        // 广播剧标签
        Runnable initRadioTagMe = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_ME_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONObject tags = radioTagJson.getJSONObject("info");
            final String[] cats = new String[]{"integrity", "age", "tags"};
            for (int i = 0, len = cats.length; i < len; i++) {
                JSONArray tagArray = tags.getJSONArray(cats[i]);
                for (int j = 0, s = tagArray.size(); j < s; j++) {
                    JSONObject tagJson = tagArray.getJSONObject(j);

                    String name = tagJson.getString("name");
                    String id = tagJson.getString("id");

                    if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                    if (i == 0) id = String.format("%s 0 0", id);
                    else if (i == 1) id = String.format("0 %s 0", id);
                    else id = String.format("0 0 %s", id);
                    Tags.radioTag.get(name)[5] = id;
                }
            }
        };

        // 豆瓣
        // 分类电台标签
        Runnable initRadioTagDb = () -> {
            String radioTagBody = HttpRequest.get(String.format(RADIO_TAG_DB_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioTagBody);
            Elements tags = doc.select("div.types span a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = ReUtil.get("type=(\\d+)", tag.attr("href"), 1);

                if (!Tags.radioTag.containsKey(name)) Tags.radioTag.put(name, new String[c]);
                Tags.radioTag.get(name)[6] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHotRadioTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRecRadioTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRankingTagXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initRadioTagDb));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    //    // 个性电台推荐 API
//    private static final String PERSONAL_RADIO_API
//            = prefix + "/dj/personalize/recommend";
    // 今日优选电台 API
    private static final String DAILY_RADIO_API
            = prefix + "/dj/today/perfered";
    // 热门电台 API
    private static final String HOT_RADIO_API
            = prefix + "/dj/hot?limit=1000";
    // 热门电台榜 API
    private static final String RADIO_TOPLIST_API
            = prefix + "/dj/toplist?type=hot&limit=200";
    // 推荐电台 API
    private static final String REC_RADIO_API
            = prefix + "/dj/recommend";
    // 分类热门电台 API
    private static final String CAT_HOT_RADIO_API
            = prefix + "/dj/radio/hot?cateId=%s&offset=%s&limit=%s";
    // 分类推荐电台 API
    private static final String CAT_REC_RADIO_API
            = prefix + "/dj/recommend/type?type=%s";
    // 分类电台榜 API (喜马拉雅)
    private static final String CAT_RADIO_RANKING_XM_API
            = "https://www.ximalaya.com/revision/rank/v3/element?typeId=%s&clusterId=%s";
    // 分类电台 API (喜马拉雅)
    private static final String CAT_RADIO_XM_API
            = "https://www.ximalaya.com/revision/category/queryCategoryPageAlbums?category=%s&subcategory=&meta=&sort=0&page=%s&perPage=%s&useCache=false";
    // 频道电台 API (喜马拉雅)
    private static final String CHANNEL_RADIO_XM_API
            = "https://www.ximalaya.com/revision/metadata/v2/channel/albums?groupId=%s&pageNum=%s&pageSize=%s&sort=1&metadata=";
    // 周榜电台 API (猫耳)
    private static final String WEEK_RADIO_ME_API
            = "https://www.missevan.com/reward/drama-reward-rank?period=1&page=%s&page_size=%s";
    // 月榜电台 API (猫耳)
    private static final String MONTH_RADIO_ME_API
            = "https://www.missevan.com/reward/drama-reward-rank?period=2&page=%s&page_size=%s";
    // 总榜电台 API (猫耳)
    private static final String ALL_TIME_RADIO_ME_API
            = "https://www.missevan.com/reward/drama-reward-rank?period=3&page=%s&page_size=%s";
    // 广播剧分类电台 API (猫耳)
    private static final String CAT_RADIO_ME_API
            = "https://www.missevan.com/dramaapi/filter?filters=%s_0_%s_%s_0&page=%s&order=1&page_size=%s";
    // Top 250 电台 API (豆瓣)
    private static final String TOP_RADIO_DB_API
            = "https://movie.douban.com/top250?start=%s&filter=";
    // 分类电台 API (豆瓣)
    private static final String CAT_RADIO_DB_API
            = "https://movie.douban.com/j/chart/top_list?type=%s&interval_id=100:90&action=&start=%s&limit=%s";
    // 分类电台总数 API (豆瓣)
    private static final String CAT_RADIO_TOTAL_DB_API
            = "https://movie.douban.com/j/chart/top_list_count?type=%s&interval_id=100:90";

    // 探索节目标签 API (猫耳)
    private static final String EXP_PROGRAM_TAG_ME_API
            = "https://www.missevan.com/explore";
    // 首页子标签 API (猫耳)
    private static final String PROGRAM_SUB_TAG_ME_API
            = "https://www.missevan.com";

    /**
     * 加载节目标签
     *
     * @return
     */
    public static void initProgramTag() {
        Tags.programTag.put("默认", new String[]{"", ""});

        final int c = 2;
        // 猫耳
        // 猫耳探索
        Runnable initExpProgramTagMe = () -> {
            String playlistTagBody = HttpRequest.get(String.format(EXP_PROGRAM_TAG_ME_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(playlistTagBody);

            Elements tags = doc.select(".explore-tag");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element t = tags.get(i);

                String id = t.attr("data-tagid");
                String name = t.getElementsByTag("a").text().trim();

                if (!Tags.programTag.containsKey(name)) Tags.programTag.put(name, new String[c]);
                Tags.programTag.get(name)[0] = id;
            }
        };
        // 首页标签
        Runnable initProgramIndexTagMe = () -> {
            String radioTagBody = HttpRequest.get(String.format(PROGRAM_SUB_TAG_ME_API))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioTagBody);

            // 大标签
            Elements tags = doc.select(".vw-topcatalog-item.fc-topcatalog-item a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.attr("title");
                String href = tag.attr("href");
                // 排除广播剧标签
                if (href.contains("drama")) continue;
                String id = href.replaceFirst("/sound/m/", "");

                if (!Tags.programTag.containsKey(name)) Tags.programTag.put(name, new String[c]);
                Tags.programTag.get(name)[1] = id;
            }

            // 子标签
            Elements subTags = doc.select(".vw-topcatalog-subitem-container.fc-topcatalog-subitem-container a");
            for (int i = 0, len = subTags.size(); i < len; i++) {
                Element tag = subTags.get(i);

                String name = tag.attr("title");
                String id = tag.attr("href").replaceFirst("/sound/m/", "");

                if (!Tags.programTag.containsKey(name)) Tags.programTag.put(name, new String[c]);
                Tags.programTag.get(name)[1] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initExpProgramTagMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(initProgramIndexTagMe));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // 推荐节目 API
    private static final String RECOMMEND_PROGRAM_API
            = prefix + "/program/recommend";
    // 推荐个性节目 API
    private static final String PERSONALIZED_PROGRAM_API
            = prefix + "/personalized/djprogram";
    // 24 小时节目榜 API
    private static final String PROGRAM_24_HOURS_TOPLIST_API
            = prefix + "/dj/program/toplist/hours";
    // 节目榜 API
    private static final String PROGRAM_TOPLIST_API
            = prefix + "/dj/program/toplist?limit=200";
    // 推荐节目 API (猫耳)
    private static final String REC_PROGRAM_ME_API
            = "https://www.missevan.com/site/homepage";
    // 探索节目 API (猫耳)
    private static final String EXP_PROGRAM_ME_API
            = "https://www.missevan.com/explore/%s?p=%s&pagesize=%s";
    // 首页分类节目 API (猫耳)
    private static final String INDEX_CAT_PROGRAM_ME_API
            = "https://www.missevan.com/sound/m?order=1&id=%s&p=%s&pagesize=%s";
    // 首页分类节目 API (最新)(猫耳)
    private static final String INDEX_CAT_NEW_PROGRAM_ME_API
            = "https://www.missevan.com/sound/m?order=0&id=%s&p=%s&pagesize=%s";

    // MV 标签 API (KG)
    private static final String MV_TAG_KG_API
            = "http://mobileservice.kugou.com/api/v5/video/recommend_channel?version=9108&type=2";
    // MV 标签 API (QQ)
    private static final String MV_TAG_QQ_API
            = prefixQQ33 + "/mv/category";

    /**
     * 加载 MV 标签
     *
     * @return
     */
    public static void initMvTag() {
        Tags.mvTag.put("默认", new String[]{"全部", "全部", "0", "15", "7", "0", ""});

        Tags.mvTag.put("精选", new String[]{"", "", "", "", "", "0", ""});
        Tags.mvTag.put("内地", new String[]{"内地", "全部", "", "16", "7", "1", ""});
        Tags.mvTag.put("港台", new String[]{"港台", "全部", "", "17", "7", "2", ""});
        Tags.mvTag.put("欧美", new String[]{"欧美", "全部", "", "18", "7", "3", ""});
        Tags.mvTag.put("韩国", new String[]{"韩国", "全部", "", "19", "7", "4", ""});
        Tags.mvTag.put("日本", new String[]{"日本", "全部", "", "20", "7", "5", ""});
        Tags.mvTag.put("官方版", new String[]{"", "官方版", "", "", "", "", ""});
//        Tags.mvTag.put("原生", new String[]{"", "原生", "", "", "", "", ""});
        Tags.mvTag.put("现场版", new String[]{"", "现场版", "", "", "", "", ""});
        Tags.mvTag.put("网易出品", new String[]{"", "网易出品", "", "", "", "", ""});

        // 好看
        Tags.mvTag.put("影视", new String[]{"", "", "", "", "", "", "yingshi_new"});
        Tags.mvTag.put("音乐", new String[]{"", "", "", "", "", "", "yinyue_new"});
        Tags.mvTag.put("VLOG", new String[]{"", "", "", "", "", "", "yunying_vlog"});
        Tags.mvTag.put("游戏", new String[]{"", "", "", "", "", "", "youxi_new"});
        Tags.mvTag.put("搞笑", new String[]{"", "", "", "", "", "", "gaoxiao_new"});
        Tags.mvTag.put("综艺", new String[]{"", "", "", "", "", "", "zongyi_new"});
        Tags.mvTag.put("娱乐", new String[]{"", "", "", "", "", "", "yule_new"});
        Tags.mvTag.put("动漫", new String[]{"", "", "", "", "", "", "dongman_new"});
        Tags.mvTag.put("生活", new String[]{"", "", "", "", "", "", "shenghuo_new"});
        Tags.mvTag.put("广场舞", new String[]{"", "", "", "", "", "", "guangchuangwu_new"});
        Tags.mvTag.put("美食", new String[]{"", "", "", "", "", "", "meishi_new"});
        Tags.mvTag.put("宠物", new String[]{"", "", "", "", "", "", "chongwu_new"});
        Tags.mvTag.put("三农", new String[]{"", "", "", "", "", "", "sannong_new"});
        Tags.mvTag.put("军事", new String[]{"", "", "", "", "", "", "junshi_new"});
        Tags.mvTag.put("社会", new String[]{"", "", "", "", "", "", "shehui_new"});
        Tags.mvTag.put("体育", new String[]{"", "", "", "", "", "", "tiyu_new"});
        Tags.mvTag.put("科技", new String[]{"", "", "", "", "", "", "keji_new"});
        Tags.mvTag.put("时尚", new String[]{"", "", "", "", "", "", "shishang_new"});
        Tags.mvTag.put("汽车", new String[]{"", "", "", "", "", "", "qiche_new"});
        Tags.mvTag.put("亲子", new String[]{"", "", "", "", "", "", "qinzi_new"});
        Tags.mvTag.put("文化", new String[]{"", "", "", "", "", "", "wenhua_new"});
        Tags.mvTag.put("旅游", new String[]{"", "", "", "", "", "", "lvyou_new"});
        Tags.mvTag.put("秒懂", new String[]{"", "", "", "", "", "", "yunying_miaodong"});

        final int c = 7;
        // 酷狗
        // MV 标签
        Runnable initMvTagKg = () -> {
            String radioTagBody = HttpRequest.get(String.format(MV_TAG_KG_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONObject("data").getJSONArray("list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                String id = tagJson.getString("channel_id");

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[2] = id;
            }
        };
        // QQ
        // MV 标签
        Runnable initMvTagQq = () -> {
            String radioTagBody = HttpRequest.get(String.format(MV_TAG_QQ_API))
                    .execute()
                    .body();
            JSONObject radioTagJson = JSONObject.fromObject(radioTagBody);
            JSONArray tags = radioTagJson.getJSONObject("data").getJSONArray("version");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("全部".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[3] = "15";
                Tags.mvTag.get(name)[4] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initMvTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initMvTagQq));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // MV 排行 API
    private static final String TOP_MV_API
            = prefix + "/top/mv?area=%s&offset=%s&limit=%s";
    // 最新 MV API
    private static final String NEW_MV_API
            = prefix + "/mv/first?area=%s&limit=100";
    // 全部 MV API
    private static final String ALL_MV_API
            = prefix + "/mv/all?area=%s&type=%s&offset=%s&limit=%s";
    // 推荐 MV API
    private static final String RECOMMEND_MV_API
            = prefix + "/personalized/mv?limit=100";
    // 网易出品 MV API
    private static final String EXCLUSIVE_MV_API
            = prefix + "/mv/exclusive/rcmd?offset=%s&limit=%s";
    // 推荐 MV API (酷狗)
    private static final String RECOMMEND_MV_KG_API
            = "http://mobilecdnbj.kugou.com/api/v5/video/list?sort=4&id=%s&page=%s&pagesize=%s";
    // 推荐 MV API (QQ)
    private static final String RECOMMEND_MV_QQ_API
            = prefixQQ33 + "/mv/list?area=%s&version=%s&pageNo=%s&pageSize=%s";
    // 最新 MV API (QQ)
    private static final String NEW_MV_QQ_API
            = prefixQQ33 + "/new/mv?type=%s";
    // 推荐 MV API (千千)
    private static final String RECOMMEND_MV_QI_API
            = "https://music.91q.com/v1/video/list?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s";
    // 猜你喜欢视频 API (好看)
    private static final String GUESS_VIDEO_HK_API
            = "https://haokan.baidu.com/videoui/api/Getvideolandfeed?time=%s";
    // 榜单视频 API (好看)
    private static final String TOP_VIDEO_HK_API
            = "https://haokan.baidu.com/videoui/page/pc/toplist?type=hotvideo&sfrom=haokan_web_banner&page=%s&pageSize=%s&_format=json";
    // 推荐视频 API (好看)
    private static final String RECOMMEND_VIDEO_HK_API
            = "https://haokan.baidu.com/web/video/feed?tab=%s&act=pcFeed&pd=pc&num=%s&shuaxin_id=1661766211525";

    // 歌曲信息 API (单首)
    private static final String SINGLE_SONG_DETAIL_API = prefix + "/song/detail?ids=%s";
    // 歌曲信息 API (酷狗)
    private static final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&hash=1&album_audio_id=%s";
    // 歌曲信息 API (QQ)
    private static final String SINGLE_SONG_DETAIL_QQ_API = prefixQQ33 + "/song?songmid=%s";
    // 歌曲封面信息 API (QQ)
    private static final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";
    // 歌曲信息 API (酷我)
    private static final String SINGLE_SONG_DETAIL_KW_API = prefixKw + "/kuwo/musicInfo?mid=%s";
    // 歌曲信息 API (咪咕)
    private static final String SINGLE_SONG_DETAIL_MG_API = prefixMg + "/song?cid=%s";
    // 歌曲信息 API (千千)
    private static final String SINGLE_SONG_DETAIL_QI_API = "https://music.91q.com/v1/song/info?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲信息 API (猫耳)
    private static final String SINGLE_SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";
    // 歌曲 URL 获取 API
    private static final String GET_SONG_URL_API_NEW = prefix + "/song/url/v1?id=%s&level=hires";
    private static final String GET_SONG_URL_API = prefix + "/song/url?id=%s";
    // 歌曲 URL 获取 API (QQ)
    private static final String GET_SONG_URL_QQ_API = prefixQQ33 + "/song/url?id=%s";
    // 歌曲 URL 获取 API (酷我)
    private static final String GET_SONG_URL_KW_API = prefixKw + "/kuwo/url?mid=%s";
    // 歌曲 URL 获取 API (千千)
    private static final String GET_SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲 URL 获取 API (喜马拉雅)
    private static final String GET_SONG_URL_XM_API = "https://www.ximalaya.com/revision/play/v1/audio?id=%s&ptype=1";

    // 歌词 API
    private static final String LYRIC_API = prefix + "/lyric?id=%s";
    // 歌词 API 获取 (QQ)
    private static final String LYRIC_QQ_API = prefixQQ33 + "/lyric?songmid=%s";
    // 歌词 API 获取 (酷我)
    private static final String LYRIC_KW_API = prefixKw + "/kuwo/lrc?musicId=%s";

    // 歌单信息 API
    private static final String PLAYLIST_DETAIL_API = prefix + "/playlist/detail?id=%s";
    // 歌单歌曲 API
    private static final String PLAYLIST_SONGS_API = prefix + "/playlist/track/all?id=%s&offset=%s&limit=%s";
    // 歌单信息 API (酷狗)
    private static final String PLAYLIST_DETAIL_KG_API = "https://mobiles.kugou.com/api/v5/special/info_v2?appid=1058&specialid=0&global_specialid=%s&format=jsonp&srcappid=2919&clientver=20000&clienttime=1586163242519&mid=1586163242519&uuid=1586163242519&dfid=-&signature=%s";
    private static final String PLAYLIST_SONGS_KG_API = "https://mobiles.kugou.com/api/v5/special/song_v2?appid=1058&global_specialid=%s&specialid=0&plat=0&version=8000&page=%s&pagesize=%s&srcappid=2919&clientver=20000&clienttime=1586163263991&mid=1586163263991&uuid=1586163263991&dfid=-&signature=%s";
    //    private static final String PLAYLIST_DETAIL_KG_API = "https://m.kugou.com/plist/list/%s?json=true&page=%s";
    // 歌单信息 API (QQ)
    private static final String PLAYLIST_DETAIL_QQ_API = prefixQQ33 + "/songlist?id=%s";
    // 歌单信息 API (酷我)
    private static final String PLAYLIST_DETAIL_KW_API = prefixKw + "/kuwo/musicList?pid=%s";
    // 歌单信息 API (咪咕)
    private static final String PLAYLIST_DETAIL_MG_API = prefixMg + "/playlist?id=%s";
    // 歌单歌曲 API (咪咕)
    private static final String PLAYLIST_SONGS_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/user/queryMusicListSongs.do?" +
            "musicListId=%s&pageNo=%s&pageSize=%s";
    // 歌单信息 API (千千)
    private static final String PLAYLIST_DETAIL_QI_API = "https://music.91q.com/v1/tracklist/info?appid=16073360&id=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // 歌单信息 API (猫耳)
    private static final String PLAYLIST_DETAIL_ME_API = "https://www.missevan.com/sound/soundAllList?albumid=%s";

    // 专辑信息 API
    private static final String ALBUM_DETAIL_API = prefix + "/album?id=%s";
    // 专辑信息 API (酷狗)
    private static final String ALBUM_DETAIL_KG_API = "http://mobilecdn.kugou.com/api/v3/album/info?version=9108&albumid=%s";
    // 专辑歌曲 API (酷狗)
    private static final String ALBUM_SONGS_KG_API = "http://mobilecdn.kugou.com/api/v3/album/song?version=9108&albumid=%s&page=%s&pagesize=%s";
    // 专辑信息 API (QQ)
    private static final String ALBUM_DETAIL_QQ_API = prefixQQ33 + "/album?albummid=%s";
    // 专辑歌曲 API (QQ)
    private static final String ALBUM_SONGS_QQ_API = prefixQQ33 + "/album/songs?albummid=%s";
    // 专辑信息 API (酷我)
    private static final String ALBUM_DETAIL_KW_API = prefixKw + "/kuwo/albumInfo?albumId=%s&pn=%s&rn=%s";
    // 专辑信息 API (咪咕)
    private static final String ALBUM_DETAIL_MG_API = prefixMg + "/album?id=%s";
    // 专辑信息 API (千千)
    private static final String ALBUM_DETAIL_QI_API = "https://music.91q.com/v1/album/info?albumAssetCode=%s&appid=16073360&timestamp=%s";
    // 专辑信息 API (豆瓣)
    private static final String ALBUM_DETAIL_DB_API = "https://music.douban.com/subject/%s/";
    // 专辑信息 API (堆糖)
    private static final String ALBUM_DETAIL_DT_API = "https://www.duitang.com/napi/album/detail/?album_id=%s";

    // 歌手信息 API
    private static final String ARTIST_DETAIL_API = prefix + "/artists?id=%s";
    // 歌手信息 API (酷狗)
    private static final String ARTIST_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/info?singerid=%s";
    // 歌手信息 API (QQ)
    private static final String ARTIST_DETAIL_QQ_API = prefixQQ33 + "/singer/desc?singermid=%s";
    // 歌手图片 API (QQ)
    private static final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // 歌手信息 API (咪咕)
    private static final String ARTIST_DETAIL_MG_API = prefixMg + "/singer/desc?id=%s";
    // 歌手信息 API (千千)
    private static final String ARTIST_DETAIL_QI_API = "https://music.91q.com/v1/artist/info?appid=16073360&artistCode=%s&timestamp=%s";
    // 歌手信息 API (豆瓣)
    private static final String ARTIST_DETAIL_DB_API = "https://movie.douban.com/celebrity/%s/";
    // 歌手歌曲 API
    private static final String ARTIST_SONGS_API = prefix + "/artist/songs?id=%s&offset=%s&limit=%s";
    // 歌手专辑 API
    private static final String ARTIST_ALBUMS_API = prefix + "/artist/album?id=%s&offset=%s&limit=%s";
    // 歌手 MV API
    private static final String ARTIST_MVS_API = prefix + "/artist/mv?id=%s&offset=%s&limit=%s";
    // 歌手视频 API
//    private static final String ARTIST_VIDEOS_API = prefix + "/artist/video?id=%s&cursor=%s&size=%s";
    // 歌手歌曲 API (酷狗)
    private static final String ARTIST_SONGS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/song?&singerid=%s&page=%s&pagesize=%s";
    // 歌手专辑 API (酷狗)
    private static final String ARTIST_ALBUMS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/album?&singerid=%s&page=%s&pagesize=%s";
    // 歌手 MV API (酷狗)
    private static final String ARTIST_MVS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/mv?&singerid=%s&page=%s&pagesize=%s";
    // 歌手歌曲 API (QQ)
    private static final String ARTIST_SONGS_QQ_API = prefixQQ33 + "/singer/songs?singermid=%s&page=%s&num=%s";
    // 歌手专辑 API (QQ)
    private static final String ARTIST_ALBUMS_QQ_API = prefixQQ33 + "/singer/album?singermid=%s&pageNo=%s&pageSize=%s";
    // 歌手 MV API (QQ)
    private static final String ARTIST_MVS_QQ_API = prefixQQ33 + "/singer/mv?singermid=%s&pageNo=%s&pageSize=%s";
    // 歌手歌曲 API (酷我)
    private static final String ARTIST_SONGS_KW_API = prefixKw + "/kuwo/singer/music?artistid=%s&pn=%s&rn=%s";
    // 歌手专辑 API (酷我)
    private static final String ARTIST_ALBUMS_KW_API = prefixKw + "/kuwo/singer/album?artistid=%s&pn=%s&rn=%s";
    // 歌手 MV API (酷我)
    private static final String ARTIST_MVS_KW_API = prefixKw + "/kuwo/singer/mv?artistid=%s&pn=%s&rn=%s";
    // 歌手歌曲 API (咪咕)
    private static final String ARTIST_SONGS_MG_API = prefixMg + "/singer/songs?id=%s&pageNo=%s";
    // 歌手专辑 API (咪咕)
    private static final String ARTIST_ALBUMS_MG_API = prefixMg + "/singer/albums?id=%s&pageNo=%s";
    // 歌手歌曲 API (千千)
    private static final String ARTIST_SONGS_QI_API = "https://music.91q.com/v1/artist/song?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // 歌手专辑 API (千千)
    private static final String ARTIST_ALBUMS_QI_API = "https://music.91q.com/v1/artist/album?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    // 电台信息 API
    private static final String RADIO_DETAIL_API = prefix + "/dj/detail?rid=%s";
    // 电台节目信息 API
    private static final String RADIO_PROGRAM_DETAIL_API = prefix + "/dj/program?rid=%s&offset=%s&limit=%s";
    // 电台信息 API (QQ)
    private static final String RADIO_DETAIL_QQ_API = prefixQQ33 + "/radio?id=%s";
    // 电台信息 API (喜马拉雅)
    private static final String RADIO_DETAIL_XM_API = "https://www.ximalaya.com/revision/album/v1/simple?albumId=%s";
    // 电台节目 API (喜马拉雅)
    private static final String RADIO_PROGRAM_XM_API = "http://www.ximalaya.com/revision/album/v1/getTracksList?albumId=%s&pageNum=%s&pageSize=%s";
    // 电台信息 API (猫耳)
    private static final String RADIO_DETAIL_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 电台信息 API (豆瓣)
    private static final String RADIO_DETAIL_DB_API = "https://movie.douban.com/subject/%s/";
    // 图书电台信息 API (豆瓣)
    private static final String BOOK_RADIO_DETAIL_DB_API = "https://book.douban.com/subject/%s/";

    // MV 信息 API
    private static final String MV_DETAIL_API = prefix + "/mv/detail?mvid=%s";
    // MV 信息 API (酷狗)
    private static final String MV_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/mv/detail?area_code=1&plat=0&mvhash=%s";
    // MV 信息 API (QQ)
    private static final String MV_DETAIL_QQ_API = prefixQQ33 + "/mv?id=%s";

    // MV 视频链接 API
    private static final String MV_URL_API = prefix + "/mv/url?id=%s";
    // 视频链接 API
    private static final String VIDEO_URL_API = prefix + "/video/url?id=%s";
    // Mlog 链接 API
//    private static final String MLOG_URL_API = prefix + "/mlog/url?id=%s";
    // MV 视频链接获取 API (酷狗)
//    private static final String MV_URL_KG_API = "https://gateway.kugou.com/v2/interface/index?appid=1014&clienttime=%s&clientver=20000&cmd=123&dfid=-" +
//            "&ext=mp4&hash=%s&ismp3=0&key=kugoumvcloud&mid=%s&pid=6&srcappid=2919&ssl=1&uuid=%s";
    private static final String MV_URL_KG_API = "http://m.kugou.com/app/i/mv.php?cmd=100&hash=%s&ismp3=1&ext=mp4";
    // MV 视频链接获取 API (QQ)
    private static final String MV_URL_QQ_API = prefixQQ33 + "/mv/url?id=%s";
    // MV 视频链接获取 API (酷我)
    private static final String MV_URL_KW_API = prefixKw + "/kuwo/url?mid=%s&type=mv";
    // MV 视频链接获取 API (千千)
    private static final String MV_URL_QI_API = "https://music.91q.com/v1/video/info?appid=16073360&assetCode=%s&timestamp=%s";
    // MV 视频链接获取 API (好看)
    private static final String MV_URL_HK_API = "https://haokan.baidu.com/v?vid=%s&_format=json";

    // 榜单信息 API (酷狗)
    private static final String RANKING_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/song?volid=35050&rankid=%s&page=%s&pagesize=%s";
    // 榜单信息 API (QQ)
    private static final String RANKING_DETAIL_QQ_API = prefixQQ33 + "/top?id=%s&pageSize=%s";
    // 榜单信息 API (酷我)
    private static final String RANKING_DETAIL_KW_API = prefixKw + "/kuwo/rank/musicList?bangId=%s&pn=%s&rn=%s";
    // 榜单信息 API (咪咕)
    private static final String RANKING_DETAIL_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=%s";

    // 用户信息 API
    private static final String USER_DETAIL_API = prefix + "/user/detail?uid=%s";
    // 用户歌曲 API
    private static final String USER_SONGS_API = prefix + "/user/record?type=%s&uid=%s";
    // 用户信息 API (喜马拉雅)
    private static final String USER_DETAIL_XM_API = "https://www.ximalaya.com/revision/user/basic?uid=%s";
    // 用户节目 API (喜马拉雅)
    private static final String USER_PROGRAMS_XM_API = "https://www.ximalaya.com/revision/user/track?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户信息 API (猫耳)
    private static final String USER_DETAIL_ME_API = "https://www.missevan.com/%s/";
    // 用户节目 API (猫耳)
    private static final String USER_PROGRAMS_ME_API = "https://www.missevan.com/person/getusersound?user_id=%s&p=%s&page_size=%s";
    // 用户信息 API (豆瓣)
    private static final String USER_DETAIL_DB_API = "https://www.douban.com/people/%s/";
    // 用户信息 API (堆糖)
    private static final String USER_DETAIL_DT_API = "https://www.duitang.com/people/?id=%s";

    // 相似歌曲 API
    private static final String SIMILAR_SONG_API = prefix + "/simi/song?id=%s";
    // 相似歌曲 API (QQ)
    private static final String SIMILAR_SONG_QQ_API = prefixQQ33 + "/song/similar?id=%s";
    // 用户歌单 API
    private static final String USER_PLAYLIST_API = prefix + "/user/playlist?uid=%s&limit=1000";
    // 用户创建歌单 API (QQ)
    private static final String USER_CREATED_PLAYLIST_QQ_API = prefixQQ33 + "/user/songlist?id=%s";
    // 用户收藏歌单 API (QQ)
    private static final String USER_COLLECTED_PLAYLIST_QQ_API = prefixQQ33 + "/user/collect/songlist?id=%s&pageNo=%s&pageSize=%s";
    // 用户收藏专辑 API (QQ)
    private static final String USER_COLLECTED_ALBUM_QQ_API = prefixQQ33 + "/user/collect/album?id=%s&pageNo=%s&pageSize=%s";
    // 用户专辑 API (堆糖)
    private static final String USER_ALBUM_DT_API = "https://www.duitang.com/napi/album/list/by_user/?user_id=%s&start=%s&limit=%s";
    // 用户电台 API
    private static final String USER_RADIO_API = prefix + "/user/audio?uid=%s";
    // 用户电台 API (喜马拉雅)
    private static final String USER_RADIO_XM_API = "https://www.ximalaya.com/revision/user/pub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户收藏电台 API (喜马拉雅)
    private static final String USER_SUB_RADIO_XM_API = "https://www.ximalaya.com/revision/user/sub?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户电台 API (猫耳)
    private static final String USER_RADIO_ME_API = "https://www.missevan.com/dramaapi/getuserdramas?user_id=%s&s=&order=0&page=%s&page_size=%s";
    // 用户收藏电台 API (猫耳)
    private static final String USER_SUB_RADIO_ME_API = "https://www.missevan.com/dramaapi/getusersubscriptions?user_id=%s&page=%s&page_size=%s";
    // 用户专辑 API (豆瓣)
    private static final String USER_ALBUM_DB_API = "https://music.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户电台 API (豆瓣)
    private static final String USER_RADIO_DB_API = "https://movie.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";
    // 用户图书电台 API (豆瓣)
    private static final String USER_BOOK_RADIO_DB_API = "https://book.douban.com/people/%s/collect?start=%s&sort=time&rating=all&filter=all&mode=grid";

    // 歌曲相关歌单 API
    private static final String RELATED_PLAYLIST_API = prefix + "/simi/playlist?id=%s";
    // 相关歌单 API (QQ)
    private static final String RELATED_PLAYLIST_QQ_API = prefixQQ33 + "/song/playlist?id=%s";
    // 歌单相似歌单 API
    private static final String SIMILAR_PLAYLIST_API = prefix + "/related/playlist?id=%s";
    // 相似 MV API
    private static final String SIMILAR_MV_API = prefix + "/simi/mv?mvid=%s";
    // 视频相关视频 API
    private static final String RELATED_VIDEO_API = prefix + "/related/allvideo?id=%s";
    // 歌曲相关视频 API
    private static final String RELATED_MLOG_API = prefix + "/mlog/music/rcmd?songid=%s&limit=500";
    // mlog id 转视频 id API
    private static final String MLOG_TO_VIDEO_API = prefix + "/mlog/to/video?id=%s";
    // 相关 MV API (QQ)
    private static final String RELATED_MV_QQ_API = prefixQQ33 + "/song/mv?id=%s";
    // 相似歌手 API
    private static final String SIMILAR_ARTIST_API = prefix + "/simi/artist?id=%s";
    // 相似歌手 API (酷狗)(POST)
    private static final String SIMILAR_ARTIST_KG_API = "http://kmr.service.kugou.com/v1/author/similar";
    // 相似歌手 API (QQ)
    private static final String SIMILAR_ARTIST_QQ_API = prefixQQ33 + "/singer/sim?singermid=%s";
    // 歌手合作人 API (豆瓣)
    private static final String ARTIST_BUDDY_DB_API = "https://movie.douban.com/celebrity/%s/partners?start=%s";
    // 歌手电台 API (豆瓣)
    private static final String ARTIST_RADIO_DB_API = "https://movie.douban.com/celebrity/%s/movies?start=%s&format=pic&sortby=time";
    // 相似视频 API (好看)
    private static final String SIMILAR_VIDEO_HK_API = "https://haokan.baidu.com/videoui/api/videorec?title=%s&vid=%s&act=pcRec&pd=pc";

    // 用户关注 API
    private static final String USER_FOLLOWS_API = prefix + "/user/follows?uid=%s&limit=1000";
    // 用户关注 API (喜马拉雅)
    private static final String USER_FOLLOWS_XM_API = "https://www.ximalaya.com/revision/user/following?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户关注 API (猫耳)
    private static final String USER_FOLLOWS_ME_API = "https://www.missevan.com/person/getuserattention?type=0&user_id=%s&p=%s&page_size=%s";
    // 用户粉丝 API
    private static final String USER_FOLLOWEDS_API = prefix + "/user/followeds?uid=%s&offset=%s&limit=%s";
    // 用户粉丝 API (喜马拉雅)
    private static final String USER_FOLLOWEDS_XM_API = "https://www.ximalaya.com/revision/user/fans?uid=%s&page=%s&pageSize=%s&keyWord=";
    // 用户粉丝 API (猫耳)
    private static final String USER_FOLLOWEDS_ME_API = "https://www.missevan.com/person/getuserattention?type=1&user_id=%s&p=%s&page_size=%s";

    // 歌单收藏者 API
    private static final String PLAYLIST_SUBSCRIBERS_API = prefix + "/playlist/subscribers?id=%s&offset=%s&limit=%s";
    // 歌手粉丝 API
    private static final String ARTIST_FANS_API = prefix + "/artist/fans?id=%s&offset=%s&limit=%s";
    // 歌手粉丝总数 API
    private static final String ARTIST_FANS_TOTAL_API = prefix + "/artist/follow/count?id=%s";
    // 歌手粉丝 API (豆瓣)
    private static final String ARTIST_FANS_DB_API = "https://movie.douban.com/celebrity/%s/fans?start=%s";
    // 电台订阅者 API
    private static final String RADIO_SUBSCRIBERS_API = prefix + "/dj/subscriber?id=%s";
    // 电台演职员 API (豆瓣)
    private static final String RADIO_ARTISTS_DB_API = "https://movie.douban.com/subject/%s/celebrities";
    // 相似电台 API (豆瓣)
    private static final String SIMILAR_RADIO_DB_API = "https://movie.douban.com/subject/%s/";
    // 相似图书电台 API (豆瓣)
    private static final String SIMILAR_BOOK_RADIO_DB_API = "https://book.douban.com/subject/%s/";
    // 相似专辑 API (豆瓣)
    private static final String SIMILAR_ALBUM_DB_API = "https://music.douban.com/subject/%s/";

    // 格言 API
    private static final String MOTTO_API = "https://v1.hitokoto.cn/?encode=json&lang=cn&c=d&c=i";

    /**
     * 获取格言
     *
     * @return
     */
    public static String getMotto() {
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

    /**
     * 获取热搜
     *
     * @return
     */
    public static Set<String> getHotSearch() {
        Set<String> results = new LinkedHashSet<>();

        // 网易云
        Callable<List<String>> getHotSearch = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.get(String.format(HOT_SEARCH_API))
                    .execute()
                    .body();
            JSONObject hotSearchJson = JSONObject.fromObject(hotSearchBody);
            JSONObject result = hotSearchJson.getJSONObject("result");
            JSONArray hotSearchArray = result.getJSONArray("hots");
            for (int i = 0, len = hotSearchArray.size(); i < len; i++) {
                JSONObject keywordJson = hotSearchArray.getJSONObject(i);
                res.add(keywordJson.getString("first").trim());
            }
            return res;
        };

        // 酷狗
        Callable<List<String>> getHotSearchKg = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.get(String.format(HOT_SEARCH_KG_API))
                    .header("dfid", "1ssiv93oVqMp27cirf2CvoF1")
                    .header("mid", "156798703528610303473757548878786007104")
                    .header("clienttime", "1584257267")
                    .header("x-router", "msearch.kugou.com")
                    .header("user-agent", "Android9-AndroidPhone-10020-130-0-searchrecommendprotocol-wifi")
                    .header("kg-rc", "1")
                    .execute()
                    .body();
            JSONArray hotkeys = JSONObject.fromObject(hotSearchBody).getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("keywords");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                res.add(keywordJson.getString("keyword"));
            }
            return res;
        };

        // QQ
        Callable<List<String>> getHotSearchQq = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.post(String.format(HOT_SEARCH_QQ_API))
                    .body("{\"comm\":{\"ct\":\"19\",\"cv\":\"1803\",\"guid\":\"0\",\"patch\":\"118\",\"psrf_access_token_expiresAt\":0,\"psrf_qqaccess_token\":\"\",\"psrf_qqopenid\":\"\",\"psrf_qqunionid\":\"\",\"tmeAppID\":\"qqmusic\",\"tmeLoginType\":0,\"uin\":\"0\",\"wid\":\"0\"},\"hotkey\":{\"method\":\"GetHotkeyForQQMusicPC\",\"module\":\"tencent_musicsoso_hotkey.HotkeyService\",\"param\":{\"search_id\":\"\",\"uin\":0}}}")
                    .execute()
                    .body();
            JSONArray hotkeys = JSONObject.fromObject(hotSearchBody).getJSONObject("hotkey").getJSONObject("data").getJSONArray("vec_hotkey");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                res.add(keywordJson.getString("title"));
            }
            return res;
        };

        // 酷我
        Callable<List<String>> getHotSearchKw = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(String.format(HOT_SEARCH_KW_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONArray hotkeys = JSONObject.fromObject(resp.body()).getJSONArray("tagvalue");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    res.add(hotkeys.getJSONObject(i).getString("key"));
                }
            }
            return res;
        };

        // 咪咕
        Callable<List<String>> getHotSearchMg = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(String.format(HOT_SEARCH_MG_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONArray hotkeys = JSONObject.fromObject(resp.body()).getJSONArray("data");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    res.add(hotkeys.getJSONObject(i).getString("word"));
                }
            }
            return res;
        };

        List<Future<List<String>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearch));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchMg));

        taskList.forEach(task -> {
            try {
                results.addAll(task.get());
            } catch (InterruptedException e) {
//                e.printStackTrace();
            } catch (ExecutionException e) {
//                e.printStackTrace();
            }
        });

        return results;
    }

    /**
     * 获取搜索建议
     *
     * @return
     */
    public static Set<String> getSearchSuggestion(String keyword) {
        Set<String> results = new LinkedHashSet<>();

        // 关键词为空时直接跳出
        if (StringUtils.isEmpty(keyword.trim())) return results;

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<List<String>> getSimpleSearchSuggestion = () -> {
            LinkedList<String> res = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SIMPLE_SEARCH_SUGGESTION_API, encodedKeyword))
                    .execute()
                    .body();
            JSONObject searchSuggestionJson = JSONObject.fromObject(searchSuggestionBody);
            JSONObject result = searchSuggestionJson.optJSONObject("result");
            if (result != null && !result.isNullObject()) {
                JSONArray searchSuggestionArray = result.optJSONArray("allMatch");
                if (searchSuggestionArray != null) {
                    for (int i = 0, len = searchSuggestionArray.size(); i < len; i++) {
                        JSONObject keywordJson = searchSuggestionArray.getJSONObject(i);
                        res.add(keywordJson.getString("keyword"));
                    }
                }
            }
            return res;
        };
        Callable<List<String>> getSearchSuggestion = () -> {
            LinkedList<String> res = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_API, encodedKeyword))
                    .execute()
                    .body();
            JSONObject searchSuggestionJson = JSONObject.fromObject(searchSuggestionBody);
            JSONObject result = searchSuggestionJson.optJSONObject("result");
            if (result != null) {
                JSONArray songArray = result.optJSONArray("songs");
                if (songArray != null) {
                    for (int i = 0, len = songArray.size(); i < len; i++) {
                        res.add(songArray.getJSONObject(i).getString("name"));
                    }
                }
                JSONArray artistArray = result.optJSONArray("artists");
                if (artistArray != null) {
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        res.add(artistArray.getJSONObject(i).getString("name"));
                    }
                }
                JSONArray albumArray = result.optJSONArray("albums");
                if (albumArray != null) {
                    for (int i = 0, len = albumArray.size(); i < len; i++) {
                        res.add(albumArray.getJSONObject(i).getString("name"));
                    }
                }
            }
            return res;
        };

        // 酷狗
        Callable<List<String>> getSearchSuggestionKg = () -> {
            LinkedList<String> res = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_KG_API, encodedKeyword))
                    .execute()
                    .body();
            JSONObject searchSuggestionJson = JSONObject.fromObject(searchSuggestionBody);
            JSONArray data = searchSuggestionJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject keywordJson = data.getJSONObject(i);
                res.add(keywordJson.getString("keyword"));
            }
            return res;
        };

        // QQ
        Callable<List<String>> getSearchSuggestionQq = () -> {
            LinkedList<String> res = new LinkedList<>();

            String searchSuggestionBody = HttpRequest.get(String.format(SEARCH_SUGGESTION_QQ_API, encodedKeyword))
                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                    .execute()
                    .body();
            JSONObject searchSuggestionJson = JSONObject.fromObject(searchSuggestionBody);
            JSONObject data = searchSuggestionJson.optJSONObject("data");
            if (data != null) {
                JSONArray songArray = data.getJSONObject("song").getJSONArray("itemlist");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    res.add(songArray.getJSONObject(i).getString("name"));
                }
                JSONArray artistArray = data.getJSONObject("singer").getJSONArray("itemlist");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    res.add(artistArray.getJSONObject(i).getString("name"));
                }
                JSONArray albumArray = data.getJSONObject("album").getJSONArray("itemlist");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    res.add(albumArray.getJSONObject(i).getString("name"));
                }
                JSONArray mvArray = data.getJSONObject("mv").getJSONArray("itemlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    res.add(mvArray.getJSONObject(i).getString("name"));
                }
            }
            return res;
        };

        // 酷我
        Callable<List<String>> getSearchSuggestionKw = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_SUGGESTION_KW_API, encodedKeyword)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject searchSuggestionJson = JSONObject.fromObject(resp.body());
                JSONArray data = searchSuggestionJson.getJSONArray("data");
                Pattern p = Pattern.compile("RELWORD=(.*?)\\r\\n");
                for (int i = 0, len = data.size(); i < len; i++) {
                    Matcher matcher = p.matcher(data.getString(i));
                    matcher.find();
                    res.add(matcher.group(1));
                }
            }
            return res;
        };

        // 千千
        Callable<List<String>> getSearchSuggestionQi = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(buildQianUrl(String.format(SEARCH_SUGGESTION_QI_API, System.currentTimeMillis(), encodedKeyword))).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject searchSuggestionJson = JSONObject.fromObject(resp.body());
                JSONArray data = searchSuggestionJson.getJSONArray("data");
                for (int i = 0, len = data.size(); i < len; i++) {
                    res.add(data.getString(i));
                }
            }
            return res;
        };

        List<Future<List<String>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getSimpleSearchSuggestion));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestion));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSearchSuggestionQi));

        taskList.forEach(task -> {
            try {
                results.addAll(task.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        return results;
    }

    /**
     * 根据关键词获取歌曲
     */
    public static CommonResult<NetMusicInfo> searchMusic(int type, String subType, String keyword, int limit, int page) throws IOException {
        AtomicReference<Integer> total = new AtomicReference<>(0);
        List<NetMusicInfo> musicInfos = new LinkedList<>();
//        Set<NetMusicInfo> set = new HashSet<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        boolean dt = "默认".equals(subType);
        String[] s = Tags.programSearchTag.get(subType);

        // 网易云
        // 搜歌曲
        Callable<CommonResult<NetMusicInfo>> searchMusic = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject result = musicInfoJson.getJSONObject("result");
            t = result.getInt("songCount");
            JSONArray songsArray = result.optJSONArray("songs");
            if (songsArray != null) {
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name");
                    String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String albumName = songJson.getJSONObject("al").getString("name");
                    Double duration = songJson.getDouble("dt") / 1000;
                    String mvId = songJson.getString("mv");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyric = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject result = musicInfoJson.getJSONObject("result");
            t = result.getInt("songCount");
            JSONArray songs = result.optJSONArray("songs");
            if (songs != null) {
                for (int i = 0, len = songs.size(); i < len; i++) {
                    JSONObject songJson = songs.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name");
                    String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String albumName = songJson.getJSONObject("album").getString("name");
                    Double duration = songJson.getDouble("duration") / 1000;
                    String mvId = songJson.getString("mvid");
                    JSONObject lyrics = songJson.optJSONObject("lyrics");
                    String lrcMatch = null;
                    if (lyrics != null) lrcMatch = lyrics.getString("txt").replace("\n", " / ");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);
                    musicInfo.setLrcMatch(lrcMatch);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 搜声音
        Callable<CommonResult<NetMusicInfo>> searchVoice = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_VOICE_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("totalCount");
            JSONArray songsArray = data.optJSONArray("resources");
            if (songsArray != null) {
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject programJson = songsArray.getJSONObject(i).getJSONObject("baseInfo");
                    JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                    JSONObject djJson = programJson.getJSONObject("dj");
                    JSONObject radioJson = programJson.getJSONObject("radio");

                    String programId = programJson.getString("id");
                    String songId = mainSongJson.getString("id");
                    String name = mainSongJson.getString("name");
                    String artist = djJson.getString("nickname");
                    String albumName = radioJson.getString("name");
                    String albumImgUrl = programJson.getString("coverUrl");
                    Double duration = mainSongJson.getDouble("duration") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setProgramId(programId);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumImgUrl(albumImgUrl);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        // 搜单曲
        Callable<CommonResult<NetMusicInfo>> searchMusicKg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songsArray = data.getJSONArray("info");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] split = songJson.getString("filename").split(" - ");
                String songName = split[split.length == 1 ? 0 : 1];
                String artist = songJson.getString("singername");
                String albumName = songJson.getString("album_name");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");
                String lrcMatch = songJson.optString("lyric");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setLrcMatch(lrcMatch);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyricKg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_BY_LYRIC_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songsArray = data.getJSONArray("info");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] split = songJson.getString("filename").split(" - ");
                String songName = split[split.length == 1 ? 0 : 1];
                String artist = songJson.getString("singername");
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");
                String lrcMatch = songJson.optString("lyric");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setLrcMatch(lrcMatch);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 搜歌曲
        Callable<CommonResult<NetMusicInfo>> searchMusicQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 0))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray songsArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 搜歌词
        Callable<CommonResult<NetMusicInfo>> searchMusicByLyricQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 7))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray songsArray = data.getJSONObject("body").getJSONObject("song").getJSONArray("list");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("id");
                String lrcMatch = StringUtils.removeHTMLLabel(songJson.optString("content")).replace("\\n", " / ");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                musicInfo.setLrcMatch(lrcMatch);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetMusicInfo>> searchMusicKw = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MUSIC_KW_API, encodedKeyword, page, limit)).execute();
            // 有时候请求会崩，先判断是否请求成功
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.optJSONObject("data");
                if (data != null) {
                    t = data.getInt("total");
                    JSONArray songsArray = data.optJSONArray("list");
                    if (songsArray != null) {
                        for (int i = 0, len = songsArray.size(); i < len; i++) {
                            JSONObject songJson = songsArray.getJSONObject(i);

                            String songId = songJson.getString("rid");
                            String songName = songJson.getString("name");
                            String artist = StringUtils.removeHTMLLabel(songJson.getString("artist"));
                            String albumName = songJson.getString("album");
                            Double duration = songJson.getDouble("duration");
                            String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                            NetMusicInfo musicInfo = new NetMusicInfo();
                            musicInfo.setSource(NetMusicSource.KW);
                            musicInfo.setId(songId);
                            musicInfo.setName(songName);
                            musicInfo.setArtist(artist);
                            musicInfo.setAlbumName(albumName);
                            musicInfo.setDuration(duration);
                            musicInfo.setMvId(mvId);

                            res.add(musicInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetMusicInfo>> searchMusicMg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.optInt("total");
            JSONArray songsArray = data.getJSONArray("list");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("cid");
                String songName = songJson.getString("name");
                String artist = parseArtists(songJson, NetMusicSource.MG);
                String albumName = songJson.getJSONObject("album").getString("name");
                // 咪咕音乐没有 mv 时，该字段不存在！
                String mvId = songJson.optString("mvId");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        Callable<CommonResult<NetMusicInfo>> searchMusicQi = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(buildQianUrl(String.format(SEARCH_MUSIC_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword)))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.optInt("total");
            JSONArray songsArray = data.getJSONArray("typeTrack");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String songName = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QI);
                String albumName = songJson.getString("albumTitle");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetMusicInfo>> searchMusicXm = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(SEARCH_MUSIC_XM_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data").getJSONObject("track");
            t = data.optInt("total");
            JSONArray songsArray = data.getJSONArray("docs");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("title");
                String artist = songJson.getString("nickname");
                String albumName = songJson.getString("albumTitle");
                String albumImgUrl = songJson.getString("coverPath");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
                // 喜马拉雅是 m4a 格式的文件！
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        Callable<CommonResult<NetMusicInfo>> searchProgramMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String musicInfoBody = HttpRequest.get(String.format(SEARCH_PROGRAM_ME_API, s[0], encodedKeyword, page, limit))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("info");
                t = data.getJSONObject("pagination").getInt("count");
                JSONArray songsArray = data.getJSONArray("Datas");
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String name = songJson.getString("soundstr");
                    String artist = songJson.getString("username");
                    Double duration = songJson.getDouble("duration") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setFormat(Format.M4A);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        switch (type) {
            case 1:
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyric));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyricKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicByLyricQq));
                break;
            case 2:
                if (dt) {
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchVoice));
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicXm));
                } else {
                    taskList.add(GlobalExecutors.requestExecutor.submit(searchProgramMe));
                }
                break;
            default:
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusic));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicKg));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicQq));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicKw));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicMg));
                taskList.add(GlobalExecutors.requestExecutor.submit(searchMusicQi));
        }

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        musicInfos.addAll(ListUtils.joinAll(rl));


        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、url、歌词)
     */
    public static void fillNetMusicInfo(NetMusicInfo musicInfo) throws IOException {
        // 歌曲信息是完整的
        if (musicInfo.isIntegrated()) return;

        String songId = musicInfo.getId();
        int source = musicInfo.getSource();
        boolean isProgram = musicInfo.isProgram();

        List<Future<?>> taskList = new LinkedList<>();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_API, songId))
                        .execute()
                        .body();
                JSONArray array = JSONObject.fromObject(songBody).optJSONArray("songs");
                if (array != null) {
                    JSONObject songJson = array.getJSONObject(0);
                    if (!musicInfo.hasDuration()) musicInfo.setDuration(songJson.getDouble("dt") / 1000);
                    if (!musicInfo.hasAlbumName())
                        musicInfo.setAlbumName(songJson.getJSONObject("al").getString("name"));
                    if (!musicInfo.hasAlbumImage()) {
                        GlobalExecutors.imageExecutor.submit(() -> {
                            BufferedImage albumImage = getImageFromUrl(isProgram ? musicInfo.getAlbumImgUrl()
                                    : songJson.getJSONObject("al").getString("picUrl"));
                            ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                            musicInfo.callback();
                        });
                    }
                }
            }));
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.hasUrl()) {
                    String url = fetchMusicUrl(songId, NetMusicSource.NET_CLOUD);
                    musicInfo.setUrl(url);
                    // 网易云音乐里面有的电台节目是 flac 格式！
                    if (url.endsWith(Format.FLAC)) musicInfo.setFormat(Format.FLAC);
                }
            }));
            // 填充歌词、翻译、罗马音
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.isLrcIntegrated()) fillLrc(musicInfo);
            }));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            // 酷狗接口请求需要带上 cookie ！
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, songId))
                    .header(Header.COOKIE, COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("timelength") / 1000);
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album_name"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = getImageFromUrl(data.getString("img"));
                    ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
//                musicInfo.setAlbumImage(getImageFromUrl(data.getString("img")));
                });
            }
            if (!musicInfo.hasUrl()) musicInfo.setUrl(data.getString("play_url"));
            if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyrics"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");
                JSONObject trackInfo = data.getJSONObject("track_info");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(trackInfo.getDouble("interval"));
                if (!musicInfo.hasAlbumName())
                    musicInfo.setAlbumName(trackInfo.getJSONObject("album").getString("name"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        // QQ 的歌曲专辑图片需要额外请求接口获得！
                        BufferedImage albumImage = getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, trackInfo.getJSONObject("album").getString("mid")));
                        // 有的歌曲没有专辑，先找备份专辑图片，如果还没有就将歌手的图片作为封面
                        if (albumImage == null)
                            albumImage = getImageFromUrl(String.format(SINGLE_SONG_IMG_QQ_API, trackInfo.getJSONObject("album").getString("pmid")));
                        if (albumImage == null)
                            albumImage = getImageFromUrl(String.format(ARTIST_IMG_QQ_API, trackInfo.getJSONArray("singer").getJSONObject(0).getString("mid")));
                        ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
//                musicInfo.setAlbumImage(albumImage);
                    });
                }
            }));
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.hasUrl()) musicInfo.setUrl(fetchMusicUrl(songId, NetMusicSource.QQ));
            }));
            // 填充歌词、翻译、罗马音
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.isLrcIntegrated()) fillLrc(musicInfo);
            }));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KW_API, songId))
                        .execute()
                        .body();
                JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");

                if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
                if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("album"));
                if (!musicInfo.hasAlbumImage()) {
                    GlobalExecutors.imageExecutor.submit(() -> {
                        BufferedImage albumImage = getImageFromUrl(data.getString("pic"));
                        ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                        musicInfo.callback();
//                musicInfo.setAlbumImage(getImageFromUrl(data.getString("pic")));
                    });
                }
            }));
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.hasUrl()) musicInfo.setUrl(fetchMusicUrl(songId, NetMusicSource.KW));
            }));
            // 填充歌词、翻译、罗马音
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.isLrcIntegrated()) fillLrc(musicInfo);
            }));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");
            // 咪咕的专辑名称需要额外请求专辑信息接口！
            if (!musicInfo.hasAlbumName()) {
                String albumBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, data.getJSONObject("album").getString("id")))
                        .execute()
                        .body();
                JSONObject albumData = JSONObject.fromObject(albumBody).getJSONObject("data");
                musicInfo.setAlbumName(albumData.getString("name"));
            }
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = getImageFromUrl(data.getString("picUrl"));
                    ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
//                musicInfo.setAlbumImage(getImageFromUrl(data.getString("picUrl")));
                });
            }
            if (!musicInfo.hasUrl()) musicInfo.setUrl(data.getString("320"));
            if (!musicInfo.hasLrc()) musicInfo.setLrc(data.getString("lyric"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String songBody = HttpRequest.get(buildQianUrl(String.format(SINGLE_SONG_DETAIL_QI_API, songId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(songBody).getJSONArray("data").getJSONObject(0);
            if (!musicInfo.hasAlbumName()) musicInfo.setAlbumName(data.getString("albumTitle"));
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = getImageFromUrl(data.getString("pic"));
                    ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.hasUrl()) musicInfo.setUrl(fetchMusicUrl(songId, NetMusicSource.QI));
            }));
            // 填充歌词、翻译、罗马音
            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                if (!musicInfo.isLrcIntegrated()) fillLrc(musicInfo);
            }));
        }

        // 喜马拉雅(时长、专辑名称提前写入了，没有歌词)
        else if (source == NetMusicSource.XM) {
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = getImageFromUrl(musicInfo.getAlbumImgUrl());
                    ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
//                musicInfo.setAlbumImage(getImageFromUrl(musicInfo.getAlbumImgUrl()));
                });
            }
            if (!musicInfo.hasUrl()) musicInfo.setUrl(fetchMusicUrl(songId, NetMusicSource.XM));
            musicInfo.setLrc("");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(songBody).getJSONObject("info").getJSONObject("sound");
            // 时长是毫秒，转为秒
            if (!musicInfo.hasDuration()) musicInfo.setDuration(data.getDouble("duration") / 1000);
            if (!musicInfo.hasArtist()) musicInfo.setArtist(data.getString("username"));
            if (!musicInfo.hasAlbumImage()) {
                GlobalExecutors.imageExecutor.submit(() -> {
                    BufferedImage albumImage = getImageFromUrl(data.getString("front_cover"));
                    ImageUtils.toFile(albumImage, SimplePath.IMG_CACHE_PATH + musicInfo.toAlbumImageFileName());
                    musicInfo.callback();
                });
            }
            if (!musicInfo.hasUrl()) musicInfo.setUrl(data.getString("soundurl"));
            musicInfo.setLrc("");
        }

        // 阻塞等待所有请求完成
        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 根据关键词获取歌单
     */
    public static CommonResult<NetPlaylistInfo> searchPlaylists(String keyword, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject result = playlistInfoJson.getJSONObject("result");
            if (result.has("playlists")) {
                t = result.getInt("playlistCount");
                JSONArray playlistArray = result.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("nickname");
                    String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray playlistArray = data.getJSONArray("info");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.getInt("songcount");
                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 3))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray playlistArray = data.getJSONObject("body").getJSONObject("songlist").getJSONArray("list");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("dissid");
                String playlistName = playlistJson.getString("dissname");
                String creator = playlistJson.getJSONObject("creator").getString("name");
                Long playCount = playlistJson.getLong("listennum");
                Integer trackCount = playlistJson.getInt("song_count");
                String coverImgThumbUrl = playlistJson.getString("imgurl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QQ);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_PLAYLIST_KW_API, encodedKeyword, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("list");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetPlaylistInfo>> searchPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(SEARCH_PLAYLIST_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.optJSONObject("data");
            if (data != null) {
                t = data.optInt("total");
                JSONArray playlistArray = data.getJSONArray("list");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("name");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("songCount");
                    String coverImgThumbUrl = playlistJson.getString("picUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylists));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchPlaylistsMg));

        List<List<NetPlaylistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetPlaylistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        playlistInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(playlistInfos, total.get());
    }

    /**
     * 根据关键词获取专辑
     */
    public static CommonResult<NetAlbumInfo> searchAlbums(String keyword, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetAlbumInfo> albumInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<CommonResult<NetAlbumInfo>> searchAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject result = albumInfoJson.getJSONObject("result");
            if (!result.has("albums")) return new CommonResult<>(albumInfos, 0);
            t = result.getInt("albumCount");
            JSONArray albumArray = result.getJSONArray("albums");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getInt("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsKg = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("info");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
//            String description = albumJson.getString("intro");
                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
                Integer songNum = albumJson.getInt("songcount");
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//            albumInfo.setCoverImgUrl(coverImgUrl);
//            albumInfo.setDescription(description);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsQq = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 2))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray albumArray = data.getJSONObject("body").getJSONObject("album").getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumMID");
                String albumName = albumJson.getString("albumName");
                String artist = parseArtists(albumJson, NetMusicSource.QQ);
                String publishTime = albumJson.getString("publicTime");
                Integer songNum = albumJson.getInt("song_count");
                String coverImgThumbUrl = albumJson.getString("albumPic").replaceFirst("http:", "https:");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsKw = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_KW_API, encodedKeyword, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String albumInfoBody = resp.body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject data = albumInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray albumArray = data.getJSONArray("albumList");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumid");
                    String albumName = albumJson.getString("album").replace("&nbsp;", " ");
                    String artist = albumJson.getString("artist");
                    String publishTime = albumJson.getString("releaseDate");
                    String coverImgThumbUrl = albumJson.getString("pic");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.KW);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsMg = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            // 咪咕可能接口异常，需要判空！
            JSONObject data = albumInfoJson.optJSONObject("data");
            if (data != null) {
                t = data.optInt("total");
                JSONArray albumArray = data.getJSONArray("list");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = parseArtists(albumJson, NetMusicSource.MG);
                    String publishTime = albumJson.getString("publishTime");
                    Integer songNum = albumJson.getInt("songCount");
                    String coverImgThumbUrl = albumJson.getString("picUrl");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.MG);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime("null".equals(publishTime) ? "" : publishTime);
                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsQi = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(buildQianUrl(String.format(SEARCH_ALBUM_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword))).execute();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("typeAlbum");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = parseArtists(albumJson, NetMusicSource.QI);
                String rd = albumJson.optString("releaseDate");
                String publishTime = StringUtils.isNotEmpty(rd) ? rd.split("T")[0] : "";
                String coverImgThumbUrl = albumJson.getString("pic");
                Integer songNum = albumJson.getJSONArray("trackList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsDb = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(SEARCH_ALBUM_DB_API, encodedKeyword, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            t = albumInfoJson.getInt("total");
            JSONArray albumArray = albumInfoJson.getJSONArray("items");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(albumArray.getString(i));
                Elements result = doc.select("div.result");
                Elements a = result.select("h3 a");

                String albumId = ReUtil.get("sid: (\\d+)", a.attr("onclick"), 1);
                String albumName = a.text().trim();
                String artist = result.select("span.subject-cast").text();
                String coverImgThumbUrl = result.select("div.pic img").attr("src");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 堆糖
        Callable<CommonResult<NetAlbumInfo>> searchAlbumsDt = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_ALBUM_DT_API, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis())).execute();
            String albumInfoBody = resp.body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("object_list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = albumJson.getJSONObject("user").getString("username");
                String publishTime = TimeUtils.msToDate(albumJson.getLong("updated_at_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
//                Integer songNum = albumJson.getJSONArray("trackList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
//                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetAlbumInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbums));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsDb));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchAlbumsDt));

        List<List<NetAlbumInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetAlbumInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        albumInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(albumInfos, total.get());
    }

    /**
     * 根据关键词获取歌手
     */
    public static CommonResult<NetArtistInfo> searchArtists(String keyword, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetArtistInfo> artistInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<CommonResult<NetArtistInfo>> searchArtists = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject result = artistInfoJson.optJSONObject("result");
            if (result != null) {
                t = result.getInt("artistCount");
                JSONArray ArtistArray = result.getJSONArray("artists");
                for (int i = 0, len = ArtistArray.size(); i < len; i++) {
                    JSONObject artistJson = ArtistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer albumNum = artistJson.getInt("albumSize");
                    Integer mvNum = artistJson.getInt("mvSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setAlbumNum(albumNum);
                    artistInfo.setMvNum(mvNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetArtistInfo>> searchArtistsQq = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 1))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray artistArray = data.getJSONObject("body").getJSONObject("singer").getJSONArray("list");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("singerMID");
                String artistName = artistJson.getString("singerName");
                Integer songNum = artistJson.getInt("songNum");
                Integer albumNum = artistJson.getInt("albumNum");
                Integer mvNum = artistJson.getInt("mvNum");
                // QQ 需要提前记录歌手封面 url
                String coverImgUrl = artistJson.getString("singerPic")
                        .replaceFirst("150x150", "500x500")
                        .replaceFirst("http:", "https:");
                String coverImgThumbUrl = coverImgUrl;

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QQ);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                artistInfo.setCoverImgUrl(coverImgUrl);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetArtistInfo>> searchArtistsKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_ARTIST_KW_API, encodedKeyword, page, limit)).execute();
            // 酷我有时候会崩，先验证是否请求成功
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getInt("musicNum");
                    String coverImgUrl = artistJson.getString("pic300");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KW);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setSongNum(songNum);
                    // 酷我音乐没有单独的歌手信息接口，需要在搜索歌手时记录封面图片 url ！
                    artistInfo.setCoverImgUrl(coverImgUrl);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                        if (coverImgThumb == null) coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetArtistInfo>> searchArtistsMg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.optJSONObject("data");
            // 咪咕可能接口异常，需要判空！
            if (data != null) {
                t = data.optInt("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getInt("songCount");
                    Integer albumNum = artistJson.getInt("albumCount");
                    String coverImgThumbUrl = artistJson.getString("picUrl");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.MG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        Callable<CommonResult<NetArtistInfo>> searchArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(buildQianUrl(String.format(SEARCH_ARTIST_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword)))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.optJSONObject("data");
            t = data.optInt("total");
            JSONArray artistArray = data.getJSONArray("typeArtist");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("artistCode");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getInt("trackTotal");
                String coverImgThumbUrl = artistJson.getString("pic");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QI);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        Callable<CommonResult<NetArtistInfo>> searchArtistsDb = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_DB_API, encodedKeyword, (page - 1) * 15))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            t = Integer.parseInt(doc.select("div.rr").first().text().split("共")[1]);
            t += t / 15 * 5;
            Elements result = doc.select("div.result");
            for (int i = 0, len = result.size(); i < len; i++) {
                Element artist = result.get(i);
                Element a = artist.select(".content a").first();
                Element img = artist.select(".pic img").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetArtistInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtists));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsDb));

        List<List<NetArtistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetArtistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        artistInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(artistInfos, total.get());
    }

    /**
     * 根据关键词获取电台
     */
    public static CommonResult<NetRadioInfo> searchRadios(String keyword, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> radioInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<CommonResult<NetRadioInfo>> searchRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_API, encodedKeyword, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject result = radioInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("djRadiosCount");
                JSONArray radioArray = result.getJSONArray("djRadios");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getJSONObject("dj").getString("nickname");
                    String djId = radioJson.getJSONObject("dj").getString("userId");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("programCount");
                    String category = radioJson.getString("category");
                    if (!category.isEmpty()) category += "、" + radioJson.getString("secondCategory");
                    String coverImgThumbUrl = radioJson.getString("picUrl");
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetRadioInfo>> searchRadiosXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_XM_API, encodedKeyword, page, limit))
                    .header(Header.USER_AGENT, USER_AGENT)
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data").getJSONObject("album");
            t = data.getInt("total");
            JSONArray radioArray = data.getJSONArray("docs");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("albumId");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getString("nickname");
                String djId = radioJson.getString("uid");
                String coverImgUrl = radioJson.getString("coverPath");
//                String description = radioJson.getString("intro");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("tracksCount");
                String category = radioJson.getString("categoryTitle");
                String coverImgThumbUrl = coverImgUrl;
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createdAt"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                // 喜马拉雅需要提前获取封面 url 和描述！
                radioInfo.setCoverImgUrl(coverImgUrl);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        Callable<CommonResult<NetRadioInfo>> searchRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_ME_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            t = data.getJSONObject("pagination").getInt("count");
            JSONArray radioArray = data.getJSONArray("Datas");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
//                String djId = radioJson.getString("uid");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");
                Long playCount = radioJson.getLong("view_count");
//                Integer trackCount = radioJson.getInt("tracksCount");
                String category = radioJson.getString("catalog_name");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setPlayCount(playCount);
//                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        Callable<CommonResult<NetRadioInfo>> searchRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_RADIO_DB_API, encodedKeyword, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            t = radioInfoJson.getInt("total");
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(radioArray.getString(i));
                Elements result = doc.select("div.result");
                Elements a = result.select("h3 a");
                Elements span = result.select(".title h3 span");

                String radioId = ReUtil.get("sid: (\\d+)", a.attr("onclick"), 1);
                String radioName = a.text().trim();
                String dj = result.select("span.subject-cast").text();
                String coverImgThumbUrl = result.select("div.pic img").attr("src");
                String category = ReUtil.get("\\[(.*?)\\]", span.text(), 1);

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 豆瓣图书
        Callable<CommonResult<NetRadioInfo>> searchBookRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_BOOK_RADIO_DB_API, encodedKeyword, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            t = radioInfoJson.getInt("total");
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(radioArray.getString(i));
                Elements result = doc.select("div.result");
                Elements a = result.select("h3 a");
                Elements span = result.select(".title h3 span");

                String radioId = ReUtil.get("sid: (\\d+)", a.attr("onclick"), 1);
                String radioName = a.text().trim();
                String dj = result.select("span.subject-cast").text();
                String coverImgThumbUrl = result.select("div.pic img").attr("src");
                String category = ReUtil.get("\\[(.*?)\\]", span.text(), 1);

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setBook(true);
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchRadios));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchRadiosDb));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchBookRadiosDb));

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        radioInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(radioInfos, total.get());
    }

    /**
     * 根据关键词获取 MV
     */
    public static CommonResult<NetMvInfo> searchMvs(String keyword, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> mvInfos = new LinkedList<>();
//        Set<NetMvInfo> set = Collections.synchronizedSet(new HashSet<>());

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        // MV
        Callable<CommonResult<NetMvInfo>> searchMvs = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("mvCount");
                JSONArray mvArray = result.getJSONArray("mvs");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = mvJson.getString("artistName");
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getDouble("duration") / 1000;
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

//                    if (!set.contains(mvInfo)) {
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(mvInfo);
//                    }
//                    set.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 视频
        Callable<CommonResult<NetMvInfo>> searchVideos = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_VIDEO_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("videoCount");
                JSONArray mvArray = result.getJSONArray("videos");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    Integer type = mvJson.getInt("type");
                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String creators = parseCreators(mvJson);
                    Long playCount = mvJson.getLong("playTime");
                    Double duration = mvJson.getDouble("durationms") / 1000;
                    String coverImgUrl = mvJson.getString("coverUrl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    // 网易云视频和 MV 分开了
                    mvInfo.setType(type == 1 ? MvInfoType.VIDEO : MvInfoType.MV);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(creators);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetMvInfo>> searchMvsKg = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("hash");
                // 酷狗返回的名称含有 HTML 标签，需要去除
                String mvName = StringUtils.removeHTMLLabel(mvJson.getString("filename"));
                String artistName = StringUtils.removeHTMLLabel(mvJson.getString("singername"));
                Long playCount = mvJson.getLong("historyheat");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publishdate").split(" ")[0];
                String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetMvInfo>> searchMvsQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 4))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray mvArray = data.getJSONObject("body").getJSONObject("mv").getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("v_id");
                String mvName = mvJson.getString("mv_name");
                String artistName = mvJson.getString("singer_name");
                Long playCount = mvJson.getLong("play_count");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publish_date");
                String coverImgUrl = mvJson.getString("mv_pic_url").replaceFirst("http:", "https:");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetMvInfo>> searchMvsKw = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_KW_API, encodedKeyword, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = mvJson.getString("artist");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");
                    String coverImgUrl = mvJson.getString("pic");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 好看
        Callable<CommonResult<NetMvInfo>> searchMvsHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_HK_API, encodedKeyword, page, limit))
                    .header(Header.COOKIE, HK_COOKIE)
                    .execute();
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = page * limit;
            if (data.getInt("has_more") == 1) t++;
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                Long playCount = StringUtils.antiFormatNumber(mvJson.getString("read_num").replaceFirst("次播放", ""));
                Double duration = TimeUtils.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publishTimeText");
                String coverImgUrl = mvJson.getString("cover_src");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchMvs));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchVideos));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsHk));

        List<List<NetMvInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMvInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        mvInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(mvInfos, total.get());
    }

    /**
     * 获取所有榜单
     */
    public static CommonResult<NetRankingInfo> getRankings() {
        AtomicInteger total = new AtomicInteger();
        List<NetRankingInfo> rankingInfos = new LinkedList<>();

        // 网易云
        Callable<CommonResult<NetRankingInfo>> getRankings = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("list");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("name");
                String coverImgUrl = rankingJson.getString("coverImgUrl");
                String description = rankingJson.getString("description");
                Long playCount = rankingJson.getLong("playCount");
                String updateFre = rankingJson.getString("updateFrequency");
                String updateTime = TimeUtils.msToDate(rankingJson.getLong("trackUpdateTime"));

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description.equals("null") ? "" : description);
                rankingInfo.setPlayCount(playCount);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setUpdateTime(updateTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetRankingInfo>> getRankingsKg = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_KG_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONObject("data").getJSONArray("info");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("rankid");
                String rankingName = rankingJson.getString("rankname");
                String coverImgUrl = rankingJson.getString("banner_9").replace("/{size}", "");
                String description = rankingJson.getString("intro");
                String updateFre = rankingJson.getString("update_frequency");
                Long playCount = rankingJson.getLong("play_times");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.KG);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetRankingInfo>> getRankingsQq = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_QQ_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONArray rankingArray = data.getJSONObject(i).getJSONArray("list");
                for (int j = 0, s = rankingArray.size(); j < s; j++) {
                    JSONObject rankingJson = rankingArray.getJSONObject(j);

                    String rankingId = rankingJson.getString("topId");
                    String rankingName = rankingJson.getString("label");
                    String coverImgUrl = rankingJson.getString("picUrl").replaceFirst("http:", "https:");
                    Long playCount = rankingJson.getLong("listenNum");
                    String updateTime = rankingJson.getString("updateTime");

                    NetRankingInfo rankingInfo = new NetRankingInfo();
                    rankingInfo.setSource(NetMusicSource.QQ);
                    rankingInfo.setId(rankingId);
                    rankingInfo.setName(rankingName);
                    rankingInfo.setCoverImgUrl(coverImgUrl);
                    rankingInfo.setPlayCount(playCount);
                    rankingInfo.setUpdateTime(updateTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                        rankingInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(rankingInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsQq2 = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_QQ_API_2))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONObject("data").getJSONArray("topList");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject rankingJson = data.getJSONObject(i);

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("topTitle");
                String coverImgUrl = rankingJson.getString("picUrl").replaceFirst("http:", "https:");
                Long playCount = rankingJson.getLong("listenCount");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.QQ);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetRankingInfo>> getRankingsKw = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(GET_RANKING_KW_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String rankingInfoBody = resp.body();
                JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
                JSONArray data = rankingInfoJson.getJSONArray("data");
                for (int i = 0, len = data.size(); i < len; i++) {
                    JSONArray rankingArray = data.getJSONObject(i).getJSONArray("list");
                    for (int j = 0, s = rankingArray.size(); j < s; j++) {
                        JSONObject rankingJson = rankingArray.getJSONObject(j);

                        String rankingId = rankingJson.getString("sourceid");
                        String rankingName = rankingJson.getString("name");
                        String coverImgUrl = rankingJson.getString("pic");
                        String description = rankingJson.getString("intro");
                        String updateFre = rankingJson.getString("pub");

                        NetRankingInfo rankingInfo = new NetRankingInfo();
                        rankingInfo.setSource(NetMusicSource.KW);
                        rankingInfo.setId(rankingId);
                        rankingInfo.setName(rankingName);
                        rankingInfo.setCoverImgUrl(coverImgUrl);
                        rankingInfo.setUpdateFre(updateFre);
                        rankingInfo.setDescription(description);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                            rankingInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(rankingInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsKw2 = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(GET_RANKING_KW_API_2)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String rankingInfoBody = resp.body();
                JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
                JSONArray data = rankingInfoJson.getJSONArray("child");
                for (int i = 0, len = data.size(); i < len; i++) {
                    JSONObject rankingJson = data.getJSONObject(i);

                    String rankingId = rankingJson.getString("sourceid");
                    String rankingName = rankingJson.getString("name");
                    String coverImgUrl = rankingJson.getString("pic");
                    String updateTime = rankingJson.getString("info").replaceFirst("更新于", "");

                    NetRankingInfo rankingInfo = new NetRankingInfo();
                    rankingInfo.setSource(NetMusicSource.KW);
                    rankingInfo.setId(rankingId);
                    rankingInfo.setName(rankingName);
                    rankingInfo.setCoverImgUrl(coverImgUrl);
                    rankingInfo.setUpdateTime(updateTime);
                    rankingInfo.setDescription("");
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                        rankingInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(rankingInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetRankingInfo>> getRankingsMg = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_MG_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            JSONArray contentItemList = data.getJSONArray("contentItemList");
            for (int i = 0, len = contentItemList.size(); i < len; i++) {
                JSONArray itemList = contentItemList.getJSONObject(i).optJSONArray("itemList");
                if (itemList != null) {
                    for (int j = 0, s = itemList.size(); j < s; j++) {
                        JSONObject item = itemList.getJSONObject(j);

                        String template = item.getString("template");
                        if (template.equals("row1") || template.equals("grid1")) {
                            JSONObject param = item.getJSONObject("displayLogId").getJSONObject("param");

                            String rankingId = param.getString("rankId");
                            String rankingName = param.getString("rankName");
                            String coverImgUrl = item.getString("imageUrl");
                            String updateFre = item.getJSONArray("barList").getJSONObject(0).getString("title");

                            NetRankingInfo rankingInfo = new NetRankingInfo();
                            rankingInfo.setSource(NetMusicSource.MG);
                            rankingInfo.setId(rankingId);
                            rankingInfo.setName(rankingName);
                            rankingInfo.setUpdateFre(updateFre);
                            rankingInfo.setCoverImgUrl(coverImgUrl);
//                        rankingInfo.setPlayCount(playCount);
//                        rankingInfo.setUpdateTime(updateTime);
                            GlobalExecutors.imageExecutor.execute(() -> {
                                BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                                rankingInfo.setCoverImgThumb(coverImgThumb);
                            });

                            res.add(rankingInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetRankingInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getRankings));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq2));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw2));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsMg));

        List<List<NetRankingInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRankingInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        rankingInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(rankingInfos, total.get());
    }

    /**
     * 根据关键词获取用户
     */
    public static CommonResult<NetUserInfo> searchUsers(String keyword, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetUserInfo> userInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtils.encode(keyword);

        // 网易云
        Callable<CommonResult<NetUserInfo>> searchUsers = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_API, encodedKeyword, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject result = userInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("userprofileCount");
                JSONArray userArray = result.getJSONArray("userprofiles");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("userId");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getInt("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                    String sign = userJson.getString("signature");
                    String avatarThumbUrl = userJson.getString("avatarUrl");
                    Integer follow = userJson.getInt("follows");
                    Integer followed = userJson.getInt("followeds");
                    Integer playlistCount = userJson.getInt("playlistCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setAvatarUrl(avatarThumbUrl);
//                    userInfo.setSign(sign);
                    userInfo.setFollow(follow);
                    userInfo.setFollowed(followed);
                    userInfo.setPlaylistCount(playlistCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetUserInfo>> searchUsersQq = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.post(String.format(qqSearchApi))
                    .body(String.format(qqSearchJson, page, limit, keyword, 8))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray userArray = data.getJSONObject("body").getJSONObject("user").getJSONArray("list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("encrypt_uin");
                String userName = userJson.getString("title");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("pic");
                Integer followed = userJson.getInt("fans_num");
                Integer playlistCount = userJson.getInt("diss_num");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.QQ);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetUserInfo>> searchUsersXm = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_XM_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject result = userInfoJson.getJSONObject("data").optJSONObject("user");
            if (result != null) {
                t = result.getInt("total");
                JSONArray userArray = result.getJSONArray("docs");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("uid");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getInt("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                    String avatarThumbUrl = userJson.getString("logoPic");
                    int in = avatarThumbUrl.lastIndexOf('!');
                    if (in > 0) avatarThumbUrl = avatarThumbUrl.substring(0, in);
                    Integer follow = userJson.getInt("followingsCount");
                    Integer followed = userJson.getInt("followersCount");
                    Integer radioCount = userJson.getInt("albumCount");
                    Integer programCount = userJson.getInt("tracksCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.XM);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setAvatarUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFollowed(followed);
                    userInfo.setRadioCount(radioCount);
                    userInfo.setProgramCount(programCount);

                    String finalAvatarThumbUrl = avatarThumbUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        Callable<CommonResult<NetUserInfo>> searchUsersMe = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_ME_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject info = userInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getInt("count");
            JSONArray userArray = info.getJSONArray("Datas");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                // 猫耳没有获取用户信息的接口！
                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar2");
                String avatarUrl = avatarThumbUrl;
//                String bgImgUrl = userJson.getString("coverurl2");
                Integer follow = userJson.getInt("follownum");
                Integer followed = userJson.getInt("fansnum");
//                Integer radioCount = userJson.getInt("albumnum");
                Integer programCount = userJson.getInt("soundnum");
//                String sign = userJson.getString("userintro");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
//                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);
//                userInfo.setBgImgUrl(bgImgUrl);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        Callable<CommonResult<NetUserInfo>> searchUsersDb = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SEARCH_USER_DB_API, encodedKeyword, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            t = radioInfoJson.getInt("total");
            JSONArray radioArray = radioInfoJson.getJSONArray("items");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                Document doc = Jsoup.parse(radioArray.getString(i));
                Elements result = doc.select("div.result");
                Elements a = result.select("h3 a");
                Elements info = result.select(".title .info");
                Elements img = result.select("div.pic img");

                String userId = ReUtil.get("sid: (\\d+)", a.attr("onclick"), 1);
                String userName = a.text().trim();
                String gender = "保密";
                String src = img.attr("src");
                String avatarThumbUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst("/up", "/ul");
                String avatarUrl = avatarThumbUrl;
                Integer followed = Integer.parseInt(ReUtil.get("(\\d+)人关注", info.text(), 1));

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DB);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setFollowed(followed);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });

                res.add(userInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 堆糖
        Callable<CommonResult<NetUserInfo>> searchUsersDt = () -> {
            LinkedList<NetUserInfo> res = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_DT_API, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis()))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray userArray = data.getJSONArray("object_list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");
                String avatarUrl = avatarThumbUrl;
                Integer follow = userJson.getInt("followCount");
                Integer followed = userJson.getInt("beFollowCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DT);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetUserInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsers));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersXm));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersDb));
        taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersDt));

        List<List<NetUserInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetUserInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        userInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(userInfos, total.get());
    }

    /**
     * 获取 歌曲 / 歌单 / 专辑 / MV 评论
     */
    public static CommonResult<NetCommentInfo> getComments(Object info, String type, int limit, int page) throws IOException {
        int total = 0;
        List<NetCommentInfo> commentInfos = new LinkedList<>();

        String id = null;
        String[] typeStr = null;
        Integer source = 0;
        boolean hotOnly = "热门评论".equals(type);

        boolean isRadio = false, isBook = false;

        if (info instanceof NetMusicInfo) {
            NetMusicInfo netMusicInfo = (NetMusicInfo) info;
            // 网易云需要先判断是普通歌曲还是电台节目，酷狗歌曲获取评论需要 hash
            boolean hasProgramId = netMusicInfo.hasProgramId();
            boolean hasHash = netMusicInfo.hasHash();
            id = hasProgramId ? netMusicInfo.getProgramId() : hasHash ? netMusicInfo.getHash() : netMusicInfo.getId();
            source = netMusicInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{hotOnly ? (hasProgramId ? "4" : "0") : (hasProgramId ? "dj" : "music"), "1", "15", "1"};

            if (source == NetMusicSource.QQ) {
                // QQ 需要先通过 mid 获取 id
                String songInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, netMusicInfo.getId()))
                        .execute()
                        .body();
                JSONObject trackInfo = JSONObject.fromObject(songInfoBody).getJSONObject("data").getJSONObject("track_info");
                id = trackInfo.getString("id");
            }
        } else if (info instanceof NetPlaylistInfo) {
            NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) info;
            id = netPlaylistInfo.getId();
            source = netPlaylistInfo.getSource();
            // 网易 QQ 酷我 猫耳
            typeStr = new String[]{hotOnly ? "2" : "playlist", "3", "8", "2"};
        } else if (info instanceof NetAlbumInfo) {
            NetAlbumInfo netAlbumInfo = (NetAlbumInfo) info;
            id = netAlbumInfo.getId();
            source = netAlbumInfo.getSource();
            typeStr = new String[]{hotOnly ? "3" : "album", "2", ""};

            if (source == NetMusicSource.QQ) {
                // QQ 需要先通过 mid 获取 id
                String songInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, netAlbumInfo.getId()))
                        .execute()
                        .body();
                id = JSONObject.fromObject(songInfoBody).getJSONObject("data").getString("id");
            }
        } else if (info instanceof NetRadioInfo) {
            NetRadioInfo netRadioInfo = (NetRadioInfo) info;
            id = netRadioInfo.getId();
            source = netRadioInfo.getSource();
            isRadio = true;
            isBook = netRadioInfo.isBook();
            typeStr = new String[]{"7", "", "", ""};
        } else if (info instanceof NetMvInfo) {
            NetMvInfo netMvInfo = (NetMvInfo) info;
            // 网易云需要判断是视频还是 MV 还是 Mlog
            boolean isVideo = netMvInfo.isVideo();
            boolean isMlog = netMvInfo.isMlog();

            id = netMvInfo.getId();
            source = netMvInfo.getSource();

            // Mlog 需要先获取视频 id，并转为视频类型
            if (isMlog) {
                String body = HttpRequest.get(String.format(MLOG_TO_VIDEO_API, id))
                        .execute()
                        .body();
                id = JSONObject.fromObject(body).getString("data");
                netMvInfo.setId(id);
                netMvInfo.setType(MvInfoType.VIDEO);
            }

            typeStr = new String[]{hotOnly ? (isVideo || isMlog ? "5" : "1") : (isVideo || isMlog ? "video" : "mv"), "5", "7"};
        } else if (info instanceof NetRankingInfo) {
            NetRankingInfo netRankingInfo = (NetRankingInfo) info;
            id = netRankingInfo.getId();
            source = netRankingInfo.getSource();
            typeStr = new String[]{hotOnly ? "2" : "playlist", "4", "2"};
        }

        // 网易云
        if (source == NetMusicSource.NET_CLOUD && StringUtils.isNotEmpty(typeStr[0])) {
            boolean newInterface = !hotOnly && isRadio;
            String url = hotOnly ? GET_HOT_COMMENTS_API : isRadio ? NEW_GET_COMMENTS_API : OLD_GET_COMMENTS_API;
            String commentInfoBody = HttpRequest.get(String.format(url, typeStr[0], id, isRadio ? page : (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject commentInfoJson = newInterface ? JSONObject.fromObject(commentInfoBody).getJSONObject("data") : JSONObject.fromObject(commentInfoBody);
            total = commentInfoJson.getInt(newInterface ? "totalCount" : "total");
            JSONArray commentArray = hotOnly ? commentInfoJson.getJSONArray("hotComments") : commentInfoJson.getJSONArray("comments");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);
                JSONObject user = commentJson.getJSONObject("user");

                String userId = user.getString("userId");
                String username = user.getString("nickname");
                String profileUrl = user.getString("avatarUrl");
                String content = commentJson.getString("content");
                String time = TimeUtils.msToPhrase(commentJson.getLong("time"));
                Integer likedCount = commentJson.getInt("likedCount");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLikedCount(likedCount);

                NetCommentInfo finalCommentInfo = commentInfo;
                String finalProfileUrl = profileUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = extractProfile(finalProfileUrl);
                    finalCommentInfo.setProfile(profile);
                });

                commentInfos.add(commentInfo);

                // 被回复的评论
                JSONArray beReplied = commentJson.optJSONArray("beReplied");
                if (beReplied == null) continue;
                for (int j = 0, l = beReplied.size(); j < l; j++) {
                    commentJson = beReplied.getJSONObject(j);

                    user = commentJson.getJSONObject("user");
                    userId = user.getString("userId");
                    username = user.getString("nickname");
                    profileUrl = user.getString("avatarUrl");
                    content = commentJson.getString("content");

                    commentInfo = new NetCommentInfo();
                    commentInfo.setBeReplied(true);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);

                    NetCommentInfo finalCommentInfo1 = commentInfo;
                    String finalProfileUrl1 = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = extractProfile(finalProfileUrl1);
                        finalCommentInfo1.setProfile(profile);
                    });

                    commentInfos.add(commentInfo);
                }
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG && info instanceof NetMusicInfo) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_KG_API, id, page, limit))
                    // 注意此处必须加 header 才能请求到正确的数据！
                    .header(Header.USER_AGENT, "Android712-AndroidPhone-8983-18-0-COMMENT-wifi")
                    .execute()
                    .body();
            JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
            JSONArray commentArray = hotOnly ? commentInfoJson.optJSONArray("weightList") : commentInfoJson.optJSONArray("list");
            total = hotOnly ? (commentArray != null ? commentArray.size() : 0) : commentInfoJson.getInt("count");
            if (commentArray != null) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = commentJson.getString("user_name");
                    String profileUrl = commentJson.getString("user_pic");
                    String content = commentJson.optString("content");
                    String time = TimeUtils.strToPhrase(commentJson.getString("addtime"));
                    Integer likedCount = commentJson.getJSONObject("like").getInt("likenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.KG);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    commentInfos.add(commentInfo);
                }
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ && StringUtils.isNotEmpty(typeStr[1])) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_QQ_API, hotOnly ? 1 : 0, typeStr[1], id, page, limit))
                    .execute()
                    .body();
            JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            JSONObject commentJson = data.getJSONObject("comment");
            total = commentJson.getInt("commenttotal");
            JSONArray commentArray = commentJson.optJSONArray("commentlist");
            if (commentArray != null) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject cj = commentArray.getJSONObject(i);

                    String userId = cj.getString("encrypt_uin");
                    String username = cj.getString("nick");
                    String profileUrl = cj.getString("avatarurl").replaceFirst("http:", "https:");
                    JSONArray middleCommentContent = cj.optJSONArray("middlecommentcontent");
                    String content;
                    JSONObject cj2 = null;
                    if (middleCommentContent != null) {
                        cj2 = middleCommentContent.getJSONObject(0);
                        content = cj2.getString("subcommentcontent").replace("\\n", "\n");
                    } else
                        content = cj.optString("rootcommentcontent").replace("\\n", "\n");
                    // 评论可能已被删除
                    if (content == null) content = "该评论已被删除";
                    String time = TimeUtils.msToPhrase(cj.getLong("time") * 1000);
                    Integer likedCount = cj.getInt("praisenum");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.QQ);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    commentInfos.add(commentInfo);

                    // 被回复的评论
                    if (middleCommentContent != null) {
                        String uId = cj2.optString("encrypt_replyeduin");
                        String uname = cj.optString("rootcommentnick");
                        String cnt = cj.optString("rootcommentcontent").replace("\\n", "\n");

                        NetCommentInfo ci = new NetCommentInfo();
                        ci.setSource(NetMusicSource.QQ);
                        ci.setBeReplied(true);
                        ci.setUserId(uId);
                        ci.setUsername(StringUtils.isEmpty(uname) ? "null" : uname.substring(1));
                        ci.setContent(StringUtils.isEmpty(cnt) ? "该评论已被删除" : cnt);
                        commentInfos.add(ci);
                    }
                }
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW && StringUtils.isNotEmpty(typeStr[2])) {
            String url = hotOnly ? GET_HOT_COMMENTS_KW_API : GET_NEW_COMMENTS_KW_API;
            // 最新评论
            String commentInfoBody = HttpRequest.get(String.format(url, typeStr[2], id, page, limit))
                    .execute()
                    .body();
            JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
            JSONArray commentArray = null;
            if (!commentInfoJson.has("data")) {
                total = commentInfoJson.getInt("total");
                commentArray = commentInfoJson.getJSONArray("rows");
            }
            if (commentArray != null) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String username = StringUtils.decode(commentJson.getString("u_name"));
                    String profileUrl = commentJson.getString("u_pic");
                    String content = commentJson.getString("msg");
                    String time = TimeUtils.strToPhrase(commentJson.getString("time"));
                    Integer likedCount = commentJson.getInt("like_num");

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.KW);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    String finalProfileUrl = profileUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = extractProfile(finalProfileUrl);
                        commentInfo.setProfile(profile);
                    });

                    commentInfos.add(commentInfo);

                    // 被回复的评论
                    JSONObject reply = commentJson.optJSONObject("reply");

                    if (reply != null) {
                        username = StringUtils.decode(reply.getString("u_name"));
                        profileUrl = reply.getString("u_pic");
                        content = reply.getString("msg");
                        time = TimeUtils.strToPhrase(reply.getString("time"));
                        likedCount = reply.getInt("like_num");

                        NetCommentInfo rCommentInfo = new NetCommentInfo();
                        rCommentInfo.setSource(NetMusicSource.KW);
                        rCommentInfo.setBeReplied(true);
                        rCommentInfo.setUsername(username);
                        rCommentInfo.setProfileUrl(profileUrl);
                        rCommentInfo.setContent(content);
                        rCommentInfo.setTime(time);
                        rCommentInfo.setLikedCount(likedCount);
                        String finalProfileUrl1 = profileUrl;
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage profile = extractProfile(finalProfileUrl1);
                            rCommentInfo.setProfile(profile);
                        });

                        commentInfos.add(rCommentInfo);
                    }
                }
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            JSONArray commentArray = null;
            if (isRadio) {
                String url = hotOnly ? GET_HOT_RADIO_COMMENTS_XM_API : GET_NEW_RADIO_COMMENTS_XM_API;
                String commentInfoBody = HttpRequest.get(String.format(url, id, page, limit))
                        .execute()
                        .body();
                JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
                JSONObject data = commentInfoJson.getJSONObject("data");
                JSONObject comments = data.getJSONObject("comments");
                total = comments.getInt("totalCount");
                commentArray = comments.getJSONArray("list");
            } else if (!hotOnly) {
                String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_XM_API, id, page, limit))
                        .execute()
                        .body();
                JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
                JSONObject data = commentInfoJson.getJSONObject("data");
                total = data.getInt("totalComment");
                commentArray = data.getJSONArray("comments");
            }
            if (commentArray != null) {
                for (int i = 0, len = commentArray.size(); i < len; i++) {
                    JSONObject commentJson = commentArray.getJSONObject(i);

                    String userId = commentJson.getString("uid");
                    String username = commentJson.getString("nickname");
                    String smallHeader = commentJson.getString("smallHeader");
                    String profileUrl = isRadio ? smallHeader : "http:" + smallHeader;
                    String content = commentJson.getString("content");
                    String time = TimeUtils.msToPhrase(commentJson.getLong(isRadio ? "updatedAt" : "commentTime"));
                    Integer likedCount = commentJson.getInt("likes");
                    Integer score = commentJson.optInt("newAlbumScore", -1);

                    NetCommentInfo commentInfo = new NetCommentInfo();
                    commentInfo.setSource(NetMusicSource.XM);
                    commentInfo.setUserId(userId);
                    commentInfo.setUsername(username);
                    commentInfo.setProfileUrl(profileUrl);
                    commentInfo.setContent(content);
                    commentInfo.setTime(time);
                    commentInfo.setLikedCount(likedCount);
                    commentInfo.setScore(score);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage profile = extractProfile(profileUrl);
                        commentInfo.setProfile(profile);
                    });

                    commentInfos.add(commentInfo);
                }
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME && StringUtils.isNotEmpty(typeStr[3])) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_ME_API, typeStr[3], hotOnly ? 3 : 1, id, page, limit))
                    .execute()
                    .body();
            JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("info").getJSONObject("comment");
            total = data.getJSONObject("pagination").getInt("count");
            JSONArray commentArray = data.getJSONArray("Datas");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);

                String userId = commentJson.getString("userid");
                String username = commentJson.getString("username");
                String profileUrl = commentJson.getString("icon");
                String content = commentJson.getString("comment_content");
                String time = commentJson.getString("ctime");
                Integer likedCount = commentJson.getInt("like_num");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.ME);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLikedCount(likedCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = extractProfile(profileUrl);
                    commentInfo.setProfile(profile);
                });

                commentInfos.add(commentInfo);
            }
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String commentInfoBody = HttpRequest.get(String.format(GET_COMMENTS_HK_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject commentInfoJson = JSONObject.fromObject(commentInfoBody);
            JSONObject data = commentInfoJson.getJSONObject("data");
            total = data.getInt("comment_count");
            JSONArray commentArray = data.getJSONArray("list");
            for (int i = 0, len = commentArray.size(); i < len; i++) {
                JSONObject commentJson = commentArray.getJSONObject(i);

                String userId = commentJson.getString("appid");
                String username = commentJson.getString("uname");
                String profileUrl = commentJson.getString("avatar");
                String content = commentJson.getString("content");
                String time = TimeUtils.msToPhrase(commentJson.getLong("create_time") * 1000);
                Integer likedCount = commentJson.getInt("like_count");

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.HK);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLikedCount(likedCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = extractProfile(profileUrl);
                    commentInfo.setProfile(profile);
                });

                commentInfos.add(commentInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String url = isRadio ? isBook ? GET_BOOK_RADIO_COMMENTS_DB_API : GET_RADIO_COMMENTS_DB_API : GET_ALBUM_COMMENTS_DB_API;
            String commentInfoBody = HttpRequest.get(String.format(url, id, hotOnly ? "new_score" : "time", (page - 1) * limit, limit))
                    .setFollowRedirects(true)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(commentInfoBody);
            Elements comments = doc.select(isRadio && !isBook ? "div.comment-item" : "li.comment-item");
            String ts = ReUtil.get("\\((\\d+)\\)", doc.select("li.is-active").text(), 1);
            total = StringUtils.isNotEmpty(ts) ? Integer.parseInt(ts) : comments.size();
            for (int i = 0, len = comments.size(); i < len; i++) {
                Element comment = comments.get(i);
                Element a = comment.select("span.comment-info a").first();
                Element img = comment.select("div.avatar img").first();
                Element cnt = comment.select("p.comment-content").first();
                Element t = comment.select(isBook ? "a.comment-time" : "span.comment-time").first();
                Element v = comment.select("span.vote-count").first();
                Element rating = comment.select("span.comment-info span").get(isRadio && !isBook ? 1 : 0);

                String userId = ReUtil.get("/people/(.*?)/", a.attr("href"), 1);
                String username = a.text();
                String src = img.attr("src");
                String profileUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst(isRadio ? "/u" : "/up", "/ul");
                String content = cnt.text();
                String time = TimeUtils.strToPhrase(t.text().trim());
                Integer likedCount = Integer.parseInt(v.text());
                String r = ReUtil.get("(\\d+) ", rating.className(), 1);
                Integer score = StringUtils.isEmpty(r) ? -1 : Integer.parseInt(r) / 10 * 2;

                NetCommentInfo commentInfo = new NetCommentInfo();
                commentInfo.setSource(NetMusicSource.DB);
                commentInfo.setUserId(userId);
                commentInfo.setUsername(username);
                commentInfo.setProfileUrl(profileUrl);
                commentInfo.setContent(content);
                commentInfo.setTime(time);
                commentInfo.setLikedCount(likedCount);
                commentInfo.setScore(score);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage profile = extractProfile(profileUrl);
                    commentInfo.setProfile(profile);
                });

                commentInfos.add(commentInfo);
            }
        }

        return new CommonResult<>(commentInfos, total);
    }

    /**
     * 获取歌曲曲谱
     */
    public static CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) throws IOException {
        int source = musicInfo.getSource();
        String id = musicInfo.getId();
        LinkedList<NetSheetInfo> sheetInfos = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NET_CLOUD) {
            String sheetInfoBody = HttpRequest.get(String.format(GET_SHEETS_API, id))
                    .execute()
                    .body();
            JSONObject sheetInfoJson = JSONObject.fromObject(sheetInfoBody);
            JSONObject data = sheetInfoJson.getJSONObject("data");
            JSONArray sheetArray = data.optJSONArray("musicSheetSimpleInfoVOS");
            if (sheetArray != null) {
                total = sheetArray.size();
                for (int i = 0, len = sheetArray.size(); i < len; i++) {
                    JSONObject sheetJson = sheetArray.getJSONObject(i);

                    String sheetId = sheetJson.getString("id");
                    String name = sheetJson.getString("name");
                    String coverImgUrl = sheetJson.getString("coverImageUrl");
                    String difficulty = sheetJson.getString("difficulty");
                    String musicKey = sheetJson.getString("musicKey");
                    String playVersion = sheetJson.getString("playVersion");
                    String chordName = sheetJson.getString("chordName");
                    Integer pageSize = sheetJson.getInt("totalPageSize");
                    Integer bpm = sheetJson.getInt("bpm");

                    NetSheetInfo sheetInfo = new NetSheetInfo();
                    sheetInfo.setId(sheetId);
                    sheetInfo.setName(name);
                    sheetInfo.setDifficulty(difficulty);
                    sheetInfo.setMusicKey(musicKey);
                    sheetInfo.setPlayVersion(playVersion);
                    sheetInfo.setChordName(chordName);
                    sheetInfo.setPageSize(pageSize);
                    sheetInfo.setBpm(bpm);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImg = extractProfile(coverImgUrl);
                        sheetInfo.setCoverImg(coverImg);
                    });

                    sheetInfos.add(sheetInfo);
                }
            }
        }

        return new CommonResult<>(sheetInfos, total);
    }

    /**
     * 获取曲谱图片链接
     */
    public static CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        int source = sheetInfo.getSource();
        String id = sheetInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;

        if (source == NetMusicSource.NET_CLOUD) {
            String imgInfoBody = HttpRequest.get(String.format(GET_SHEETS_IMG_API, id))
                    .execute()
                    .body();
            JSONObject imgInfoJson = JSONObject.fromObject(imgInfoBody);
            JSONArray imgArray = imgInfoJson.getJSONArray("data");
            total = imgArray.size();
            for (int i = 0, len = imgArray.size(); i < len; i++) {
                JSONObject imgJson = imgArray.getJSONObject(i);
                imgUrls.add(imgJson.getString("url"));
            }
        }

        return new CommonResult<>(imgUrls, total);
    }

    /**
     * 获取专辑照片链接
     */
    public static CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        int source = albumInfo.getSource();
        String id = albumInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        cursor = StringUtils.encode(cursor);
        Integer total = 0;

        if (source == NetMusicSource.DT) {
            String imgInfoBody = HttpRequest.get(String.format(GET_ALBUMS_IMG_DT_API, id, cursor, limit, System.currentTimeMillis()))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(imgInfoBody).getJSONObject("data");
            JSONArray imgs = data.getJSONArray("object_list");
            cursor = data.getString("after");
            total = page * limit;
            if (data.getInt("more") == 1) total++;
            else total = (page - 1) * limit + imgs.size();
            for (int i = 0, len = imgs.size(); i < len; i++) {
                JSONObject img = imgs.getJSONObject(i);
                imgUrls.add(img.getJSONObject("photo").getString("path"));
            }
        }

        return new CommonResult<>(imgUrls, total, cursor);
    }

    /**
     * 获取歌手照片链接
     */
    public static CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        int source = artistInfo.getSource();
        String id = artistInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB) {
            String imgInfoBody = HttpRequest.get(String.format(GET_ARTISTS_IMG_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
            String t = ReUtil.get("共(\\d+)张", doc.select("span.count").text(), 1);
            total = StringUtils.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                imgUrls.add(url);
            }
        }

        return new CommonResult<>(imgUrls, total);
    }

    /**
     * 获取电台照片链接
     */
    public static CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB && !isBook) {
            String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_IMG_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
            String t = ReUtil.get("共(\\d+)张", doc.select("span.count").text(), 1);
            total = StringUtils.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                imgUrls.add(url);
            }
        }

        return new CommonResult<>(imgUrls, total);
    }

    /**
     * 获取电台海报链接
     */
    public static CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB && !isBook) {
            String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_POSTER_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
            String t = ReUtil.get("共(\\d+)张", doc.select("span.count").text(), 1);
            total = StringUtils.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                imgUrls.add(url);
            }
        }

        return new CommonResult<>(imgUrls, total);
    }

    /**
     * 提取头像
     *
     * @param imgUrl
     * @return
     */
    private static BufferedImage extractProfile(String imgUrl) {
        return ImageUtils.setRadius(ImageUtils.width(imgUrl, ImageConstants.profileWidth), 0.1);
    }

    /**
     * 提取 MV 封面
     *
     * @param imgUrl
     * @return
     */
    private static BufferedImage extractMvCover(String imgUrl) {
        BufferedImage img = ImageUtils.width(imgUrl, ImageConstants.mvCoverWidth);
        if (img == null) return null;
        // 控制 MV 封面高度不超过阈值
        if (img.getHeight() > ImageConstants.mvCoverMaxHeight)
            img = ImageUtils.height(img, ImageConstants.mvCoverMaxHeight);
        return ImageUtils.setRadius(img, 0.1);
    }

    /**
     * 获取推荐歌单
     */
    public static CommonResult<NetPlaylistInfo> getRecommendPlaylists(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.recPlaylistTag.get(tag);

        // 网易云(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(RECOMMEND_PLAYLIST_API)
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("result");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("picUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        // 每页固定 30 条的推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_KG_API, page))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("plist").getJSONObject("list");
            t = data.getInt("total");
            JSONArray playlistArray = data.getJSONArray("info");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("username");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.optInt("songcount", -1);
                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(推荐)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_CAT_PLAYLIST_KG_API, s[0].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = 20 * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long pc = playlistJson.optLong("total_play_count");
                    Long playCount = pc != 0 ? pc : (long) (Double.parseDouble(playlistJson.getString("total_play_count").replace("万", "")) * 10000);
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getNewTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_CAT_PLAYLIST_KG_API, s[0].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = 20 * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long pc = playlistJson.optLong("total_play_count");
                    Long playCount = pc != 0 ? pc : (long) (Double.parseDouble(playlistJson.getString("total_play_count").replace("万", "")) * 10000);
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 每日推荐(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsQqDaily = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(DAILY_RECOMMEND_PLAYLIST_QQ_API)
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("content_id");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("username");
                Long playCount = playlistJson.getLong("listen_num");
                String coverImgThumbUrl = playlistJson.getString("cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QQ);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(程序分页)
//        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsQq = () -> {
//            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            if (StringUtils.isNotEmpty(s[0])) {
//                String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_QQ_API, s[0]))
//                        .execute()
//                        .body();
//                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
//                t = playlistArray.size();
//                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
//                    JSONObject playlistJson = playlistArray.getJSONObject(i);
//
//                    String playlistId = playlistJson.getString("tid");
//                    String playlistName = playlistJson.getString("title");
//                    String creator = playlistJson.getJSONObject("creator_info").getString("nick");
//                    Long playCount = playlistJson.getLong("access_num");
//                    Integer trackCount = playlistJson.getJSONArray("song_ids").size();
//                    String coverImgThumbUrl = playlistJson.getString("cover_url_big");
//
//                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
//                    playlistInfo.setSource(NetMusicSource.QQ);
//                    playlistInfo.setId(playlistId);
//                    playlistInfo.setName(playlistName);
//                    playlistInfo.setCreator(creator);
//                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    playlistInfo.setPlayCount(playCount);
//                    playlistInfo.setTrackCount(trackCount);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
//                        playlistInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(playlistInfo);
//                }
//            }
//            return new CommonResult<>(res, t);
//        };
        // 分类推荐歌单(最新)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String cat = s[1];
                boolean isAll = "10000000".equals(cat);
                String url;
                if (isAll) {
                    url = NEW_PLAYLIST_QQ_API + StringUtils.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_playlist_by_tag\"," +
                                    "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":2,\"cur_page\":%s}," +
                                    "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
                } else {
                    url = NEW_PLAYLIST_QQ_API + StringUtils.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_category_content\"," +
                                    "\"param\":{" +
                                    "\"titleid\":%s," +
                                    "\"caller\":\"0\"," +
                                    "\"category_id\":%s," +
                                    "\"size\":%s," +
                                    "\"page\":%s," +
                                    "\"use_page\":1}," +
                                    "\"module\":\"playlist.PlayListCategoryServer\"}}", cat, cat, limit, page - 1));
                }
                String playlistInfoBody = HttpRequest.get(url)
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                if (isAll) {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray playlistArray = data.getJSONArray("v_playlist");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator_info").getString("nick");
                        Long playCount = playlistJson.getLong("access_num");
                        String coverImgThumbUrl = playlistJson.getString("cover_url_small");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                } else {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data").getJSONObject("content");
                    t = data.getInt("total_cnt");
                    JSONArray playlistArray = data.getJSONArray("v_item");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("basic");

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator").getString("nick");
                        Long playCount = playlistJson.getLong("play_cnt");
                        String coverImgThumbUrl = playlistJson.getJSONObject("cover").getString("small_url");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        // 推荐歌单(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(RECOMMEND_PLAYLIST_KW_API).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(最新)(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(NEW_PLAYLIST_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕(接口分页)
        // 推荐歌单(每页固定 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
            t = data.getInt("countSize");
            JSONArray playlistArray = data.getJSONArray("playlist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playListId");
                String playlistName = playlistJson.getString("playListName");
                String creator = playlistJson.getString("createName");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("contentCount");
                String coverImgThumbUrl = playlistJson.getString("image");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 最新歌单(每页固定 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_MG_API, (page - 1) * 10))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
            t = data.getInt("countSize");
            JSONArray playlistArray = data.getJSONArray("playlist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playListId");
                String playlistName = playlistJson.getString("playListName");
                String creator = playlistJson.getString("createName");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("contentCount");
                String coverImgThumbUrl = playlistJson.getString("image");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecPlaylistsQi = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(buildQianUrl(String.format(REC_PLAYLIST_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONArray("data").getJSONObject(5);
            t = data.getInt("module_nums");
            JSONArray playlistArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("pic");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QI);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 推荐歌单
        Callable<CommonResult<NetPlaylistInfo>> getRecPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(REC_PLAYLIST_ME_API))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject info = playlistInfoJson.getJSONObject("info");
            JSONArray playlistArray = info.getJSONArray("albums");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                String creator = playlistJson.getString("username");
                String creatorId = playlistJson.getString("user_id");
                Integer trackCount = playlistJson.getInt("music_count");
                Long playCount = playlistJson.getLong("view_count");
                String coverImgThumbUrl = playlistJson.getString("front_cover");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.ME);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setTrackCount(trackCount);
                playlistInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌单(最新)
        Callable<CommonResult<NetPlaylistInfo>> getNewPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_PLAYLIST_ME_API, s[2].trim(), page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getJSONObject("pagination").getInt("count");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("albums");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Integer trackCount = playlistJson.getInt("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylists));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendTagPlaylistsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewTagPlaylistsKg));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsQqDaily));
//        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsQq));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsKw));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsKw));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsMg));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsMg));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecPlaylistsQi));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecPlaylistsMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewPlaylistsMe));

        List<List<NetPlaylistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetPlaylistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        playlistInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(playlistInfos, total.get());
    }

    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public static CommonResult<NetPlaylistInfo> getHighQualityPlaylists(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetPlaylistInfo> playlistInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.playlistTag.get(tag);

        // 网易云
        // 精品歌单(程序分页)
        Callable<CommonResult<NetPlaylistInfo>> getHighQualityPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String playlistInfoBody = HttpRequest.get(String.format(HIGH_QUALITY_PLAYLIST_API, s[0]))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("nickname");
                    String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 网友精选碟(最热)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getHotPickedPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_PICKED_PLAYLIST_API, s[1], limit, (page - 1) * limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getInt("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("nickname");
                    String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 网友精选碟(最新)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getNewPickedPlaylists = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String playlistInfoBody = HttpRequest.get(String.format(NEW_PICKED_PLAYLIST_API, s[1], limit, (page - 1) * limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getInt("total");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getJSONObject("creator").getString("nickname");
                    String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        // 推荐歌单(最热)
        Callable<CommonResult<NetPlaylistInfo>> getTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = 20 * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long pc = playlistJson.optLong("total_play_count");
                    Long playCount = pc != 0 ? pc : (long) (Double.parseDouble(playlistJson.getString("total_play_count").replace("万", "")) * 10000);
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(热藏)
        Callable<CommonResult<NetPlaylistInfo>> getHotCollectedTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(HOT_COLLECTED_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = 20 * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long pc = playlistJson.optLong("total_play_count");
                    Long playCount = pc != 0 ? pc : (long) (Double.parseDouble(playlistJson.getString("total_play_count").replace("万", "")) * 10000);
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐歌单(飙升)
        Callable<CommonResult<NetPlaylistInfo>> getUpTagPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String playlistInfoBody = HttpRequest.get(String.format(UP_CAT_PLAYLIST_KG_API, s[2].trim(), page))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = 20 * 100;
                JSONArray playlistArray = playlistInfoJson.getJSONArray("special_db");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("specialid");
                    String playlistName = playlistJson.getString("specialname");
                    String creator = playlistJson.getString("nickname");
                    Long pc = playlistJson.optLong("total_play_count");
                    Long playCount = pc != 0 ? pc : (long) (Double.parseDouble(playlistJson.getString("total_play_count").replace("万", "")) * 10000);
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 热门歌单(这个接口不分页，分开处理)
        Callable<CommonResult<NetPlaylistInfo>> getHotPlaylistsKg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(HOT_PLAYLIST_KG_API))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            JSONArray playlistArray = data.getJSONArray("list");
            t = playlistArray.size();
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("specialid");
                String playlistName = playlistJson.getString("specialname");
                String creator = playlistJson.getString("nickname");
                Long playCount = playlistJson.getLong("playcount");
                Integer trackCount = playlistJson.getInt("songcount");
                String coverImgThumbUrl = playlistJson.getString("imgurl").replace("/{size}", "");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.KG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 分类推荐歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQq = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[3])) {
                String cat = s[3];
                boolean isAll = "10000000".equals(cat);
                String url;
                if (isAll) {
                    url = CAT_PLAYLIST_QQ_API + StringUtils.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_playlist_by_tag\"," +
                                    "\"param\":{\"id\":10000000,\"sin\":%s,\"size\":%s,\"order\":5,\"cur_page\":%s}," +
                                    "\"module\":\"playlist.PlayListPlazaServer\"}}", (page - 1) * limit, limit, page));
                } else {
                    url = CAT_PLAYLIST_QQ_API + StringUtils.encode(String.format(
                            "{\"comm\":{\"cv\":1602,\"ct\":20}," +
                                    "\"playlist\":{" +
                                    "\"method\":\"get_category_content\"," +
                                    "\"param\":{" +
                                    "\"titleid\":%s," +
                                    "\"caller\":\"0\"," +
                                    "\"category_id\":%s," +
                                    "\"size\":%s," +
                                    "\"page\":%s," +
                                    "\"use_page\":1}," +
                                    "\"module\":\"playlist.PlayListCategoryServer\"}}", cat, cat, limit, page - 1));
                }
                String playlistInfoBody = HttpRequest.get(url)
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                if (isAll) {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray playlistArray = data.getJSONArray("v_playlist");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i);

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator_info").getString("nick");
                        Long playCount = playlistJson.getLong("access_num");
                        String coverImgThumbUrl = playlistJson.getString("cover_url_small");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                } else {
                    JSONObject data = playlistInfoJson.getJSONObject("playlist").getJSONObject("data").getJSONObject("content");
                    t = data.getInt("total_cnt");
                    JSONArray playlistArray = data.getJSONArray("v_item");
                    for (int i = 0, len = playlistArray.size(); i < len; i++) {
                        JSONObject playlistJson = playlistArray.getJSONObject(i).getJSONObject("basic");

                        String playlistId = playlistJson.getString("tid");
                        String playlistName = playlistJson.getString("title");
                        String creator = playlistJson.getJSONObject("creator").getString("nick");
                        Long playCount = playlistJson.getLong("play_cnt");
                        String coverImgThumbUrl = playlistJson.getJSONObject("cover").getString("small_url");

                        NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                        playlistInfo.setSource(NetMusicSource.QQ);
                        playlistInfo.setId(playlistId);
                        playlistInfo.setName(playlistName);
                        playlistInfo.setCreator(creator);
                        playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        playlistInfo.setPlayCount(playCount);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            playlistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(playlistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        // 默认歌单(热门)(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getDefaultPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(DEFAULT_PLAYLIST_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String playlistInfoBody = resp.body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("data");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("name");
                    String creator = playlistJson.getString("uname");
                    Long playCount = playlistJson.getLong("listencnt");
                    Integer trackCount = playlistJson.getInt("total");
                    String coverImgThumbUrl = playlistJson.getString("img");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.KW);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌单(接口分页)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsKw = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[4])) {
                String[] sp = s[4].split(" ");
                // 根据 digest 信息请求不同的分类歌单接口
                if ("43".equals(sp[1])) {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API_2, sp[0])).execute();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONArray playlistArray = JSONArray.fromObject(playlistInfoBody);
                        for (int i = 0, l = playlistArray.size(); i < l; i++) {
                            JSONArray list = playlistArray.getJSONObject(i).getJSONArray("list");
                            for (int j = 0, k = list.size(); j < k; j++, t++) {
                                if (t >= (page - 1) * limit && t < page * limit) {
                                    JSONObject playlistJson = list.getJSONObject(j);

                                    String playlistId = playlistJson.getString("id");
                                    String playlistName = playlistJson.getString("name");
                                    String coverImgThumbUrl = playlistJson.getString("img");

                                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                                    playlistInfo.setSource(NetMusicSource.KW);
                                    playlistInfo.setId(playlistId);
                                    playlistInfo.setName(playlistName);
                                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                                    GlobalExecutors.imageExecutor.execute(() -> {
                                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                                        playlistInfo.setCoverImgThumb(coverImgThumb);
                                    });

                                    res.add(playlistInfo);
                                }
                            }
                        }
                    }
                } else {
                    HttpResponse resp = HttpRequest.get(String.format(CAT_PLAYLIST_KW_API, sp[0], page, limit)).execute();
                    if (resp.getStatus() == HttpStatus.HTTP_OK) {
                        String playlistInfoBody = resp.body();
                        JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                        JSONObject data = playlistInfoJson.getJSONObject("data");
                        t = data.getInt("total");
                        JSONArray playlistArray = data.getJSONArray("data");
                        for (int i = 0, len = playlistArray.size(); i < len; i++) {
                            JSONObject playlistJson = playlistArray.getJSONObject(i);

                            String playlistId = playlistJson.getString("id");
                            String playlistName = playlistJson.getString("name");
                            String creator = playlistJson.getString("uname");
                            Long playCount = playlistJson.getLong("listencnt");
                            Integer trackCount = playlistJson.getInt("total");
                            String coverImgThumbUrl = playlistJson.getString("img");

                            NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                            playlistInfo.setSource(NetMusicSource.KW);
                            playlistInfo.setId(playlistId);
                            playlistInfo.setName(playlistName);
                            playlistInfo.setCreator(creator);
                            playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                            playlistInfo.setPlayCount(playCount);
                            playlistInfo.setTrackCount(trackCount);
                            GlobalExecutors.imageExecutor.execute(() -> {
                                BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                                playlistInfo.setCoverImgThumb(coverImgThumb);
                            });

                            res.add(playlistInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 推荐歌单(每页固定 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getRecommendPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            String playlistInfoBody = HttpRequest.get(String.format(RECOMMEND_PLAYLIST_MG_API, (page - 1) * 10))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("retMsg");
            t = data.getInt("countSize");
            JSONArray playlistArray = data.getJSONArray("playlist");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("playListId");
                String playlistName = playlistJson.getString("playListName");
                String creator = playlistJson.getString("createName");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("contentCount");
                String coverImgThumbUrl = playlistJson.getString("image");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.MG);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌单(每页 10 条)
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsMg = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[5])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_MG_API, s[5], (page - 1) * 10))
                        .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                        .header(Header.REFERER, "https://m.music.migu.cn/")
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("retMsg");
                t = data.getInt("countSize");
                JSONArray playlistArray = data.getJSONArray("playlist");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("playListId");
                    String playlistName = playlistJson.getString("playListName");
                    String creator = playlistJson.getString("createName");
                    Long playCount = playlistJson.getLong("playCount");
                    Integer trackCount = playlistJson.getInt("contentCount");
                    String coverImgThumbUrl = playlistJson.getString("image");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.MG);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator("null".equals(creator) ? "" : creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsQi = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[6])) {
                String playlistInfoBody = HttpRequest.get(buildQianUrl(String.format(CAT_PLAYLIST_QI_API, page, limit, s[6].trim(), System.currentTimeMillis())))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray playlistArray = data.getJSONArray("result");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    Integer trackCount = playlistJson.getInt("trackCount");
                    String coverImgThumbUrl = playlistJson.getString("pic");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QI);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 分类歌单
        Callable<CommonResult<NetPlaylistInfo>> getCatPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[7])) {
                String playlistInfoBody = HttpRequest.get(String.format(CAT_PLAYLIST_ME_API, s[7].trim(), page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                t = playlistInfoJson.getJSONObject("pagination").getInt("count");
                JSONArray playlistArray = playlistInfoJson.getJSONArray("albums");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Integer trackCount = playlistJson.getInt("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 探索歌单
        Callable<CommonResult<NetPlaylistInfo>> getExpPlaylistsMe = () -> {
            LinkedList<NetPlaylistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[8])) {
                String playlistInfoBody = HttpRequest.get(String.format(EXP_PLAYLIST_ME_API, s[8].trim(), page, limit))
                        .execute()
                        .body();
                JSONArray playlistArray = JSONArray.fromObject(playlistInfoBody);
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("id");
                    String playlistName = playlistJson.getString("title");
                    String creator = playlistJson.getString("username");
                    String creatorId = playlistJson.getString("user_id");
                    Long playCount = playlistJson.getLong("view_count");
                    Integer trackCount = playlistJson.getInt("music_count");
                    String coverImgThumbUrl = playlistJson.getString("front_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.ME);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCreatorId(creatorId);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setTrackCount(trackCount);
                    playlistInfo.setPlayCount(playCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(playlistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        taskList.add(GlobalExecutors.requestExecutor.submit(getHighQualityPlaylists));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotPickedPlaylists));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewPickedPlaylists));

        taskList.add(GlobalExecutors.requestExecutor.submit(getTagPlaylistsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotCollectedTagPlaylistsKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getUpTagPlaylistsKg));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotPlaylistsKg));

        taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsQq));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getDefaultPlaylistsKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsKw));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPlaylistsMg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsMg));

        taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsQi));

        taskList.add(GlobalExecutors.requestExecutor.submit(getCatPlaylistsMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(getExpPlaylistsMe));

        List<List<NetPlaylistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetPlaylistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        playlistInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(playlistInfos, total.get());
    }

    /**
     * 获取歌手排行
     */
    public static CommonResult<NetArtistInfo> getArtistLists(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetArtistInfo> artistInfos = new LinkedList<>();
//        Set<NetArtistInfo> set = Collections.synchronizedSet(new HashSet<>());

        final String defaultTag = "默认";
        String[] s = Tags.artistTag.get(tag);

        // 网易云 (接口分页)
        // 歌手榜
        Callable<CommonResult<NetArtistInfo>> getArtistRanking = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_RANKING_LIST_API, s[0]))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONArray artistArray = artistInfoJson.getJSONObject("list").getJSONArray("artists");
                t = artistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getInt("musicSize");
                    Integer albumNum = artistJson.getInt("albumSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
//                if (!set.contains(artistInfo)) res.add(artistInfo);
//                set.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 热门歌手
        Callable<CommonResult<NetArtistInfo>> getHotArtist = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(HOT_ARTIST_LIST_API, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("artists");
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getInt("musicSize");
                Integer albumNum = artistJson.getInt("albumSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
                // 热门歌手一部分与歌手榜重复，需要去重
//                if (!set.contains(artistInfo)) res.add(artistInfo);
//                set.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌手
        Callable<CommonResult<NetArtistInfo>> getCatArtist = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String[] split = s[1].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(CAT_ARTIST_API, split[0], split[1], split[2], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONArray artistArray = artistInfoJson.getJSONArray("artists");
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getInt("musicSize");
                    Integer albumNum = artistJson.getInt("albumSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                    // 热门歌手一部分与歌手榜重复，需要去重
//                if (!set.contains(artistInfo)) res.add(artistInfo);
//                set.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        // 热门歌手
        Callable<CommonResult<NetArtistInfo>> getHotArtistKg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String[] split = s[2].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(HOT_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray artistArray = data.getJSONArray("info");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singerid");
                    String artistName = artistJson.getString("singername");
//            Integer songNum = artistJson.getInt("songcount");
//            Integer albumNum = artistJson.getInt("albumcount");
//            Integer mvNum = artistJson.getInt("mvcount");
                    String coverImgUrl = artistJson.getString("imgurl").replace("{size}", "240");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
//            artistInfo.setSongNum(songNum);
//            artistInfo.setAlbumNum(albumNum);
//            artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgUrl(coverImgUrl);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 飙升歌手
        Callable<CommonResult<NetArtistInfo>> getUpArtistKg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String[] split = s[2].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(UP_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray artistArray = data.getJSONArray("info");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singerid");
                    String artistName = artistJson.getString("singername");
//            Integer songNum = artistJson.getInt("songcount");
//            Integer albumNum = artistJson.getInt("albumcount");
//            Integer mvNum = artistJson.getInt("mvcount");
                    String coverImgUrl = artistJson.getString("imgurl").replace("{size}", "240");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
//            artistInfo.setSongNum(songNum);
//            artistInfo.setAlbumNum(albumNum);
//            artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgUrl(coverImgUrl);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ (每页固定 80 条)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingQq = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[3])) {
                final int num = 80;
                String[] split = s[3].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_QQ_API, split[0], split[1], split[2], split[3], (page - 1) / 4 + 1))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = (page - 1) * limit % num, len = Math.min(artistArray.size(), (page - 1) * limit % num + limit); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singer_mid");
                    String artistName = artistJson.getString("singer_name");
                    String coverImgUrl = String.format(ARTIST_IMG_QQ_API, artistId);
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QQ);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgUrl(coverImgUrl);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我 (接口分页)
        // 推荐歌手
        Callable<CommonResult<NetArtistInfo>> getArtistRankingKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[4])) {
                HttpResponse resp = HttpRequest.get(String.format(ARTIST_LIST_KW_API, s[4], page, limit)).execute();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String artistInfoBody = resp.body();
                    JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                    JSONObject data = artistInfoJson.getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray artistArray = data.getJSONArray("artistList");
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        JSONObject artistJson = artistArray.getJSONObject(i);

                        String artistId = artistJson.getString("id");
                        String artistName = artistJson.getString("name");
                        String coverImgUrl = artistJson.getString("pic300");
                        Integer songNum = artistJson.getInt("musicNum");
                        Integer albumNum = artistJson.getInt("albumNum");
                        Integer mvNum = artistJson.getInt("mvNum");
                        String coverImgThumbUrl = coverImgUrl;

                        NetArtistInfo artistInfo = new NetArtistInfo();
                        artistInfo.setSource(NetMusicSource.KW);
                        artistInfo.setId(artistId);
                        artistInfo.setName(artistName);
                        artistInfo.setSongNum(songNum);
                        artistInfo.setAlbumNum(albumNum);
                        artistInfo.setMvNum(mvNum);
                        // 酷我音乐没有单独的歌手信息接口，需要在搜索歌手时记录封面图片 url ！
                        artistInfo.setCoverImgUrl(coverImgUrl);
                        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                            if (coverImgThumb == null) coverImgThumb = extractProfile(coverImgThumbUrl);
                            artistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(artistInfo);
                        // 全部歌手一部分与推荐歌手重复，需要去重
//                    if (!set.contains(artistInfo)) res.add(artistInfo);
//                    set.add(artistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };
        // 全部歌手
        Callable<CommonResult<NetArtistInfo>> getAllArtistsKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[5])) {
                HttpResponse resp = HttpRequest.get(String.format(ALL_ARTISTS_LIST_KW_API, s[5], page, limit)).execute();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String artistInfoBody = resp.body();
                    JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                    JSONObject data = artistInfoJson.getJSONObject("data");
                    t = data.getInt("total");
                    JSONArray artistArray = data.getJSONArray("artistList");
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        JSONObject artistJson = artistArray.getJSONObject(i);

                        String artistId = artistJson.getString("id");
                        String artistName = artistJson.getString("name");
                        String coverImgUrl = artistJson.getString("pic300");
                        Integer songNum = artistJson.getInt("musicNum");
                        Integer albumNum = artistJson.getInt("albumNum");
                        Integer mvNum = artistJson.getInt("mvNum");
                        String coverImgThumbUrl = coverImgUrl;

                        NetArtistInfo artistInfo = new NetArtistInfo();
                        artistInfo.setSource(NetMusicSource.KW);
                        artistInfo.setId(artistId);
                        artistInfo.setName(artistName);
                        artistInfo.setSongNum(songNum);
                        artistInfo.setAlbumNum(albumNum);
                        artistInfo.setMvNum(mvNum);
                        // 酷我音乐没有单独的歌手信息接口，需要在搜索歌手时记录封面图片 url ！
                        artistInfo.setCoverImgUrl(coverImgUrl);
                        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                            if (coverImgThumb == null) coverImgThumb = extractProfile(coverImgThumbUrl);
                            artistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(artistInfo);
                        // 全部歌手一部分与推荐歌手重复，需要去重
//                    if (!set.contains(artistInfo)) res.add(artistInfo);
//                    set.add(artistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 来电新声榜(程序分页)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingMg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_MG_API))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("columnInfo");
            t = data.getInt("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 来电唱作榜(程序分页)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingMg2 = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_MG_API_2))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("columnInfo");
            t = data.getInt("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 推荐歌手
        Callable<CommonResult<NetArtistInfo>> getRecArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[6])) {
                // 分割时保留空串
                String[] sp = s[6].split(" ", -1);
                HttpResponse resp = HttpRequest.get(buildQianUrl(String.format(REC_ARTISTS_LIST_QI_API, System.currentTimeMillis())))
                        .execute();
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONArray("data").getJSONObject(7);
                t = data.getInt("module_nums");
                JSONArray artistArray = data.getJSONArray("result");
                for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("artistCode");
                    String artistName = artistJson.getString("name");
                    String coverImgUrl = artistJson.getString("pic");
                    Integer songNum = artistJson.getInt("trackTotal");
                    Integer albumNum = artistJson.getInt("albumTotal");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QI);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                        if (coverImgThumb == null) coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌手
        Callable<CommonResult<NetArtistInfo>> getCatArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[6])) {
                // 分割时保留空串
                String[] sp = s[6].split(" ", -1);
                HttpResponse resp = HttpRequest.get(buildQianUrl(
                                String.format(CAT_ARTISTS_LIST_QI_API, sp[0], sp[2], sp[1], page, limit, System.currentTimeMillis())))
                        .execute();
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray artistArray = data.getJSONArray("result");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("artistCode");
                    String artistName = artistJson.getString("name");
                    String coverImgUrl = artistJson.getString("pic");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QI);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgUrl);
                        if (coverImgThumb == null) coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetArtistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRanking));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotArtist));
        taskList.add(GlobalExecutors.requestExecutor.submit(getCatArtist));

        taskList.add(GlobalExecutors.requestExecutor.submit(getHotArtistKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getUpArtistKg));

        taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingQq));

        taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getAllArtistsKw));

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingMg));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingMg2));

        taskList.add(GlobalExecutors.requestExecutor.submit(getRecArtistsQi));
        taskList.add(GlobalExecutors.requestExecutor.submit(getCatArtistsQi));

        List<List<NetArtistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetArtistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        artistInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(artistInfos, total.get());
    }

    /**
     * 获取新晋电台
     */
    public static CommonResult<NetRadioInfo> getNewRadios(int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> radioInfos = new LinkedList<>();

        // 网易云(程序分页)
        // 新晋电台榜
        Callable<CommonResult<NetRadioInfo>> getNewRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(NEW_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("toplist");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐个性电台
        Callable<CommonResult<NetRadioInfo>> getPersonalizedRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(PERSONALIZED_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("result");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject infoJson = radioArray.getJSONObject(i);
                JSONObject programJson = infoJson.getJSONObject("program");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = djJson.getString("nickname");
                String djId = djJson.getString("userId");
//                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐电台
        Callable<CommonResult<NetRadioInfo>> getRecommendRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(RECOMMEND_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 付费精品电台
        Callable<CommonResult<NetRadioInfo>> getPayRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(PAY_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("list");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("creatorName");
                Long playCount = radioJson.getLong("score");
//                Integer trackCount = radioJson.getInt("programCount");
//                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
//                radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 付费精选电台
        Callable<CommonResult<NetRadioInfo>> getPayGiftRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(PAY_GIFT_RADIO_API, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("list");
            t = radioArray.size();
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
//                String dj = radioJson.getString("creatorName");
//                Long playCount = radioJson.getLong("score");
                Integer trackCount = radioJson.getInt("programCount");
//                String category = radioJson.getString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
//                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
//            radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ(程序分页)
        Callable<CommonResult<NetRadioInfo>> getRecommendRadiosQq = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(RECOMMEND_RADIO_QQ_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray data = radioInfoJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONArray radioArray = data.getJSONObject(i).getJSONArray("list");
                for (int j = 0, l = radioArray.size(); j < l; j++, t++) {
                    if (t >= (page - 1) * limit && t < page * limit) {
                        JSONObject radioJson = radioArray.getJSONObject(j);

                        String radioId = radioJson.getString("id");
                        String radioName = radioJson.getString("title");
                        String coverImgUrl = radioJson.getString("pic_url");
                        String coverImgThumbUrl = coverImgUrl;
                        Long playCount = radioJson.getLong("listenNum");

                        NetRadioInfo radioInfo = new NetRadioInfo();
                        radioInfo.setSource(NetMusicSource.QQ);
                        radioInfo.setId(radioId);
                        radioInfo.setName(radioName);
                        radioInfo.setPlayCount(playCount);
                        // QQ 需要提前写入电台图片 url，电台信息接口不提供！
                        radioInfo.setCoverImgUrl(coverImgUrl);
                        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            radioInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(radioInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 推荐广播剧
        Callable<CommonResult<NetRadioInfo>> getRecRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(REC_RADIO_ME_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("info");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = "https:" + radioJson.getString("cover");
                String description = StringUtils.removeHTMLLabel(radioJson.getString("abstract"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 夏日推荐
        Callable<CommonResult<NetRadioInfo>> getSummerRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(SUMMER_RADIO_ME_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("info");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONArray array = radioArray.getJSONArray(i);
                for (int j = 0, s = array.size(); j < s; j++, t++) {
                    if (t >= (page - 1) * limit && t < page * limit) {
                        JSONObject radioJson = array.getJSONObject(j);

                        String radioId = radioJson.getString("id");
                        String radioName = radioJson.getString("name");
                        String dj = radioJson.getString("author");
                        String coverImgThumbUrl = radioJson.getString("cover");

                        NetRadioInfo radioInfo = new NetRadioInfo();
                        radioInfo.setSource(NetMusicSource.ME);
                        radioInfo.setId(radioId);
                        radioInfo.setName(radioName);
                        radioInfo.setDj(dj);
                        radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                            radioInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(radioInfo);
                    }
                }
            }

            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getNewRadios));
        taskList.add(GlobalExecutors.requestExecutor.submit(getPersonalizedRadios));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendRadios));
        taskList.add(GlobalExecutors.requestExecutor.submit(getPayRadios));
        taskList.add(GlobalExecutors.requestExecutor.submit(getPayGiftRadios));

        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendRadiosQq));

        taskList.add(GlobalExecutors.requestExecutor.submit(getRecRadiosMe));
        taskList.add(GlobalExecutors.requestExecutor.submit(getSummerRadiosMe));

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        radioInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(radioInfos, total.get());
    }

    /**
     * 获取个性电台 + 今日优选 + 热门电台 + 热门电台榜
     */
    public static CommonResult<NetRadioInfo> getHotRadios(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetRadioInfo> radioInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.radioTag.get(tag);
        // 网易云(程序分页)
//        // 个性电台推荐
//        String radioInfoBody = HttpRequest.get(String.format(PERSONAL_RADIO_API))
//                .execute()
//                .body();
//        JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
//        JSONArray radioArray = radioInfoJson.getJSONArray("data");
//        for (int i = 0, len = radioArray.size(); i < len; i++) {
//            JSONObject radioJson = radioArray.getJSONObject(i);
//
//            long radioId = radioJson.getLong("id");
//            String radioName = radioJson.getString("name");
//
//            NetRadioInfo radioInfo = new NetRadioInfo();
//            radioInfo.setId(radioId);
//            radioInfo.setName(radioName);
//            radioInfos.add(radioInfo);
//        }
        // 今日优选电台
        Callable<CommonResult<NetRadioInfo>> getDailyRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(DAILY_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("data");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = null;
//                if (i >= rs) dj = radioJson.getJSONObject("dj").getString("nickname");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.optString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 热门电台
        Callable<CommonResult<NetRadioInfo>> getHotRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(HOT_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.optString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 热门电台榜
        Callable<CommonResult<NetRadioInfo>> getRadiosRanking = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(RADIO_TOPLIST_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("toplist");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.optString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐电台
        Callable<CommonResult<NetRadioInfo>> getRecRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(REC_RADIO_API))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.optString("category");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类热门电台
        Callable<CommonResult<NetRadioInfo>> getCatHotRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_HOT_RADIO_API, s[0], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                t = radioInfoJson.getInt("count");
                JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getJSONObject("dj").getString("nickname");
                    String djId = radioJson.getJSONObject("dj").getString("userId");
//                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("programCount");
                    String category = radioJson.optString("category");
                    String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类推荐电台
        Callable<CommonResult<NetRadioInfo>> getCatRecRadios = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_REC_RADIO_API, s[1]))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
                t = radioArray.size();
                for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = radioJson.getJSONObject("dj").getString("nickname");
                    String djId = radioJson.getJSONObject("dj").getString("userId");
//                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("programCount");
                    String category = radioJson.optString("category");
                    String coverImgThumbUrl = radioJson.getString("picUrl");
//            long ms = radioJson.optLong("createTime");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 喜马拉雅
        // 分类电台
        Callable<CommonResult<NetRadioInfo>> getCatRadiosXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[3])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_XM_API, s[3], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray radioArray = data.getJSONArray("albums");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("albumId");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getString("anchorName");
                    String djId = radioJson.getString("uid");
//                    String description = radioJson.getString("description");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
                    String category = tag;
                    String coverImgUrl = "http:" + radioJson.getString("coverPath");
                    coverImgUrl = coverImgUrl.substring(0, coverImgUrl.lastIndexOf('!'));
                    String coverImgThumbUrl = coverImgUrl;
//            long ms = radioJson.optLong("createTime");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
//                    radioInfo.setDescription(description);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 频道电台
        Callable<CommonResult<NetRadioInfo>> getChannelRadiosXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[4])) {
                String radioInfoBody = HttpRequest.get(String.format(CHANNEL_RADIO_XM_API, s[4], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray radioArray = data.getJSONArray("albums");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("albumId");
                    String radioName = radioJson.getString("albumTitle");
                    String dj = radioJson.getString("albumUserNickName");
//                    String description = radioJson.optString("intro");
                    Long playCount = radioJson.getLong("albumPlayCount");
                    Integer trackCount = radioJson.getInt("albumTrackCount");
                    String category = tag;
                    String coverImgUrl = "http://imagev2.xmcdn.com/" + radioJson.getString("albumCoverPath");
                    String coverImgThumbUrl = coverImgUrl;
//            long ms = radioJson.optLong("createTime");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
//                    radioInfo.setDescription(description);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 排行榜
        Callable<CommonResult<NetRadioInfo>> getCatRadioRankingXm = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String[] sp = s[2].split(" ");
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_RANKING_XM_API, sp[0], sp[1]))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("rankList").getJSONObject(0).getJSONArray("albums");
                t = radioArray.size();
                for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("albumTitle");
                    String dj = radioJson.getString("anchorName");
                    String djId = radioJson.getString("anchorUrl").replace("/zhubo/", "");
//                    String description = radioJson.getString("description");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
                    String category = radioJson.optString("categoryTitle");
                    String coverImgUrl = "http://imagev2.xmcdn.com/" + radioJson.getString("cover");
                    String coverImgThumbUrl = coverImgUrl;
//            long ms = radioJson.optLong("createTime");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
//                    radioInfo.setDescription(description);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//            if (ms != 0) {
//                String createTime = TimeUtils.msToDate(ms);
//                radioInfo.setCreateTime(createTime);
//            }
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 周榜
        Callable<CommonResult<NetRadioInfo>> getWeekRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(WEEK_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 月榜
        Callable<CommonResult<NetRadioInfo>> getMonthRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(MONTH_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 总榜
        Callable<CommonResult<NetRadioInfo>> getAllTimeRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            String radioInfoBody = HttpRequest.get(String.format(ALL_TIME_RADIO_ME_API, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("ranks");
            JSONArray radioArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getString("author");
                String coverImgThumbUrl = radioJson.getString("cover");
                String description = radioJson.getString("abstract");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setDescription(description);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 广播剧分类
        Callable<CommonResult<NetRadioInfo>> getCatRadiosMe = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[5])) {
                String[] sp = s[5].split(" ");
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_ME_API, sp[2], sp[0], sp[1], page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getInt("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String category = radioJson.getString("type_name");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        // Top 250
        Callable<CommonResult<NetRadioInfo>> getTopRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;
            final int rn = 25;

            String radioInfoBody = HttpRequest.get(String.format(TOP_RADIO_DB_API, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioInfoBody);
            Elements rs = doc.select("div.item");
            String ts = ReUtil.get("共(\\d+)条", doc.select("span.count").text(), 1);
            t = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t -= t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Elements a = radio.select("div.hd a");
                Elements p = radio.select("div.bd p");
                Elements img = radio.select("div.pic img");

                String radioId = ReUtil.get("/subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text().trim();
                String dj = ReUtil.get("导演: (.*?) ", p.text(), 1);
                String coverImgThumbUrl = img.attr("src");
                String category = "电影";

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类电台
        Callable<CommonResult<NetRadioInfo>> getCatRadiosDb = () -> {
            LinkedList<NetRadioInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[6])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_RADIO_DB_API, s[6], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONArray radioArray = JSONArray.fromObject(radioInfoBody);
                t = JSONObject.fromObject(HttpRequest.get(String.format(CAT_RADIO_TOTAL_DB_API, s[6])).execute().body()).getInt("total");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = joinStrings(radioJson.getJSONArray("actors"), 4);
                    String coverImgThumbUrl = radioJson.getString("cover_url");
                    String category = joinStrings(radioJson.getJSONArray("types"), -1);

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getDailyRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRadiosRanking));
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecRadios));

            taskList.add(GlobalExecutors.requestExecutor.submit(getWeekRadiosMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getMonthRadiosMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllTimeRadiosMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosMe));

            taskList.add(GlobalExecutors.requestExecutor.submit(getTopRadiosDb));
        } else {
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatHotRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRecRadios));

            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosXm));
            taskList.add(GlobalExecutors.requestExecutor.submit(getChannelRadiosXm));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadioRankingXm));

            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosMe));

            taskList.add(GlobalExecutors.requestExecutor.submit(getCatRadiosDb));
        }

        List<List<NetRadioInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRadioInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        radioInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(radioInfos, total.get());
    }

    /**
     * 获取推荐节目
     */
    public static CommonResult<NetMusicInfo> getRecommendPrograms(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.programTag.get(tag);

        // 网易云(程序分页)
        // 推荐节目
        Callable<CommonResult<NetMusicInfo>> getRecommendPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(RECOMMEND_PROGRAM_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.fromObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("programs");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i);
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String albumName = radioJson.getString("name");
                String albumImgUrl = programJson.getString("coverUrl");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐个性节目
        Callable<CommonResult<NetMusicInfo>> getPersonalizedPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PERSONALIZED_PROGRAM_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.fromObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("result");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String albumName = radioJson.getString("name");
                String albumImgUrl = programJson.getString("coverUrl");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 24 小时节目榜
        Callable<CommonResult<NetMusicInfo>> get24HoursPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PROGRAM_24_HOURS_TOPLIST_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.fromObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONObject("data").getJSONArray("list");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String albumName = radioJson.getString("name");
                String albumImgUrl = programJson.getString("coverUrl");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 节目榜
        Callable<CommonResult<NetMusicInfo>> getProgramsRanking = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PROGRAM_TOPLIST_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.fromObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("toplist");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String albumName = radioJson.getString("name");
                String albumImgUrl = programJson.getString("coverUrl");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 推荐节目
        Callable<CommonResult<NetMusicInfo>> getRecProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(REC_PROGRAM_ME_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.fromObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONObject("info").getJSONObject("sounds").getJSONArray("day3");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i);

                String id = programJson.getString("id");
                String name = programJson.getString("soundstr");
//                String albumImgUrl = programJson.getString("front_cover");
                Double duration = programJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(id);
                musicInfo.setName(name);
//                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 探索节目(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getExpProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String programInfoBody = HttpRequest.get(String.format(EXP_PROGRAM_ME_API, s[0], page, limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = ts.isEmpty() ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select(".video-box");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("data-id");
                    String name = box.select(".video-title").text();
                    String artist = box.select(".video-auther a").text();
                    Double duration = TimeUtils.chineseToSeconds(box.select("span.video-duration").get(1).text());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setFormat(Format.M4A);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 首页分类节目(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getIndexCatProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_PROGRAM_ME_API, s[1], page, limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = ts.isEmpty() ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select("div.vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("href").replaceFirst("/sound/", "");
                    String name = box.attr("title");
                    Double duration = TimeUtils.toSeconds(box.select("div.vw-frontsound-time.fc-hoverheight").first().text().trim());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setFormat(Format.M4A);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 首页分类节目(最新)(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getIndexCatNewProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_NEW_PROGRAM_ME_API, s[1], page, limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = ts.isEmpty() ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select("div.vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("href").replaceFirst("/sound/", "");
                    String name = box.attr("title");
                    Double duration = TimeUtils.toSeconds(box.select("div.vw-frontsound-time.fc-hoverheight").first().text().trim());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setFormat(Format.M4A);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = tag.equals(defaultTag);

        if (dt) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPrograms));
            taskList.add(GlobalExecutors.requestExecutor.submit(getPersonalizedPrograms));
            taskList.add(GlobalExecutors.requestExecutor.submit(get24HoursPrograms));
            taskList.add(GlobalExecutors.requestExecutor.submit(getProgramsRanking));

            taskList.add(GlobalExecutors.requestExecutor.submit(getRecProgramsMe));
        } else {
            taskList.add(GlobalExecutors.requestExecutor.submit(getExpProgramsMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getIndexCatProgramsMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getIndexCatNewProgramsMe));
        }

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        musicInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取飙升歌曲
     */
    public static CommonResult<NetMusicInfo> getHotMusicRecommend(int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        // 网易云(榜单就是歌单，固定榜单 id 直接请求歌单音乐接口，接口分页)
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusic = () -> getMusicInfoInPlaylist(String.valueOf(19723756), NetMusicSource.NET_CLOUD, limit, page);
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusic = () -> getMusicInfoInPlaylist(String.valueOf(3778678), NetMusicSource.NET_CLOUD, limit, page);

        // 酷狗
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(UP_MUSIC_KG_API, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvHash = songJson.getString("mvhash");
                String mvId = StringUtils.isEmpty(mvHash) ? "" : songJson.getJSONArray("mvdata").getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // TOP500
        Callable<CommonResult<NetMusicInfo>> getTop500Kg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(TOP500_KG_API, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvHash = songJson.getString("mvhash");
                String mvId = StringUtils.isEmpty(mvHash) ? "" : songJson.getJSONArray("mvdata").getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 流行指数榜
        Callable<CommonResult<NetMusicInfo>> getPopularMusicQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(POPULAR_MUSIC_QQ_API, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(HOT_MUSIC_QQ_API, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String id = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        // 飙升榜
        Callable<CommonResult<NetMusicInfo>> getUpMusicKw = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(UP_MUSIC_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getInt("num");
                JSONArray songArray = data.getJSONArray("musicList");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String id = songJson.getString("rid");
                    String name = songJson.getString("name");
                    String artist = songJson.getString("artist");
                    String albumName = songJson.getString("album");
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KW);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicKw = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(HOT_MUSIC_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getInt("num");
                JSONArray songArray = data.getJSONArray("musicList");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String id = songJson.getString("rid");
                    String name = songJson.getString("name");
                    String artist = songJson.getString("artist");
                    String albumName = songJson.getString("album");
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KW);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 尖叫热歌榜
        Callable<CommonResult<NetMusicInfo>> getHotMusicMg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(HOT_MUSIC_MG_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
            t = data.getInt("contentsCount");
            JSONArray songArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");

                String songId = songJson.optString("copyrightId");
                // 过滤掉不是歌曲的 objectInfo
                if (StringUtils.isEmpty(songId)) continue;
                String name = songJson.getString("songName");
                String artists = songJson.getString("singer");
                String albumName = songJson.getString("album");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
                musicInfo.setAlbumName(albumName);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusic));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusic));

        taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusicKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getTop500Kg));

        taskList.add(GlobalExecutors.requestExecutor.submit(getPopularMusicQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicQq));

        taskList.add(GlobalExecutors.requestExecutor.submit(getUpMusicKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicKw));

        taskList.add(GlobalExecutors.requestExecutor.submit(getHotMusicMg));

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        musicInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取推荐歌曲 + 新歌速递
     */
    public static CommonResult<NetMusicInfo> getNewMusic(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.newSongTag.get(tag);

        // 网易云(程序分页)
        // 推荐新歌
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSong = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(RECOMMEND_NEW_SONG_API)
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("result");
            t = songsArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                JSONObject jsonObject = songsArray.getJSONObject(i);
                JSONObject songJson;
                if (jsonObject.has("song")) songJson = jsonObject.getJSONObject("song");
                else songJson = jsonObject;
                String songId = songJson.getString("id");
                String songName = songJson.getString("name");
                String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("duration") / 1000;
                String mvId = songJson.getString("mvid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);
                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 新歌速递
        Callable<CommonResult<NetMusicInfo>> getFastNewSong = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String musicInfoBody = HttpRequest.get(String.format(FAST_NEW_SONG_API, s[0]))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONArray songsArray = musicInfoJson.getJSONArray("data");
                t = songsArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                    JSONObject jsonObject = songsArray.getJSONObject(i);
                    JSONObject songJson;
                    if (jsonObject.has("song")) songJson = jsonObject.getJSONObject("song");
                    else songJson = jsonObject;
                    String songId = songJson.getString("id");
                    String songName = songJson.getString("name");
                    String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String albumName = songJson.getJSONObject("album").getString("name");
                    Double duration = songJson.getDouble("duration") / 1000;
                    String mvId = songJson.getString("mvid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);
                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        // 华语新歌(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_KG_API, s[1], page, limit))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray songsArray = data.getJSONArray("info");
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String hash = songJson.getString("hash");
                    String songId = songJson.getString("album_audio_id");
                    String[] sp = songJson.getString("filename").split(" - ");
                    String songName = sp[1];
                    String artist = sp[0];
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getString("mvhash");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KG);
                    musicInfo.setHash(hash);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQq = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_QQ_API, s[2]))
                        .execute()
                        .body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONArray songsArray = musicInfoJson.getJSONObject("data").getJSONArray("list");
                t = songsArray.size();
                for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String songId = songJson.getString("mid");
                    String songName = songJson.getString("title");
                    String artist = parseArtists(songJson, NetMusicSource.QQ);
                    String albumName = songJson.getJSONObject("album").getString("name");
                    Double duration = songJson.getDouble("interval");
                    String mvId = songJson.getJSONObject("mv").getString("vid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.QQ);
                    musicInfo.setId(songId);
                    musicInfo.setName(songName);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongKw = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(NEW_SONG_KW_API, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String musicInfoBody = resp.body();
                JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
                JSONObject data = musicInfoJson.getJSONObject("data");
                t = data.getInt("num");
                JSONArray songsArray = data.getJSONArray("musicList");
                t = Math.max(t, songsArray.size());
                for (int i = 0, len = songsArray.size(); i < len; i++) {
                    JSONObject songJson = songsArray.getJSONObject(i);

                    String id = songJson.getString("rid");
                    String name = songJson.getString("name");
                    String artist = songJson.getString("artist");
                    String albumName = songJson.getString("album");
                    Double duration = songJson.getDouble("duration");
                    String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.KW);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfo.setMvId(mvId);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕(接口分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongMg = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(String.format(RECOMMEND_NEW_SONG_MG_API, page, limit))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray songsArray = data.getJSONArray("list");
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("cid");
                String songName = songJson.getString("name");
                String artist = parseArtists(songJson, NetMusicSource.MG);

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千(程序分页)
        Callable<CommonResult<NetMusicInfo>> getRecommendNewSongQi = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String musicInfoBody = HttpRequest.get(buildQianUrl(String.format(RECOMMEND_NEW_SONG_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONArray("data").getJSONObject(3);
            t = data.getInt("module_nums");
            JSONArray songsArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(songsArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String songName = songJson.getString("title");
                String artist = parseArtists(songJson, NetMusicSource.QI);
                String albumName = songJson.getString("albumTitle");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSong));
        taskList.add(GlobalExecutors.requestExecutor.submit(getFastNewSong));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongQq));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongKw));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongMg));
        if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendNewSongQi));

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        musicInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取新碟上架
     */
    public static CommonResult<NetAlbumInfo> getNewAlbums(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetAlbumInfo> albumInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.newAlbumTag.get(tag);

        // 网易云(程序分页)
        // 新蹀上架
        Callable<CommonResult<NetAlbumInfo>> getNewAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String albumInfoBody = HttpRequest.get(String.format(NEW_ALBUM_API, s[0]))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONArray albumArray = albumInfoJson.optJSONArray("weekData");
                JSONArray monthData = albumInfoJson.getJSONArray("monthData");
                if (albumArray == null) albumArray = monthData;
                else albumArray.addAll(monthData);
                t = albumArray.size();
                for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                    String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
                    Integer songNum = albumJson.getInt("size");
                    String coverImgThumbUrl = albumJson.getString("picUrl");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 新碟上架(热门)
//        Callable<CommonResult<NetAlbumInfo>> getHotAlbums = () -> {
//            LinkedList<NetAlbumInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            if (StringUtils.isNotEmpty(s[0])) {
//                String albumInfoBody = HttpRequest.get(String.format(HOT_ALBUM_API, s[0]))
//                        .execute()
//                        .body();
//                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//                JSONArray albumArray = albumInfoJson.optJSONArray("weekData");
//                JSONArray monthData = albumInfoJson.getJSONArray("monthData");
//                if (albumArray == null) albumArray = monthData;
//                else albumArray.addAll(monthData);
//                t = albumArray.size();
//                for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
//                    JSONObject albumJson = albumArray.getJSONObject(i);
//
//                    String albumId = albumJson.getString("id");
//                    String albumName = albumJson.getString("name");
//                    String artist = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
//                    String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
//                    Integer songNum = albumJson.getInt("size");
//                    String coverImgThumbUrl = albumJson.getString("picUrl");
//
//                    NetAlbumInfo albumInfo = new NetAlbumInfo();
//                    albumInfo.setId(albumId);
//                    albumInfo.setName(albumName);
//                    albumInfo.setArtist(artist);
//                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    albumInfo.setPublishTime(publishTime);
//                    albumInfo.setSongNum(songNum);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
//                        albumInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(albumInfo);
//                }
//            }
//            return new CommonResult<>(res, t);
//        };
        // 全部新碟(接口分页，与上面两个分开处理)
        Callable<CommonResult<NetAlbumInfo>> getAllNewAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String albumInfoBody = HttpRequest.get(String.format(ALL_NEW_ALBUM_API, s[0], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                t = albumInfoJson.getInt("total");
                JSONArray albumArray = albumInfoJson.getJSONArray("albums");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                    String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
                    Integer songNum = albumJson.getInt("size");
                    String coverImgThumbUrl = albumJson.getString("picUrl");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新专辑
        Callable<CommonResult<NetAlbumInfo>> getNewestAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(NEWEST_ALBUM_API))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONArray("albums");
            t = albumArray.size();
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getInt("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 数字新碟上架
        Callable<CommonResult<NetAlbumInfo>> getNewestDiAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(NEWEST_DI_ALBUM_API))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONArray albumArray = albumInfoJson.getJSONArray("products");
            t = albumArray.size();
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumId");
                String albumName = albumJson.getString("albumName");
                String artist = albumJson.getString("artistName");
                String publishTime = TimeUtils.msToDate(albumJson.getLong("pubTime"));
                String coverImgThumbUrl = albumJson.getString("coverUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 数字专辑语种风格馆
        Callable<CommonResult<NetAlbumInfo>> getLangDiAlbums = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[1])) {
                String albumInfoBody = HttpRequest.get(String.format(LANG_DI_ALBUM_API, s[1]))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONArray albumArray = albumInfoJson.getJSONArray("albumProducts");
                t = albumArray.size();
                for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumId");
                    String albumName = albumJson.getString("albumName");
                    String artist = albumJson.getString("artistName");
                    String coverImgThumbUrl = albumJson.getString("coverUrl");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ(程序分页)
        Callable<CommonResult<NetAlbumInfo>> getNewAlbumsQq = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String albumInfoBody = HttpRequest.get(String.format(NEW_ALBUM_QQ_API, s[2]))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONArray albumArray = albumInfoJson.getJSONObject("data").getJSONArray("list");
                t = albumArray.size();
                for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("mid");
                    String albumName = albumJson.getString("name");
                    String artist = parseArtists(albumJson, NetMusicSource.QQ);
                    String publishTime = albumJson.getString("release_time");
//            Integer songNum = albumJson.getJSONObject("ex").getInt("track_nums");
                    String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.QQ);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
//            albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 新碟推荐(接口分页)
        Callable<CommonResult<NetAlbumInfo>> getNewAlbumsMg = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(NEW_ALBUM_MG_API, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = parseArtists(albumJson, NetMusicSource.MG);
                String publishTime = albumJson.getString("publishTime");
                Integer songNum = albumJson.getInt("songCount");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.MG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 新专辑榜(程序分页)
        Callable<CommonResult<NetAlbumInfo>> getNewAlbumsRankingMg = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(String.format(NEW_ALBUM_RANKING_MG_API))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("columnInfo");
            t = data.getInt("contentsCount");
            JSONArray albumArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(albumArray.size(), page * limit); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i).getJSONObject("objectInfo");

                String albumId = albumJson.getString("albumId");
                String albumName = albumJson.getString("title");
                String artist = albumJson.getString("singer");
                String publishTime = albumJson.getString("publishTime");
                Integer songNum = albumJson.getInt("totalCount");
                String coverImgThumbUrl = albumJson.getJSONArray("imgItems").getJSONObject(0).getString("img");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.MG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 秀动发行
        Callable<CommonResult<NetAlbumInfo>> getXDAlbumsQi = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(buildQianUrl(String.format(XD_ALBUM_QI_API, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.optJSONObject("data");
            t = data.optInt("total");
            JSONArray albumArray = data.getJSONArray("result");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = parseArtists(albumJson, NetMusicSource.QI);
                String coverImgThumbUrl = albumJson.getString("pic");
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                Integer songNum = albumJson.getInt("trackCount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 新专辑推荐
        Callable<CommonResult<NetAlbumInfo>> getNewAlbumsQi = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            String albumInfoBody = HttpRequest.get(buildQianUrl(String.format(NEW_ALBUM_QI_API, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.optInt("total");
            JSONArray albumArray = data.getJSONArray("result");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = parseArtists(albumJson, NetMusicSource.QI);
                String coverImgThumbUrl = albumJson.getString("pic");
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                Integer songNum = albumJson.getInt("trackCount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        // Top 250
        Callable<CommonResult<NetAlbumInfo>> getTopAlbumsDb = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;
            final int rn = 25;

            String radioInfoBody = HttpRequest.get(String.format(TOP_ALBUM_DB_API, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioInfoBody);
            Elements as = doc.select("tr.item");
            t -= 250 / rn * 5;
            for (int i = 0, len = as.size(); i < len; i++) {
                Element album = as.get(i);
                Elements a = album.select("div.pl2 a");
                Elements pl = album.select("div.pl2 p.pl");
                Elements img = album.select("td img");

                String albumId = ReUtil.get("/subject/(\\d+)/", a.attr("href"), 1);
                String albumName = a.text().trim();
                String[] sp = pl.text().split(" / ");
                String artist = sp[0];
                String pubTime = sp[1];
                String coverImgThumbUrl = img.attr("src");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setPublishTime(pubTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类专辑
        Callable<CommonResult<NetAlbumInfo>> getCatAlbumsDb = () -> {
            LinkedList<NetAlbumInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[3])) {
                String radioInfoBody = HttpRequest.get(String.format(CAT_ALBUM_DB_API, s[3], (page - 1) * limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements as = doc.select("tr.item");
                Element te = doc.select("div.paginator > a").last();
                String ts = te == null ? "" : te.text();
                t = StringUtils.isNotEmpty(ts) ? Integer.parseInt(ts) * limit : limit;
                for (int i = 0, len = as.size(); i < len; i++) {
                    Element album = as.get(i);
                    Elements a = album.select("div.pl2 a");
                    Elements pl = album.select("div.pl2 p.pl");
                    Elements img = album.select("td img");

                    String albumId = ReUtil.get("/subject/(\\d+)/", a.attr("href"), 1);
                    String albumName = a.text().trim();
                    String[] sp = pl.text().split(" / ");
                    String artist = sp[0];
                    String pubTime = sp[1];
                    String coverImgThumbUrl = img.attr("src");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.DB);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setPublishTime(pubTime);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(albumInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetAlbumInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbums));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewestAlbums));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewestDiAlbums));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllNewAlbums));

            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbumsQq));

            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbumsMg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbumsRankingMg));

            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbumsQi));
            taskList.add(GlobalExecutors.requestExecutor.submit(getXDAlbumsQi));

            taskList.add(GlobalExecutors.requestExecutor.submit(getTopAlbumsDb));
        } else {
            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbums));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllNewAlbums));
            taskList.add(GlobalExecutors.requestExecutor.submit(getLangDiAlbums));

            taskList.add(GlobalExecutors.requestExecutor.submit(getNewAlbumsQq));

            taskList.add(GlobalExecutors.requestExecutor.submit(getCatAlbumsDb));
        }
//        taskList.add(GlobalExecutors.requestExecutor.submit(getHotAlbums));

        List<List<NetAlbumInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetAlbumInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        albumInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(albumInfos, total.get());
    }

    /**
     * 获取 MV 排行 + 最新 MV + 推荐 MV
     */
    public static CommonResult<NetMvInfo> getRecommendMvs(String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> mvInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.mvTag.get(tag);

        // 网易云(程序分页)
        // MV 排行
        Callable<CommonResult<NetMvInfo>> getMvRanking = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String mvInfoBody = HttpRequest.get(String.format(TOP_MV_API, s[0].replace("全部", ""), (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                    String pubTime = mvJson.getJSONObject("mv").getString("publishTime");
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新 MV
        Callable<CommonResult<NetMvInfo>> getNewMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0])) {
                String mvInfoBody = HttpRequest.get(String.format(NEW_MV_API, s[0]))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    Long playCount = mvJson.getLong("playCount");
//                Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 全部 MV
        Callable<CommonResult<NetMvInfo>> getAllMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[0]) || StringUtils.isNotEmpty(s[1])) {
                String mvInfoBody = HttpRequest.get(String.format(ALL_MV_API, s[0], s[1], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);
                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    Long playCount = mvJson.getLong("playCount");
//                Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(RECOMMEND_MV_API)
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("result");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name");
                String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                Long playCount = mvJson.getLong("playCount");
//                Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("picUrl");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 网易出品 MV
        Callable<CommonResult<NetMvInfo>> getExclusiveMv = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(EXCLUSIVE_MV_API, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);
                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name");
                String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                Long playCount = mvJson.getLong("playCount");
//                Double duration = mvJson.getJSONObject("mv").getJSONArray("videos").getJSONObject(0).getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("cover");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        Callable<CommonResult<NetMvInfo>> getRecommendMvKg = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[2])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_KG_API, s[2], page, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("info");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("mvhash");
                    String mvName = mvJson.getString("videoname");
                    String artistName = mvJson.getString("singername");
                    Long playCount = mvJson.getLong("playcount");
                    Double duration = mvJson.getDouble("duration") / 1000;
                    String pubTime = mvJson.getString("publish").split(" ")[0];
                    String coverImgUrl = mvJson.getString("img").replace("/{size}", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KG);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        // 推荐 MV (接口分页)
        Callable<CommonResult<NetMvInfo>> getRecommendMvQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[3])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_MV_QQ_API, s[3], s[4], page, limit))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("list");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = parseArtists(mvJson, NetMusicSource.QQ);
                    Long playCount = mvJson.getLong("playcnt");
                    Double duration = mvJson.getDouble("duration");
                    String pubTime = TimeUtils.msToDate(mvJson.getLong("pubdate") * 1000);
                    String coverImgUrl = mvJson.getString("picurl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.QQ);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 最新 MV (程序分页)
        Callable<CommonResult<NetMvInfo>> getNewMvQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[5])) {
                String mvInfoBody = HttpRequest.get(String.format(NEW_MV_QQ_API, s[5]))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("list");
                t = mvArray.size();
                for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("mvtitle");
                    String artistName = mvJson.getString("singername");
                    Long playCount = mvJson.getLong("listennum");
                    String pubTime = mvJson.getString("pub_date");
                    String coverImgUrl = mvJson.getString("picurl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.QQ);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setPubTime(pubTime);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 推荐 MV
        Callable<CommonResult<NetMvInfo>> getRecommendMvQi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(buildQianUrl(String.format(RECOMMEND_MV_QI_API, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("result");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("assetCode");
                String mvName = mvJson.getString("title");
                String artistName = parseArtists(mvJson, NetMusicSource.QI);
                Long playCount = mvJson.getLong("playnum");
                String coverImgUrl = mvJson.getString("pic");
                Double duration = mvJson.getDouble("duration") / 1000;
                String pubTime = mvJson.getString("originalReleaseDate").split("T")[0];

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QI);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 好看
        // 猜你喜欢视频
        Callable<CommonResult<NetMvInfo>> getGuessVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(GUESS_VIDEO_HK_API, System.currentTimeMillis()))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            JSONArray mvArray = data.getJSONArray("apiData");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                Long playCount = StringUtils.antiFormatNumber(mvJson.getString("fmplaycnt"));
                String coverImgUrl = "https:" + mvJson.getString("poster");
                Double duration = TimeUtils.toSeconds(mvJson.getString("time_length"));
                String pubTime = TimeUtils.msToDate(mvJson.getLong("publish_time") * 1000);

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 榜单视频
        Callable<CommonResult<NetMvInfo>> getTopVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(TOP_VIDEO_HK_API, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("apiData").getJSONObject("response");
            t = data.getInt("total_page") * limit;
            JSONArray mvArray = data.getJSONArray("video");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                Long playCount = mvJson.getLong("hot");
                String coverImgUrl = mvJson.getString("poster");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publish_time").replaceAll("[发布时间：日]", "").replaceAll("年|月", "-");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类推荐视频
        Callable<CommonResult<NetMvInfo>> getRecommendVideoHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtils.isNotEmpty(s[6])) {
                String mvInfoBody = HttpRequest.get(String.format(RECOMMEND_VIDEO_HK_API, s[6], limit))
                        .header(Header.COOKIE, HK_COOKIE)
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data").getJSONObject("response");
                JSONArray mvArray = data.getJSONArray("videos");
                t = limit;
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("title");
                    String artistName = mvJson.getString("source_name");
                    Long playCount = mvJson.getLong("playcnt");
                    String coverImgUrl = mvJson.getString("poster_pc");
                    Double duration = TimeUtils.toSeconds(mvJson.getString("duration"));
                    String pubTime = mvJson.getString("publish_time").replaceAll("年|月", "-").replace("日", "");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.HK);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setPubTime(pubTime);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (dt) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMv));
            taskList.add(GlobalExecutors.requestExecutor.submit(getExclusiveMv));

            taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvQi));

            taskList.add(GlobalExecutors.requestExecutor.submit(getGuessVideoHk));
            taskList.add(GlobalExecutors.requestExecutor.submit(getTopVideoHk));
        }
        taskList.add(GlobalExecutors.requestExecutor.submit(getMvRanking));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewMv));
        taskList.add(GlobalExecutors.requestExecutor.submit(getAllMv));

        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendMvQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getNewMvQq));

        taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendVideoHk));

        List<List<NetMvInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMvInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        mvInfos.addAll(ListUtils.joinAll(rl));

        return new CommonResult<>(mvInfos, total.get());
    }

    /**
     * 根据歌单 id 和 source 预加载歌单信息
     */
    public static void preloadPlaylistInfo(NetPlaylistInfo playlistInfo) {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;

        int source = playlistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
//            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_API, playlistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");

//            String coverImgUrl = playlistJson.getString("coverImgUrl");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
//            if (!playlistInfo.hasName()) playlistInfo.setName(playlistJson.getString("name"));
//            if (!playlistInfo.hasCreator())
//                playlistInfo.setCreator(playlistJson.getJSONObject("creator").getString("nickname"));
//            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(playlistJson.getInt("trackCount"));
//            if (!playlistInfo.hasPlayCount()) playlistInfo.setPlayCount(playlistJson.getLong("playCount"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KG_API, playlistInfo.getId(), 1))
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("info").getJSONObject("list");

//            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
//            if (!playlistInfo.hasName()) playlistInfo.setName(data.getString("specialname"));
//            if (!playlistInfo.hasCreator()) playlistInfo.setCreator(data.getString("nickname"));
//            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("songcount"));
//            if (!playlistInfo.hasPlayCount()) playlistInfo.setPlayCount(data.getLong("playcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
//            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, playlistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.optJSONObject("data");

//            if (data != null) {
//                String coverImgUrl = data.getString("logo");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
//                if (!playlistInfo.hasName()) playlistInfo.setName(data.getString("dissname"));
//                if (!playlistInfo.hasCreator()) playlistInfo.setCreator(data.getString("nickname"));
//                if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("songnum"));
//                if (!playlistInfo.hasPlayCount()) playlistInfo.setPlayCount(data.getLong("visitnum"));
//            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, playlistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("img500");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
//            if (!playlistInfo.hasName()) playlistInfo.setName(data.getString("name"));
//            if (!playlistInfo.hasCreator()) playlistInfo.setCreator(data.getString("userName"));
//            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("total"));
//            if (!playlistInfo.hasPlayCount()) playlistInfo.setPlayCount(data.getLong("listencnt"));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
//            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, playlistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
//            JSONObject data = playlistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("picUrl");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
//            if (!playlistInfo.hasName()) playlistInfo.setName(data.getString("name"));
//            if (!playlistInfo.hasCreator()) playlistInfo.setCreator(data.getJSONObject("creator").getString("name"));
//            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("trackCount"));
//            if (!playlistInfo.hasPlayCount()) playlistInfo.setPlayCount(data.getLong("playCount"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImgThumb(extractProfile(playlistInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public static void fillPlaylistInfo(NetPlaylistInfo playlistInfo) throws IOException {
        // 信息完整直接跳过
        if (playlistInfo.isIntegrated()) return;

        int source = playlistInfo.getSource();
        String id = playlistInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("playlist");

            String coverImgUrl = playlistJson.getString("coverImgUrl");
            String description = playlistJson.getString("description");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description.equals("null") ? "" : description);
            if (!playlistInfo.hasCreator())
                playlistInfo.setCreator(playlistJson.getJSONObject("creator").getString("nickname"));
            if (!playlistInfo.hasCreatorId())
                playlistInfo.setCreatorId(playlistJson.getJSONObject("creator").getString("userId"));
            if (!playlistInfo.hasTag())
                playlistInfo.setTag(parseTags(playlistJson, NetMusicSource.NET_CLOUD));
            if (!playlistInfo.hasTrackCount())
                playlistInfo.setTrackCount(playlistJson.getInt("trackCount"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KG_API, id,
                            StringUtils.toMD5("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwtappid=1058clienttime=1586163242519clientver=20000dfid=-format=jsonpglobal_specialid="
                                    + id + "mid=1586163242519specialid=0srcappid=2919uuid=1586163242519NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt")))
                    .header("mid", "1586163242519")
                    .header("Referer", "https://m3ws.kugou.com/share/index.php")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                    .header("dfid", "-")
                    .header("clienttime", "1586163242519")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            String description = data.getString("intro");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            playlistInfo.setTag("");
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("songcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("logo");
            String description = data.getString("desc").replace("<br>", "\n");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(parseTags(data, NetMusicSource.QQ));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(data.getInt("songnum"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("img500");
            String description = data.getString("info");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(data.getString("tag").replace(",", "、"));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_MG_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("picUrl");
            String description = data.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(parseTags(data, NetMusicSource.MG));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playlistInfoBody = HttpRequest.get(buildQianUrl(String.format(PLAYLIST_DETAIL_QI_API, id, 1, 1, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject playlistJson = playlistInfoJson.getJSONObject("data");

            String coverImgUrl = playlistJson.getString("pic");
            String description = playlistJson.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(parseTags(playlistJson, NetMusicSource.QI));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(playlistJson.getInt("trackCount"));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject info = playlistInfoJson.getJSONObject("info");
            JSONObject album = info.getJSONObject("album");

            String coverImgUrl = album.getString("front_cover");
            String description = StringUtils.removeHTMLLabel(album.getString("intro"));
            GlobalExecutors.imageExecutor.submit(() -> playlistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            playlistInfo.setDescription(description);
            if (!playlistInfo.hasTag()) playlistInfo.setTag(parseTags(info, NetMusicSource.ME));
            if (!playlistInfo.hasTrackCount()) playlistInfo.setTrackCount(album.getInt("music_count"));
        }
    }

    /**
     * 根据专辑 id 预加载专辑信息
     */
    public static void preloadAlbumInfo(NetAlbumInfo albumInfo) {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;

        int source = albumInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//            JSONObject albumJson = albumInfoJson.getJSONObject("album");
//
//            String coverImgUrl = albumJson.getString("picUrl");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
//            if (!albumInfo.hasName()) albumInfo.setName(albumJson.getString("name"));
//            if (!albumInfo.hasArtist()) albumInfo.setArtist(albumJson.getJSONObject("artist").getString("name"));
//            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getInt("size"));
//            if (!albumInfo.hasPublishTime())
//                albumInfo.setPublishTime(TimeUtils.msToDate(albumJson.getLong("publishTime")));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, albumInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
//            if (!albumInfo.hasName()) albumInfo.setName(data.getString("albumname"));
//            if (!albumInfo.hasArtist()) albumInfo.setArtist(data.getString("singername"));
////            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(data.getInt("songcount"));
//            if (!albumInfo.hasPublishTime())
//                albumInfo.setPublishTime(data.getString("publishtime").replace(" 00:00:00", ""));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, albumInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
//
//            String coverImgUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumInfo.getId());
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
//            if (!albumInfo.hasName()) albumInfo.setName(data.getString("albumName"));
//            if (!albumInfo.hasArtist()) albumInfo.setArtist(parseArtists(data, NetMusicSource.QQ));
//            if (!albumInfo.hasPublishTime()) albumInfo.setPublishTime(data.getString("publishTime"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KW_API, albumInfo.getId(), 1, 1))
//                    .execute()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//            JSONObject data = albumInfoJson.optJSONObject("data");
//
//            if (data != null) {
//                String coverImgUrl = data.getString("pic");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
//                if (!albumInfo.hasName()) albumInfo.setName(data.getString("album"));
//                if (!albumInfo.hasArtist()) albumInfo.setArtist(data.getString("artist"));
//                if (!albumInfo.hasSongNum()) albumInfo.setSongNum(data.getInt("total"));
//                if (!albumInfo.hasPublishTime()) albumInfo.setPublishTime(data.getString("releaseDate"));
//            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
//            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//            JSONObject data = albumInfoJson.getJSONObject("data");
//
//            String coverImgUrl = "https:" + data.getString("picUrl");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
//            if (!albumInfo.hasName()) albumInfo.setName(data.getString("name"));
//            if (!albumInfo.hasArtist()) albumInfo.setArtist(parseArtists(data, NetMusicSource.MG));
//            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(data.getJSONArray("songList").size());
//            if (!albumInfo.hasPublishTime()) albumInfo.setPublishTime(data.getString("publishTime"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(extractProfile(albumInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public static void fillAlbumInfo(NetAlbumInfo albumInfo) throws IOException {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;

        int source = albumInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("album");

            String coverImgUrl = albumJson.getString("picUrl");
            String description = albumJson.getString("description");
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getInt("size"));
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(TimeUtils.msToDate(albumJson.getLong("publishTime")));
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description.equals("null") ? "" : description);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            String description = data.getString("intro").replace("\\n", "\n");
            albumInfo.setCoverImg(getImageFromUrl(coverImgUrl));
            albumInfo.setDescription(description);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            // QQ 专辑封面图片 url 获取方式与歌曲相同
            String coverImgUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumInfo.getId());
            String description = data.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KW_API, albumInfo.getId(), 1, 1))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.optJSONObject("data");

            if (data != null) {
                String coverImgUrl = data.getString("pic");
                String description = data.getString("albuminfo");
                Integer songNum = data.getInt("total");
                GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
                albumInfo.setDescription(description);
                albumInfo.setSongNum(songNum);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            String coverImgUrl = "https:" + data.getString("picUrl");
            String description = data.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(buildQianUrl(String.format(ALBUM_DETAIL_QI_API, albumInfo.getId(), System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("data");

            String coverImgUrl = albumJson.getString("pic");
            String description = albumJson.getString("introduce");
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getJSONArray("trackList").size());
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(albumJson.getString("releaseDate").split("T")[0]);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DB_API, albumInfo.getId()))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            String info = StringUtils.getPrettyText(doc.select("div#info").first()) + "\n";
            Element re = doc.select("div#link-report").first();
            Elements span = re.select("span");
            String desc = StringUtils.getPrettyText(span.isEmpty() ? re : span.last()) + "\n";
            String tracks = StringUtils.getPrettyText(doc.select("div.track-list div div").first());
            String coverImgUrl = doc.select("div#mainpic img").attr("src");

            albumInfo.setDescription(info + desc + "\n曲目：\n" + tracks);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DT_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("data");

            String coverImgUrl = albumJson.getJSONArray("covers").getString(0);
            String description = albumJson.getString("desc");
//            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getJSONArray("trackList").size());
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(TimeUtils.msToDate(albumJson.getLong("updated_at_ts") * 1000));
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }
    }

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public static void preloadArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;

        int source = artistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
//            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_API, artistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
//            JSONObject artistJson = artistInfoJson.getJSONObject("artist");
//
//            String coverImgUrl = artistJson.getString("img1v1Url");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(extractProfile(artistInfo.getCoverImgThumbUrl())));
//            if (!artistInfo.hasName()) artistInfo.setName(artistJson.getString("name"));
//            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistJson.getInt("musicSize"));
//            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(artistJson.getInt("musicSize"));
//            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(artistJson.getInt("mvSize"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, artistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = data.getString("imgurl").replace("{size}", "240");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(extractProfile(artistInfo.getCoverImgThumbUrl())));
//            if (!artistInfo.hasName()) artistInfo.setName(data.getString("singername"));
//            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getInt("songcount"));
//            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getInt("albumcount"));
//            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getInt("mvcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
//            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, artistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("data");

            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(extractProfile(artistInfo.getCoverImgThumbUrl())));
//            if (!artistInfo.hasName()) artistInfo.setName(data.getString("singername"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImgThumb = extractProfile(artistInfo.getCoverImgThumbUrl());
                if (coverImgThumb == null)
                    coverImgThumb = extractProfile(artistInfo.getCoverImgUrl().replaceFirst("/300/", "/0/"));
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
//            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, artistInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
//            JSONObject data = artistInfoJson.getJSONObject("data");
//
//            String coverImgUrl = "https:" + data.getString("picUrl");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(extractProfile(artistInfo.getCoverImgThumbUrl())));
//            if (!artistInfo.hasName()) artistInfo.setName(data.getString("name"));
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(extractProfile(artistInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public static void fillArtistInfo(NetArtistInfo artistInfo) throws IOException {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;

        int source = artistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_API, artistInfo.getId()))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject artistJson = artistInfoJson.getJSONObject("artist");

            String coverImgUrl = artistJson.getString("img1v1Url");
            String description = artistJson.getString("briefDesc");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description.equals("null") ? "" : description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistJson.getInt("musicSize"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(artistJson.getInt("mvSize"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, artistInfo.getId()))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("intro");
            String coverImgUrl = data.getString("imgurl").replace("{size}", "240");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getInt("songcount"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getInt("albumcount"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getInt("mvcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, artistInfo.getId()))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

//            String coverImgUrl = String.format(ARTIST_IMG_QQ_API, artistInfo.getId());
            String description = data.optString("desc");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(artistInfo.getCoverImgUrl())));
            artistInfo.setDescription(description);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImg = getImageFromUrl(artistInfo.getCoverImgUrl());
                if (coverImg == null)
                    coverImg = getImageFromUrl(artistInfo.getCoverImgUrl().replaceFirst("/300/", "/0/"));
                artistInfo.setCoverImg(coverImg);
            });
            artistInfo.setDescription("");
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, artistInfo.getId()))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String coverImgUrl = "https:" + data.getString("picUrl");
            String description = data.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = HttpRequest.get(buildQianUrl(String.format(ARTIST_DETAIL_QI_API, artistInfo.getId(), System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("introduce");
            String coverImgUrl = data.getString("pic");
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getInt("trackTotal"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getInt("albumTotal"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getInt("videoTotal"));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, artistInfo.getId()))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            String info = StringUtils.getPrettyText(doc.select("#headline div.info").first()) + "\n";
            Element bd = doc.select("#intro div.bd").first();
            Elements span = bd.select("span");
            String desc = StringUtils.getPrettyText(span.isEmpty() ? bd : span.last());
            String coverImgUrl = doc.select("div.nbg img").attr("src");

            artistInfo.setDescription(info + desc);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
        }
    }

    /**
     * 根据电台 id 预加载电台信息
     */
    public static void preloadRadioInfo(NetRadioInfo radioInfo) {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;

        int source = radioInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
//            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, radioInfo.getId()))
//                    .execute()
//                    .body();
//            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
//            JSONObject radioJson = radioInfoJson.getJSONObject("data");
//
//            String coverImgUrl = radioJson.getString("picUrl");
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImgThumb(extractProfile(radioInfo.getCoverImgThumbUrl())));
//            if (!radioInfo.hasName()) radioInfo.setName(radioJson.getString("name"));
//            if (!radioInfo.hasDj()) radioInfo.setDj(radioJson.getJSONObject("dj").getString("nickname"));
//            if (!radioInfo.hasCategory()) radioInfo.setCategory(radioJson.getString("category"));
//            if (!radioInfo.hasTrackCount()) radioInfo.setTrackCount(radioJson.getInt("programCount"));
//            if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(radioJson.getInt("playCount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImgThumb(extractProfile(radioInfo.getCoverImgThumbUrl())));
//            radioInfo.setDescription("");
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImgThumb(extractProfile(radioInfo.getCoverImgThumbUrl())));
//            radioInfo.setDescription("");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImgThumb(extractProfile(radioInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public static void fillRadioInfo(NetRadioInfo radioInfo) throws IOException {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;

        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject radioJson = radioInfoJson.getJSONObject("data");

            String coverImgUrl = radioJson.getString("picUrl");
            String description = radioJson.getString("desc");
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            radioInfo.setDescription(description.equals("null") ? "" : description);
            if (!radioInfo.hasDj()) radioInfo.setDj(radioJson.getJSONObject("dj").getString("nickname"));
            if (!radioInfo.hasDjId()) radioInfo.setDjId(radioJson.getJSONObject("dj").getString("userId"));
            if (!radioInfo.hasCategory()) {
                String category = radioJson.getString("category");
                if (!category.isEmpty()) category += "、" + radioJson.getString("secondCategory");
                radioInfo.setCategory(category);
            }
            if (!radioInfo.hasTrackCount()) radioInfo.setTrackCount(radioJson.getInt("programCount"));
            if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(radioJson.getLong("playCount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(getImageFromUrl(radioInfo.getCoverImgUrl())));
            radioInfo.setDescription("");
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(getImageFromUrl(radioInfo.getCoverImgUrl())));
            if (!radioInfo.hasDescription()) {
                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_XM_API, id))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject radioJson = radioInfoJson.getJSONObject("data").getJSONObject("albumPageMainInfo");
                radioInfo.setDescription(radioJson.optString("shortIntro"));
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject info = radioInfoJson.getJSONObject("info");
            JSONObject drama = info.getJSONObject("drama");
            JSONObject episodes = info.getJSONObject("episodes");

            String coverImgUrl = drama.getString("cover");
            String description = drama.getString("abstract");
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
            if (!radioInfo.hasDescription()) radioInfo.setDescription(StringUtils.removeHTMLLabel(description));
            if (!radioInfo.hasDj()) radioInfo.setDj(drama.getString("author"));
            if (!radioInfo.hasDjId()) radioInfo.setDjId(drama.getString("user_id"));
            if (!radioInfo.hasCategory()) radioInfo.setCategory(drama.getString("catalog_name"));
            if (!radioInfo.hasTrackCount()) radioInfo.setTrackCount(episodes.getJSONArray("episode").size());
            if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(drama.getLong("view_count"));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String radioInfoBody = HttpRequest.get(String.format(isBook ? BOOK_RADIO_DETAIL_DB_API : RADIO_DETAIL_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(radioInfoBody);
            String info = StringUtils.getPrettyText(doc.select("div#info").first()) + "\n";
            Elements re = doc.select("div#link-report");
            Elements span = re.select("span");
            Element intro = doc.select("div.intro").last();
            Element cata = doc.select(String.format("div#dir_%s_full", id)).first();
            Element tr = doc.select("div.subject_show.block5:not(#rec-ebook-section) div").first();

            String desc = StringUtils.getPrettyText(span.isEmpty() ? re.first() : span.last()) + "\n";
            String authorIntro = StringUtils.getPrettyText(intro) + "\n";
            String catalog = StringUtils.getPrettyText(cata) + "\n\n";
            String trace = StringUtils.getPrettyText(tr);
            String coverImgUrl = doc.select("div#mainpic img").attr("src");

            radioInfo.setDescription(info + desc + "作者简介：\n" + authorIntro + "目录：\n" + catalog + "丛书信息：\n" + trace);
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(getImageFromUrl(coverImgUrl)));
        }
    }

    /**
     * 根据 MV id 预加载 MV 信息
     */
    public static void preloadMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.submit(() -> mvInfo.setCoverImgThumb(extractMvCover(mvInfo.getCoverImgUrl())));
    }

    /**
     * 根据 MV id 补全 MV 信息(只包含 url)
     */
    public static void fillMvInfo(NetMvInfo mvInfo) {
        // 信息完整直接跳过
        if (mvInfo.isIntegrated()) return;

        mvInfo.setUrl(fetchMvUrl(mvInfo));
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public static void fillMvDetail(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        String mvId = netMvInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String mvBody = HttpRequest.get(String.format(MV_DETAIL_API, mvId))
                    .execute()
                    .body();
            JSONObject mvJson = JSONObject.fromObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data");
            String name = data.getString("name");
            String artists = parseArtists(data, NetMusicSource.NET_CLOUD);
            Long playCount = data.getLong("playCount");
            Double duration = data.getDouble("duration") / 1000;
            String pubTime = data.getString("publishTime");
            String coverImgUrl = data.getString("cover");

            netMvInfo.setName(name);
            netMvInfo.setArtist(artists);
            netMvInfo.setPlayCount(playCount);
            netMvInfo.setDuration(duration);
            netMvInfo.setPubTime(pubTime);
            netMvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                netMvInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String mvBody = HttpRequest.get(String.format(MV_DETAIL_KG_API, mvId))
                    .execute()
                    .body();
            JSONObject mvJson = JSONObject.fromObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data").getJSONObject("info");
            String[] s = data.getString("filename").split(" - ");
            String name = s[1];
            String artists = s[0];
            Long playCount = data.getLong("history_heat");
            Double duration = data.getDouble("mv_timelength") / 1000;
            String pubTime = data.getString("update");
            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");

            netMvInfo.setName(name);
            netMvInfo.setArtist(artists);
            netMvInfo.setPlayCount(playCount);
            netMvInfo.setDuration(duration);
            netMvInfo.setPubTime(pubTime);
            netMvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                netMvInfo.setCoverImgThumb(coverImgThumb);
            });
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvBody = HttpRequest.get(String.format(MV_DETAIL_QQ_API, mvId))
                    .execute()
                    .body();
            JSONObject mvJson = JSONObject.fromObject(mvBody);
            JSONObject data = mvJson.getJSONObject("data").getJSONObject("info");
            String name = data.getString("name");
            String artists = parseArtists(data, NetMusicSource.QQ);
            Long playCount = data.getLong("playcnt");
            Double duration = data.getDouble("duration");
            String pubTime = TimeUtils.msToDate(data.getLong("pubdate") * 1000);
            String coverImgUrl = data.getString("cover_pic");

            netMvInfo.setName(name);
            netMvInfo.setArtist(artists);
            netMvInfo.setPlayCount(playCount);
            netMvInfo.setDuration(duration);
            netMvInfo.setPubTime(pubTime);
            netMvInfo.setCoverImgUrl(coverImgUrl);

            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                netMvInfo.setCoverImgThumb(coverImgThumb);
            });
        }
    }

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public static void preloadRankingInfo(NetRankingInfo rankingInfo) {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;

        int source = rankingInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImgThumb(extractProfile(rankingInfo.getCoverImgUrl())));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImgThumb(extractProfile(rankingInfo.getCoverImgUrl())));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImgThumb(extractProfile(rankingInfo.getCoverImgUrl())));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImgThumb(extractProfile(rankingInfo.getCoverImgUrl())));
        }
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public static void fillRankingInfo(NetRankingInfo rankingInfo) throws IOException {
        // 信息完整直接跳过
        if (rankingInfo.isIntegrated()) return;

        int source = rankingInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_QQ_API, rankingInfo.getId(), 1))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");

            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // QQ 需要额外补全榜单描述
            rankingInfo.setDescription(data.getJSONObject("info").getString("desc").replace("<br>", "\n"));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(getImageFromUrl(rankingInfo.getCoverImgUrl())));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, rankingInfo.getId()))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");

            if (!rankingInfo.hasPlayCount())
                rankingInfo.setPlayCount(data.getJSONObject("opNumItem").getLong("playNum"));
            if (!rankingInfo.hasUpdateTime()) rankingInfo.setUpdateTime(data.optString("columnUpdateTime"));
            GlobalExecutors.imageExecutor.submit(() -> rankingInfo.setCoverImg(getImageFromUrl(rankingInfo.getCoverImgUrl())));
            // 咪咕需要额外补全榜单描述
            rankingInfo.setDescription(data.getString("columnDes"));
        }
    }

    /**
     * 根据用户 id 预加载用户信息
     */
    public static void preloadUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;

        int source = userInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatarThumb(extractProfile(userInfo.getAvatarThumbUrl())));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatarThumb(extractProfile(userInfo.getAvatarThumbUrl())));
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatarThumb(extractProfile(userInfo.getAvatarThumbUrl())));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatarThumb(extractProfile(userInfo.getAvatarThumbUrl())));
        }
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public static void fillUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;

        int source = userInfo.getSource();
        String uid = userInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_API, uid))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject profileJson = userInfoJson.getJSONObject("profile");
            if (!userInfo.hasLevel()) userInfo.setLevel(userInfoJson.getInt("level"));
            if (!userInfo.hasAccAge()) userInfo.setAccAge(TimeUtils.getAccAge(profileJson.getLong("createTime")));
            if (!userInfo.hasBirthday()) userInfo.setBirthday(TimeUtils.msToDate(profileJson.getLong("birthday")));
            if (!userInfo.hasArea())
                userInfo.setArea(AreaUtils.getArea(profileJson.getInt("province"), profileJson.getInt("city")));
            if (!userInfo.hasSign()) userInfo.setSign(profileJson.getString("signature"));
            if (!userInfo.hasFollow()) userInfo.setFollow(profileJson.getInt("follows"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(profileJson.getInt("followeds"));
            if (!userInfo.hasPlaylistCount()) userInfo.setPlaylistCount(profileJson.getInt("playlistCount"));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(getImageFromUrl(profileJson.getString("backgroundUrl"))));
        }

        // QQ
        if (source == NetMusicSource.QQ) {
            userInfo.setSign("");
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, uid))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            if (!userInfo.hasLevel()) userInfo.setLevel(data.getInt("anchorGrade"));
            if (!userInfo.hasGender()) {
                Integer gen = data.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                userInfo.setGender(gender);
            }
            if (!userInfo.hasBirthday())
                userInfo.setBirthday(data.getInt("birthMonth") <= 0 ? null : data.getString("birthMonth") + "-" + data.getString("birthDay"));
            if (!userInfo.hasArea()) {
                String area = (data.has("province") ? data.getString("province") : "") + (data.has("city") ? " - " + data.getString("city") : "");
                userInfo.setArea(area.isEmpty() ? "未知" : area);
            }
            if (!userInfo.hasSign()) userInfo.setSign(data.optString("personalSignature"));
            if (!userInfo.hasFollow()) userInfo.setFollow(data.getInt("followingCount"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(data.getInt("fansCount"));
            if (!userInfo.hasRadioCount()) userInfo.setRadioCount(data.getInt("albumsCount"));
            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(data.getInt("tracksCount"));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(getImageFromUrl("http:" + data.getString("background"))));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, uid))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);

            if (!userInfo.hasLevel())
                userInfo.setLevel(Integer.parseInt(doc.select("span.level").first().text().replace("LV", "")));
            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasArea()) userInfo.setArea("未知");
            if (!userInfo.hasSign())
                userInfo.setSign(doc.select("#t_u_n_a").first().text());
            if (!userInfo.hasFollow())
                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
            if (!userInfo.hasFollowed())
                userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));
//            if (!userInfo.hasRadioCount()) userInfo.setRadioCount(Integer.parseInt(ReUtil.get(
//                    "剧集.*?\\((\\d+)\\)", userInfoBody, 1)));
//            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(Integer.parseInt(ReUtil.get(
//                    "声音.*?\\((\\d+)\\)", userInfoBody, 1)));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
            String bgUrl = ReUtil.get("style=\"background-image:url\\((.*?)\\)\"", userInfoBody, 1);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(getImageFromUrl(
                    (bgUrl.startsWith("//static") ? "https:" : "https://www.missevan.com") + bgUrl)));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, uid))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);

            if (!userInfo.hasAccAge()) {
                String dt = ReUtil.get("(\\d+\\-\\d+\\-\\d+)加入", doc.select("div.pl").text(), 1);
                userInfo.setAccAge(TimeUtils.getAccAge(TimeUtils.dateToMs(dt)));
            }
            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasArea()) userInfo.setArea(doc.select("div.user-info a").text());
            if (!userInfo.hasSign())
                userInfo.setSign(StringUtils.getPrettyText(doc.select("span#intro_display").first()));
//            if (!userInfo.hasFollow())
//                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
//            if (!userInfo.hasFollowed())
//                userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DT_API, uid))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);

            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasArea()) userInfo.setArea("未知");
            if (!userInfo.hasSign())
                userInfo.setSign(doc.select("div.people-desc").text().trim());
            if (!userInfo.hasFollow())
                userInfo.setFollow(Integer.parseInt(ReUtil.get("(\\d+)", doc.select("div.people-funs a").first().text(), 1)));
            if (!userInfo.hasFollowed())
                userInfo.setFollowed(Integer.parseInt(ReUtil.get("(\\d+)", doc.select("div.people-funs a").last().text(), 1)));
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(getImageFromUrl(userInfo.getAvatarUrl())));
            String bgUrl = doc.select("img.header-bg").attr("src");
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(getImageFromUrl(bgUrl)));
        }
    }

//    /**
//     * 根据专辑 id 获取专辑歌曲总数
//     */
//    public static Integer findMusicCountInAlbum(long albumId) {
//        String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumId))
//                .execute()
//                .body();
//        JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
//        JSONArray songsArray = albumInfoJson.getJSONArray("songs");
//        return songsArray.size();
//    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInPlaylist(String playlistId, int source, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> netMusicInfos = new LinkedList<>();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            LinkedList<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_API, playlistId, (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray songArray = playlistInfoJson.getJSONArray("songs");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String name = songJson.getString("name");
                    String artists = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String albumName = songJson.getJSONObject("al").getString("name");
                    Double duration = songJson.getDouble("dt") / 1000;
                    String mvId = songJson.getString("mv");

                    NetMusicInfo netMusicInfo = new NetMusicInfo();
                    netMusicInfo.setId(songId);
                    netMusicInfo.setName(name);
                    netMusicInfo.setArtist(artists);
                    netMusicInfo.setAlbumName(albumName);
                    netMusicInfo.setDuration(duration);
                    netMusicInfo.setMvId(mvId);

                    netMusicInfos.add(netMusicInfo);
                }
            }));

            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
                // 网易云获取歌单歌曲总数需要额外请求歌单详情接口！
                String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_API, playlistId))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                total.set(playlistInfoJson.getJSONObject("playlist").getInt("trackCount"));
            }));

            taskList.forEach(task -> {
                try {
                    task.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_KG_API, playlistId, page, limit,
                            StringUtils.toMD5("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwtappid=1058clienttime=1586163263991" +
                                    "clientver=20000dfid=-global_specialid=" + playlistId + "mid=1586163263991page=" + page + "pagesize=" + limit +
                                    "plat=0specialid=0srcappid=2919uuid=1586163263991version=8000NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt")))
                    .header("mid", "1586163263991")
                    .header("Referer", "https://m3ws.kugou.com/share/index.php")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                    .header("dfid", "-")
                    .header("clienttime", "1586163263991")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getInt("total"));
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KG);
                netMusicInfo.setHash(hash);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
//                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_QQ_API, playlistId))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getInt("songnum"));
            JSONArray songArray = data.getJSONArray("songlist");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("songmid");
                String name = songJson.getString("songname");
                String artists = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getString("albumname");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QQ);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_KW_API, playlistId))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray songArray = playlistInfoJson.getJSONObject("data").getJSONArray("musicList");
            total.set(songArray.size());
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                String name = songJson.getString("name");
                String artists = songJson.getString("artist");
                String albumName = songJson.getString("album");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KW);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_SONGS_MG_API, playlistId, page, limit))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header(Header.REFERER, "https://m.music.migu.cn/")
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray songArray = playlistInfoJson.getJSONArray("list");
            total.set(playlistInfoJson.getInt("totalCount"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("copyrightId");
                String name = songJson.getString("songName");
                String artists = parseArtists(songJson, NetMusicSource.MG);
                String albumName = songJson.getString("album");
                Double duration = TimeUtils.toSeconds(songJson.getString("length"));
                // 咪咕音乐没有 mv 时，该字段不存在！
                String mvId = songJson.optString("mvId");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.MG);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playlistInfoBody = HttpRequest.get(buildQianUrl(String.format(PLAYLIST_DETAIL_QI_API, playlistId, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            total.set(data.getInt("trackCount"));
            JSONArray songArray = data.getJSONArray("trackList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QI);
                String albumName = songJson.getString("albumTitle");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QI);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String playlistInfoBody = HttpRequest.get(String.format(PLAYLIST_DETAIL_ME_API, playlistId))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("info");
            JSONArray songArray = data.getJSONArray("sounds");
            total.set(songArray.size());
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("soundstr");
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.ME);
                netMusicInfo.setFormat(Format.M4A);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        return new CommonResult<>(netMusicInfos, total.get());
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInAlbum(String albumId, int source, int limit, int page) throws IOException {
        int total = 0;
        List<NetMusicInfo> netMusicInfos = new LinkedList<>();

        // 网易云 (程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONArray songArray = albumInfoJson.getJSONArray("songs");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("name");
                String artists = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String albumName = songJson.getJSONObject("al").getString("name");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷狗 (接口分页)
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_KG_API, albumId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KG);
                netMusicInfo.setHash(hash);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
//                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // QQ (程序分页)
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_QQ_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QQ);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷我 (接口分页)
        else if (source == NetMusicSource.KW) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KW_API, albumId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("musicList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                String name = songJson.getString("name");
                String artists = songJson.getString("artist");
                String albumName = songJson.getString("album");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KW);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 咪咕 (程序分页)
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("songList");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("cid");
                String name = songJson.getString("name");
                String artists = parseArtists(songJson, NetMusicSource.MG);
                String albumName = songJson.getJSONObject("album").getString("name");
                // 咪咕音乐可能没有 mvId
                String mvId = songJson.optString("mvId");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.MG);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(buildQianUrl(String.format(ALBUM_DETAIL_QI_API, albumId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("trackList");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("assetId");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QI);
                String albumName = data.getString("title");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QI);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        return new CommonResult<>(netMusicInfos, total);
    }

//    /**
//     * 根据歌手 id 获取歌手歌曲总数
//     */
//    public static Integer findMusicCountInArtist(long artistId) {
//        String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_API, artistId, 0, 0))
//                .execute()
//                .body();
//        JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
//        int total = artistInfoJson.getInt("total");
//        return total;
//    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInArtist(String artistId, int source, int limit, int page) throws IOException {
        int total = 0;
        List<NetMusicInfo> netMusicInfos = new LinkedList<>();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_API, artistId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            total = artistInfoJson.getInt("total");
            JSONArray songArray = artistInfoJson.getJSONArray("songs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("name");
                String artists = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String albumName = songJson.getJSONObject("al").getString("name");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_KG_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvHash = songJson.getString("mvhash");
                String mvId = StringUtils.isEmpty(mvHash) ? "" : songJson.getJSONArray("mvdata").getJSONObject(0).getString("hash");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KG);
                netMusicInfo.setHash(hash);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
//                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_QQ_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QQ);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_KW_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                // 酷我歌名中可能含有 HTML 标签，先去除
                String name = StringUtils.removeHTMLLabel(songJson.getString("name"));
                String artists = songJson.getString("artist");
                String albumName = songJson.getString("album");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KW);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_MG_API, artistId, page))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.optJSONObject("data");
            // 咪咕可能接口异常，需要判空！
            if (data != null) {
                total = data.optInt("totalPage") * 20;
                JSONArray songArray = data.getJSONArray("list");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("cid");
                    String name = songJson.getString("name");
                    String artists = parseArtists(songJson, NetMusicSource.MG);
                    String albumName = songJson.getJSONObject("album").getString("name");
                    // 咪咕音乐可能没有 MV 字段！
                    String mvId = songJson.optString("mvId");

                    NetMusicInfo netMusicInfo = new NetMusicInfo();
                    netMusicInfo.setSource(NetMusicSource.MG);
                    netMusicInfo.setId(songId);
                    netMusicInfo.setName(name);
                    netMusicInfo.setArtist(artists);
                    netMusicInfo.setAlbumName(albumName);
                    netMusicInfo.setMvId(mvId);

                    netMusicInfos.add(netMusicInfo);
                }
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = HttpRequest.get(buildQianUrl(String.format(ARTIST_SONGS_QI_API, artistId, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QI);
                String albumName = songJson.getString("albumTitle");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QI);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        return new CommonResult<>(netMusicInfos, total);
    }

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public static CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo netArtistInfo, int limit, int page) throws IOException {
        int total = 0;
        List<NetAlbumInfo> albumInfos = new LinkedList<>();

        String artistId = netArtistInfo.getId();
        int source = netArtistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_API, artistId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            total = albumInfoJson.getJSONObject("artist").getInt("albumSize");
            JSONArray albumArray = albumInfoJson.getJSONArray("hotAlbums");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String id = albumJson.getString("id");
                String name = albumJson.getString("name");
                String artists = parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                String publishTime = TimeUtils.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getInt("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(id);
                albumInfo.setName(name);
                albumInfo.setArtist(artists);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_KG_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("info");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
                String description = albumJson.getString("intro");
                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
                Integer songNum = albumJson.getInt("songcount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setDescription(description);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_QQ_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = Math.max(total, data.getInt("total"));
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("album_mid");
                String albumName = albumJson.getString("album_name");
                String artist = parseArtists(albumJson, NetMusicSource.QQ);
                String publishTime = albumJson.getString("pub_time");
                Integer songNum = albumJson.getJSONObject("latest_song").getInt("song_count");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = HttpRequest.get(String.format(ARTIST_ALBUMS_KW_API, artistId, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String albumInfoBody = resp.body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject data = albumInfoJson.getJSONObject("data");
                total = data.getInt("total");
                JSONArray albumArray = data.getJSONArray("albumList");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumid");
                    String albumName = albumJson.getString("album").replace("&nbsp;", " ");
                    String artist = albumJson.getString("artist").replace("&nbsp;", " ");
                    String publishTime = albumJson.getString("releaseDate");
                    String coverImgThumbUrl = albumJson.getString("pic");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.KW);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    albumInfos.add(albumInfo);
                }
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_MG_API, artistId, page))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            // 咪咕可能接口异常，需要判空！
            JSONObject data = albumInfoJson.optJSONObject("data");
            if (data != null) {
                total = data.optInt("total");
                JSONArray albumArray = data.getJSONArray("list");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = parseArtists(albumJson, NetMusicSource.MG);
                    String coverImgThumbUrl = "http:" + albumJson.getString("picUrl");
//                    String publishTime = albumJson.getString("publishTime");
//                    Integer songNum = albumJson.getInt("songCount");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.MG);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    albumInfo.setPublishTime(publishTime);
//                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    albumInfos.add(albumInfo);
                }
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(buildQianUrl(String.format(ARTIST_ALBUMS_QI_API, artistId, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.optJSONObject("data");
            total = data.optInt("total");
            JSONArray albumArray = data.getJSONArray("result");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = parseArtists(albumJson, NetMusicSource.QI);
                String coverImgThumbUrl = albumJson.getString("pic");
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                Integer songNum = albumJson.getJSONArray("trackList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        return new CommonResult<>(albumInfos, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public static CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo netArtistInfo, int limit, int page) {
        int total = 0;
        List<NetMvInfo> mvInfos = new LinkedList<>();

        String artistId = netArtistInfo.getId();
        int source = netArtistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 歌手 MV
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_API, artistId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
            total = netArtistInfo.getMvNum();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name");
                String artistName = mvJson.getString("artistName");
                Long playCount = mvJson.getLong("playCount");
                Double duration = mvJson.getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("imgurl");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
            // 歌手视频
//            Callable<CommonResult<NetMvInfo>> getArtistVideo = ()->{
//                List<NetMvInfo> res = new LinkedList<>();
//                int t = 0;
//
//                String mvInfoBody = HttpRequest.get(String.format(ARTIST_VIDEOS_API, artistId, (page - 1) * limit, limit))
//                        .execute()
//                        .body();
//                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
//                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("records");
//                t = netArtistInfo.getMvNum();
//                for (int i = 0, len = mvArray.size(); i < len; i++) {
//                    JSONObject mvJson = mvArray.getJSONObject(i);
//
//                    String mvId = mvJson.getString("id");
//                    String mvName = mvJson.getString("name");
//                    String artistName = mvJson.getString("artistName");
//                    Long playCount = mvJson.getLong("playCount");
//                    Double duration = mvJson.getDouble("duration") / 1000;
//                    String coverImgUrl = mvJson.getString("imgurl");
//
//                    NetMvInfo mvInfo = new NetMvInfo();
//                    mvInfo.setId(mvId);
//                    mvInfo.setName(mvName.trim());
//                    mvInfo.setArtist(artistName);
//                    mvInfo.setCoverImgUrl(coverImgUrl);
//                    mvInfo.setPlayCount(playCount);
//                    mvInfo.setDuration(duration);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
//                        mvInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(mvInfo);
//                }
//
//                return new CommonResult<>(res, t);
//            };
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_KG_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("hash");
                // 酷狗返回的名称含有 HTML 标签，需要去除
                String mvName = StringUtils.removeHTMLLabel(mvJson.getString("filename"));
                String artistName = StringUtils.removeHTMLLabel(mvJson.getString("singername"));
                String coverImgUrl = mvJson.getString("imgurl");
//                Long playCount = mvJson.getLong("historyheat");
//                Double duration = mvJson.getDouble("duration");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
//                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_QQ_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("singer_name");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("listenCount");
//                Double duration = mvJson.getDouble("duration");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = HttpRequest.get(String.format(ARTIST_MVS_KW_API, artistId, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                total = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = mvJson.getString("artist");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    mvInfos.add(mvInfo);
                }
            }
        }

        return new CommonResult<>(mvInfos, total);
    }

//    /**
//     * 根据电台 id 获取电台歌曲总数
//     */
//    public static Integer findMusicCountInRadio(long radioId) {
//        String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, radioId))
//                .execute()
//                .body();
//        JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
//        return radioInfoJson.getJSONObject("data").getInt("programCount");
//    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        int source = radioInfo.getSource();
        String radioId = radioInfo.getId();

        // 网易云(接口分页)
        if (source == NetMusicSource.NET_CLOUD) {
//            LinkedList<Future<?>> taskList = new LinkedList<>();

//            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_PROGRAM_DETAIL_API, radioId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            total.set(radioInfoJson.getInt("count"));
            JSONArray songArray = radioInfoJson.getJSONArray("programs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject programJson = songArray.getJSONObject(i);
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String albumName = radioJson.getString("name");
                String albumImgUrl = programJson.getString("coverUrl");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);
                musicInfos.add(musicInfo);
            }
//            }));

//            taskList.add(GlobalExecutors.requestExecutor.submit(() -> {
//                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, radioId))
//                        .execute()
//                        .body();
//                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
//                total.set(radioInfoJson.getJSONObject("data").getInt("programCount"));
//            }));

//            taskList.forEach(task -> {
//                try {
//                    task.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            });
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_QQ_API, radioId))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray songArray = radioInfoJson.getJSONObject("data").optJSONArray("tracks");
            if (songArray != null) {
                total.set(songArray.size());
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("mid");
                    String name = songJson.getString("title");
                    String artist = parseArtists(songJson, NetMusicSource.QQ);
                    String albumName = songJson.getJSONObject("album").getString("name");
                    Double duration = songJson.getDouble("interval");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.QQ);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setDuration(duration);
                    musicInfos.add(musicInfo);
                }
            }
        }

        // 喜马拉雅(接口分页)
        else if (source == NetMusicSource.XM) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_PROGRAM_XM_API, radioId, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            total.set(data.getInt("trackTotalCount"));
            JSONArray songArray = data.optJSONArray("tracks");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String id = songJson.getString("trackId");
                String name = songJson.getString("title");
                String artist = songJson.getString("anchorName");
                Double duration = songJson.getDouble("duration");
                String albumName = songJson.getString("albumTitle");
                // 专辑图片 url 可能不存在，需要判空
                String albumCoverPath = songJson.optString("albumCoverPath");
                String albumImgUrl = albumCoverPath != null ? "http://imagev2.xmcdn.com/" + albumCoverPath : "";

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
                // 喜马拉雅是 m4a 格式的文件！
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                // 喜马拉雅歌曲需要提前写入时长、专辑名称、封面图片 url！
                musicInfo.setDuration(duration);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfos.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, radioId))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject info = radioInfoJson.getJSONObject("info");
            JSONObject episodes = info.getJSONObject("episodes");
            JSONArray songArray = episodes.getJSONArray("episode");
            total.set(songArray.size());
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = songArray.getJSONObject(i);

                String songId = programJson.getString("sound_id");
                String name = programJson.getString("name");
                String artist = radioInfo.getDj();
                String albumName = radioInfo.getName();
                Double duration = programJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);

                musicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRanking(String rankingId, int source, int limit, int page) throws IOException {
        int total = 0;
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        // 网易云(榜单就是歌单，接口分页)
        if (source == NetMusicSource.NET_CLOUD) {
            return getMusicInfoInPlaylist(rankingId, source, limit, page);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_KG_API, rankingId, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                Double duration = songJson.getDouble("duration");
                String mvHash = songJson.getString("mvhash");
                String mvId = StringUtils.isEmpty(mvHash) ? "" : songJson.getJSONArray("mvdata").getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
//                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                musicInfos.add(musicInfo);
            }
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_QQ_API, rankingId, 1000))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
                String artists = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                musicInfos.add(musicInfo);
            }
        }

        // 酷我(接口分页)
        else if (source == NetMusicSource.KW) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_KW_API, rankingId, page, limit))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            total = data.getInt("num");
            JSONArray songArray = data.getJSONArray("musicList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                String name = songJson.getString("name");
                String artist = songJson.getString("artist");
                String albumName = songJson.getString("album");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getJSONObject("mvpayinfo").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KW);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                musicInfos.add(musicInfo);
            }
        }

        // 咪咕(程序分页)
        else if (source == NetMusicSource.MG) {
            String rankingInfoBody = HttpRequest.get(String.format(RANKING_DETAIL_MG_API, rankingId))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("columnInfo");
            total = data.getInt("contentsCount");
            JSONArray songArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i).getJSONObject("objectInfo");

                String songId = songJson.optString("copyrightId");
                // 过滤掉不是歌曲的 objectInfo
                if (StringUtils.isEmpty(songId)) continue;
                String name = songJson.getString("songName");
                String artists = songJson.getString("singer");
                String albumName = songJson.getString("album");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artists);
                musicInfo.setAlbumName(albumName);

                musicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(musicInfos, total);
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int limit, int page) throws IOException {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();
        boolean isAll = recordType == 0;

        int source = userInfo.getSource();
        String userId = userInfo.getId();

        // 网易云(程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_SONGS_API, recordType, userId))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray songArray = userInfoJson.optJSONArray(isAll ? "allData" : "weekData");
            if (songArray != null) {
                total.set(songArray.size());
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i).getJSONObject("song");

                    String songId = songJson.getString("id");
                    String name = songJson.getString("name");
                    String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String albumName = songJson.getJSONObject("al").getString("name");
                    String albumImgUrl = songJson.getJSONObject("al").getString("picUrl");
                    Double duration = songJson.getDouble("dt") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumImgUrl(albumImgUrl);
                    musicInfo.setDuration(duration);
                    musicInfos.add(musicInfo);
                }
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_XM_API, userId, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("trackList");
            total.set(data.getInt("totalCount"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("trackId");
                String name = songJson.getString("title");
                String artist = songJson.getString("nickname");
                String albumName = songJson.getString("albumTitle");
                String albumImgUrl = "http:" + songJson.getString("coverPath");
                Double duration = songJson.getDouble("length");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
                // 喜马拉雅是 m4a 格式的文件！
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);
                musicInfos.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, userId, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray songArray = data.getJSONArray("Datas");
            total.set(data.getJSONObject("pagination").getInt("count"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("soundstr");
                String artist = userInfo.getName();
//                String albumImgUrl = songJson.getString("front_cover");
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setFormat(Format.M4A);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
//                musicInfo.setAlbumImgUrl(albumImgUrl);
                musicInfo.setDuration(duration);
                musicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public static CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo netMusicInfo) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        LinkedList<NetMusicInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("songs");
            t = songsArray.size();
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String songName = songJson.getString("name");
                String artist = parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("duration") / 1000;
                String mvId = songJson.getString("mvid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            // 先根据 mid 获取 id
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("data").getJSONObject("track_info").getString("id");

            musicInfoBody = HttpRequest.get(String.format(SIMILAR_SONG_QQ_API, id))
                    .execute()
                    .body();
            musicInfoJson = JSONObject.fromObject(musicInfoBody);
            JSONArray songsArray = musicInfoJson.getJSONArray("data");
            t = songsArray.size();
            for (int i = 0, len = songsArray.size(); i < len; i++) {
                JSONObject songJson = songsArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String songName = songJson.getString("name");
                String artist = parseArtists(songJson, NetMusicSource.QQ);
                String albumName = songJson.getJSONObject("album").getString("name");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo netCommentInfo, int limit, int page) {
        int source = netCommentInfo.getSource();
        String uid = StringUtils.encode(netCommentInfo.getUserId());

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(USER_PLAYLIST_API, uid))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
            total.set(playlistArray.size());
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_QQ_API, uid))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("diss_name");
                    String creator = netCommentInfo.getUsername();
                    Long playCount = playlistJson.getLong("listen_num");
                    Integer trackCount = playlistJson.getInt("song_cnt");
                    String coverImgThumbUrl = playlistJson.getString("diss_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_QQ_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("list");
                t = data.getInt("total");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("dissid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("dissname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("listennum");
                    Integer trackCount = playlistJson.getInt("songnum");
                    String coverImgThumbUrl = playlistJson.getString("logo");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }

                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCollectedPlaylists));

            List<List<NetPlaylistInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetPlaylistInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtils.joinAll(rl));
        }

        return new CommonResult<>(res, total.get());
    }

    /**
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo netCommentInfo, int limit, int page) {
        int source = netCommentInfo.getSource();
        String uid = StringUtils.encode(netCommentInfo.getUserId());

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer total = 0;

        // QQ
        if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(USER_COLLECTED_ALBUM_QQ_API, uid, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albummid");
                String albumName = albumJson.getString("albumname");
                String artist = parseArtists(albumJson, NetMusicSource.QQ);
                String publishTime = TimeUtils.msToDate(albumJson.getLong("pubtime") * 1000);
                Integer songNum = albumJson.getInt("songnum");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 15;
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, uid, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("div.item");
            String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
            total = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            total += total / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select("div.pic img").first();

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text();
                String coverImgThumbUrl = img.attr("src");
                String[] sp = intro.text().split(" / ");
                String artist = sp[0];
                String pubTime = sp[1];

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(radioId);
                albumInfo.setName(radioName);
                albumInfo.setArtist(artist);
                albumInfo.setPublishTime(pubTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(USER_PLAYLIST_API, uid))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlist");
            total.set(playlistArray.size());
            for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            Callable<CommonResult<NetPlaylistInfo>> getCreatedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_CREATED_PLAYLIST_QQ_API, uid))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONArray playlistArray = playlistInfoJson.getJSONObject("data").getJSONArray("list");
                t = playlistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(playlistArray.size(), page * limit); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("tid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("diss_name");
                    String creator = netUserInfo.getName();
                    Long playCount = playlistJson.getLong("listen_num");
                    Integer trackCount = playlistJson.getInt("song_cnt");
                    String coverImgThumbUrl = playlistJson.getString("diss_cover");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetPlaylistInfo>> getCollectedPlaylists = () -> {
                LinkedList<NetPlaylistInfo> r = new LinkedList<>();
                Integer t = 0;

                String playlistInfoBody = HttpRequest.get(String.format(USER_COLLECTED_PLAYLIST_QQ_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
                JSONObject data = playlistInfoJson.getJSONObject("data");
                JSONArray playlistArray = data.getJSONArray("list");
                t = data.getInt("total");
                for (int i = 0, len = playlistArray.size(); i < len; i++) {
                    JSONObject playlistJson = playlistArray.getJSONObject(i);

                    String playlistId = playlistJson.getString("dissid");
                    if ("0".equals(playlistId)) continue;
                    String playlistName = playlistJson.getString("dissname");
                    String creator = playlistJson.getString("nickname");
                    Long playCount = playlistJson.getLong("listennum");
                    Integer trackCount = playlistJson.getInt("songnum");
                    String coverImgThumbUrl = playlistJson.getString("logo");

                    NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                    playlistInfo.setSource(NetMusicSource.QQ);
                    playlistInfo.setId(playlistId);
                    playlistInfo.setName(playlistName);
                    playlistInfo.setCreator(creator);
                    playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    playlistInfo.setPlayCount(playCount);
                    playlistInfo.setTrackCount(trackCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        playlistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(playlistInfo);
                }

                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetPlaylistInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedPlaylists));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCollectedPlaylists));

            List<List<NetPlaylistInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetPlaylistInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtils.joinAll(rl));
        }

        return new CommonResult<>(res, total.get());
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 0;

        // QQ
        if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(USER_COLLECTED_ALBUM_QQ_API, uid, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albummid");
                String albumName = albumJson.getString("albumname");
                String artist = parseArtists(albumJson, NetMusicSource.QQ);
                String publishTime = TimeUtils.msToDate(albumJson.getLong("pubtime") * 1000);
                Integer songNum = albumJson.getInt("songnum");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 15;
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DB_API, uid, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("div.item");
            String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
            t = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / rn * 5;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("li.title a").first();
                Element intro = radio.select("li.intro").first();
                Element img = radio.select("div.pic img").first();

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text();
                String coverImgThumbUrl = img.attr("src");
                String[] sp = intro.text().split(" / ");
                String artist = sp[0];
                String pubTime = sp[1];

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DB);
                albumInfo.setId(radioId);
                albumInfo.setName(radioName);
                albumInfo.setArtist(artist);
                albumInfo.setPublishTime(pubTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String albumInfoBody = HttpRequest.get(String.format(USER_ALBUM_DT_API, uid, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray albumArray = data.getJSONArray("object_list");
            t = page * limit;
            if (data.getInt("more") == 1) t++;
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("id");
                String albumName = albumJson.getString("name");
                String artist = albumJson.getJSONObject("user").getString("username");
                String publishTime = TimeUtils.msToDate(albumJson.getLong("updated_at_ts") * 1000);
                String coverImgThumbUrl = albumJson.getJSONArray("covers").getString(0);
//                Integer songNum = albumJson.getInt("songnum");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.DT);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
//                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getUserRadios(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String uid = netUserInfo.getId();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        AtomicInteger total = new AtomicInteger();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_API, uid))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONArray("djRadios");
            total.set(radioInfoJson.getInt("count"));
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getInt("programCount");
                String category = radioJson.getString("category");
                if (!category.isEmpty()) category += "、" + radioJson.getString("secondCategory");
                String coverImgThumbUrl = radioJson.getString("picUrl");
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            Callable<CommonResult<NetRadioInfo>> getCreatedRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_XM_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                JSONArray radioArray = data.getJSONArray("albumList");
                t = data.getInt("totalCount");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getString("anchorNickName");
                    String djId = radioJson.getString("anchorUid");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
//                String category = radioJson.getString("category");
                    String coverImgThumbUrl = "http:" + radioJson.getString("coverPath");
                    String coverImgUrl = coverImgThumbUrl;
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
//                radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetRadioInfo>> getSubRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_XM_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                JSONArray radioArray = data.getJSONArray("albumsInfo");
                t = data.getInt("totalCount");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("title");
                    String dj = radioJson.getJSONObject("anchor").getString("anchorNickName");
                    String djId = radioJson.getJSONObject("anchor").getString("anchorUid");
                    Long playCount = radioJson.getLong("playCount");
                    Integer trackCount = radioJson.getInt("trackCount");
                    String category = radioJson.getString("categoryTitle");
                    String coverImgThumbUrl = "http://imagev2.xmcdn.com/" + radioJson.getString("coverPath");
                    String coverImgUrl = coverImgThumbUrl;
//                String createTime = TimeUtils.msToDate(radioJson.getLong("createTime"));

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.XM);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setDjId(djId);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setTrackCount(trackCount);
                    radioInfo.setCategory(category);
//                radioInfo.setCreateTime(createTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }

                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSubRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtils.joinAll(rl));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            Callable<CommonResult<NetRadioInfo>> getCreatedRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getInt("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
//                    String category = radioJson.getString("type_name");
                    String dj = netUserInfo.getName();
                    Long playCount = radioJson.getLong("view_count");
                    String coverImgThumbUrl = radioJson.getString("cover");
                    String coverImgUrl = coverImgThumbUrl;

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setCoverImgUrl(coverImgUrl);
                    radioInfo.setPlayCount(playCount);
//                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }

                return new CommonResult<>(r, t);
            };

            Callable<CommonResult<NetRadioInfo>> getSubRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                String radioInfoBody = HttpRequest.get(String.format(USER_SUB_RADIO_ME_API, uid, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getInt("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
//                    String category = radioJson.getString("type");
                    String coverImgThumbUrl = radioJson.getString("cover");
                    String coverImgUrl = coverImgThumbUrl;

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setCoverImgUrl(coverImgUrl);
//                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    r.add(radioInfo);
                }

                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getCreatedRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getSubRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtils.joinAll(rl));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            Callable<CommonResult<NetRadioInfo>> getRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_RADIO_DB_API, uid, (page - 1) * rn))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select("div.item");
                String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
                t = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select("li.title a").first();
                    Element intro = radio.select("li.intro").first();
                    Element img = radio.select("div.pic img").first();

                    String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                    String radioName = a.text();
                    String dj = StringUtils.shorten(intro.text(), 100);
                    String coverImgThumbUrl = img.attr("src");
                    String category = "电影";

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(radioInfo);
                }
                return new CommonResult<>(r, t);
            };

            // 图书电台
            Callable<CommonResult<NetRadioInfo>> getBookRadios = () -> {
                LinkedList<NetRadioInfo> r = new LinkedList<>();
                Integer t = 0;

                final int rn = 15;
                String radioInfoBody = HttpRequest.get(String.format(USER_BOOK_RADIO_DB_API, uid, (page - 1) * rn))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                Elements rs = doc.select("li.subject-item");
                String ts = ReUtil.get("\\((\\d+)\\)", doc.select("div#db-usr-profile div.info h1").text(), 1);
                t = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
                t += t / rn * 5;
                for (int i = 0, len = rs.size(); i < len; i++) {
                    Element radio = rs.get(i);
                    Element a = radio.select("div.info a").first();
                    Element pub = radio.select("div.pub").first();
                    Element img = radio.select("div.pic img").first();

                    String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                    String radioName = a.text();
                    String dj = pub.text().trim();
                    String coverImgThumbUrl = img.attr("src");
                    String category = "书籍";

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setBook(true);
                    radioInfo.setSource(NetMusicSource.DB);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCategory(category);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });
                    r.add(radioInfo);
                }
                return new CommonResult<>(r, t);
            };

            List<Future<CommonResult<NetRadioInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getRadios));
            taskList.add(GlobalExecutors.requestExecutor.submit(getBookRadios));

            List<List<NetRadioInfo>> rl = new LinkedList<>();
            taskList.forEach(task -> {
                try {
                    CommonResult<NetRadioInfo> result = task.get();
                    rl.add(result.data);
                    total.set(Math.max(total.get(), result.total));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            res.addAll(ListUtils.joinAll(rl));
        }

        return new CommonResult<>(res, total.get());
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo netMusicInfo) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(RELATED_PLAYLIST_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
                Long playCount = playlistJson.getLong("playCount");
                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            // 先根据 mid 获取 id
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("data").getJSONObject("track_info").getString("id");

            String playlistInfoBody = HttpRequest.get(String.format(RELATED_PLAYLIST_QQ_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("data");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("tid");
                String playlistName = playlistJson.getString("dissname");
                String creator = playlistJson.getString("creator");
                Long playCount = playlistJson.getLong("listen_num");
                Integer trackCount = playlistJson.getInt("song_num");
                String coverImgThumbUrl = playlistJson.getString("imgurl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetMusicSource.QQ);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setPlayCount(playCount);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo netPlaylistInfo) {
        int source = netPlaylistInfo.getSource();
        String id = netPlaylistInfo.getId();

        LinkedList<NetPlaylistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String playlistInfoBody = HttpRequest.get(String.format(SIMILAR_PLAYLIST_API, id))
                    .execute()
                    .body();
            JSONObject playlistInfoJson = JSONObject.fromObject(playlistInfoBody);
            JSONArray playlistArray = playlistInfoJson.getJSONArray("playlists");
            t = playlistArray.size();
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("name");
                String creator = playlistJson.getJSONObject("creator").getString("nickname");
                String creatorId = playlistJson.getJSONObject("creator").getString("userId");
//                Long playCount = playlistJson.getLong("playCount");
//                Integer trackCount = playlistJson.getInt("trackCount");
                String coverImgThumbUrl = playlistJson.getString("coverImgUrl");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCreator(creator);
                playlistInfo.setCreatorId(creatorId);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                playlistInfo.setPlayCount(playCount);
//                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(playlistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo netMusicInfo, int limit, int page) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云(程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String mvInfoBody = HttpRequest.get(String.format(RELATED_MLOG_API, id))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("feeds");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject resource = mvArray.getJSONObject(i).getJSONObject("resource");
                JSONObject mlogBaseData = resource.getJSONObject("mlogBaseData");

                String mvId = mlogBaseData.getString("id");
                String mvName = mlogBaseData.getString("originalTitle");
                if ("null".equals(mvName)) mvName = mlogBaseData.getString("text");
                String artistName = resource.getJSONObject("userProfile").getString("nickname");
                String coverImgUrl = mlogBaseData.getString("coverUrl");
                Long playCount = resource.getJSONObject("mlogExtVO").getLong("playCount");
                Double duration = mlogBaseData.getDouble("duration") / 1000;
                String pubTime = TimeUtils.msToDate(mlogBaseData.getLong("pubTime"));

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setType(MvInfoType.MLOG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            // 先根据 mid 获取 id
            String musicInfoBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject musicInfoJson = JSONObject.fromObject(musicInfoBody);
            id = musicInfoJson.getJSONObject("data").getJSONObject("track_info").getString("id");

            String mvInfoBody = HttpRequest.get(String.format(RELATED_MV_QQ_API, id))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("data");
            t = mvArray.size();
            for (int i = (page - 1) * limit, len = Math.min(mvArray.size(), page * limit); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = parseArtists(mvJson, NetMusicSource.QQ);
                String coverImgUrl = mvJson.getString("picurl");
                Long playCount = mvJson.getLong("playcnt");
//                Double duration = mvJson.getDouble("duration");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName.trim());
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
//                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        String id = netMvInfo.getId();
        String name = StringUtils.encode(netMvInfo.getName());
        boolean isVideo = netMvInfo.isVideo();
        boolean isMlog = netMvInfo.isMlog();

        LinkedList<NetMvInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 视频
            if (isVideo || isMlog) {
                // Mlog 需要先获取视频 id，并转为视频类型
                if (isMlog) {
                    String body = HttpRequest.get(String.format(MLOG_TO_VIDEO_API, id))
                            .execute()
                            .body();
                    id = JSONObject.fromObject(body).getString("data");
                    netMvInfo.setId(id);
                    netMvInfo.setType(MvInfoType.VIDEO);
                }

                String mvInfoBody = HttpRequest.get(String.format(RELATED_VIDEO_API, id))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("data");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String artistName = parseCreators(mvJson);
                    String coverImgUrl = mvJson.getString("coverUrl");
                    Long playCount = mvJson.getLong("playTime");
                    Double duration = mvJson.getDouble("durationms") / 1000;

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setType(MvInfoType.VIDEO);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            // MV
            else {
                String mvInfoBody = HttpRequest.get(String.format(SIMILAR_MV_API, id))
                        .execute()
                        .body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
                t = mvArray.size();
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name");
                    String artistName = parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    String coverImgUrl = mvJson.getString("cover");
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getDouble("duration") / 1000;

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName.trim());
                    mvInfo.setArtist(artistName);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String mvInfoBody = HttpRequest.get(String.format(SIMILAR_VIDEO_HK_API, name, id))
                    .header(Header.REFERER, String.format("https://haokan.baidu.com/v?vid=%s", id))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvInfoBody).getJSONObject("data").getJSONObject("response");
            JSONArray mvArray = data.getJSONArray("videos");
            t = mvArray.size();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("source_name");
                String coverImgUrl = mvJson.getString("poster");
                Long playCount = mvJson.getLong("playcnt");
                Double duration = TimeUtils.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publish_time").replaceAll("年|月|日", "-");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似专辑
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getSimilarAlbums(NetAlbumInfo albumInfo) {
        int source = albumInfo.getSource();
        String id = albumInfo.getId();

        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 0;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String albumInfoBody = HttpRequest.get(String.format(SIMILAR_ALBUM_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            Elements rs = doc.select("dl.subject-rec-list");
            t = rs.size();
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element album = rs.get(i);
                Element a = album.select("dd a").first();
                Element img = album.select("img").first();

                String albumId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String albumName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetAlbumInfo ai = new NetAlbumInfo();
                ai.setSource(NetMusicSource.DB);
                ai.setId(albumId);
                ai.setName(albumName);
                ai.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    ai.setCoverImgThumb(coverImgThumb);
                });
                res.add(ai);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("artists");
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getInt("musicSize");
                Integer albumNum = artistJson.getInt("albumSize");
//                Integer mvNum = artistJson.optInt("mvSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.post(String.format(SIMILAR_ARTIST_KG_API))
                    .body("{\"clientver\":\"9108\",\"mid\":\"286974383886022203545511837994020015101\"," +
                            "\"clienttime\":\"1545746019\",\"key\":\"4c8b684568f03eeef985ae271561bcd8\"," +
                            "\"appid\":\"1005\",\"data\":[{\"author_id\":" + id + "}]}")
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("data").getJSONArray(0);
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("author_id");
                String artistName = artistJson.getString("author_name");
                String coverImgThumbUrl = artistJson.getString("sizable_avatar").replace("{size}", "240");
                String coverImgUrl = coverImgThumbUrl;

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_QQ_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONObject("data").optJSONArray("list");
            if (artistArray != null) {
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("mid");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = artistJson.getString("pic");
                    String coverImgUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QQ);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌单收藏者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo netPlaylistInfo, int limit, int page) {
        int source = netPlaylistInfo.getSource();
        String id = netPlaylistInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(PLAYLIST_SUBSCRIBERS_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("subscribers");
            t = userInfoJson.getInt("total");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
//                Integer follow = userJson.getInt("follows");
//                Integer followed = userJson.getInt("followeds");
//                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
//                userInfo.setFollow(follow);
//                userInfo.setFollowed(followed);
//                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getArtistFans(NetArtistInfo netArtistInfo, int limit, int page) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        AtomicReference<Integer> t = new AtomicReference<>(0);

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            Runnable getFans = () -> {
                String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_API, id, (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
                JSONArray userArray = userInfoJson.getJSONArray("data");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i).getJSONObject("userProfile");

                    String userId = userJson.getString("userId");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getInt("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                    String sign = userJson.getString("signature");
                    String avatarThumbUrl = userJson.getString("avatarUrl");
//                    Integer follow = userJson.getInt("follows");
//                    Integer followed = userJson.getInt("followeds");
//                    Integer playlistCount = userJson.getInt("playlistCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setAvatarUrl(avatarThumbUrl);
//                    userInfo.setSign(sign);
//                    userInfo.setFollow(follow);
//                    userInfo.setFollowed(followed);
//                    userInfo.setPlaylistCount(playlistCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            };

            Runnable getFansCnt = () -> {
                String tBody = HttpRequest.get(String.format(ARTIST_FANS_TOTAL_API, id))
                        .execute()
                        .body();
                t.set(JSONObject.fromObject(tBody).getJSONObject("data").getInt("fansCnt"));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getFans));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFansCnt));

            taskList.forEach(task -> {
                try {
                    task.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 35;
            String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_DB_API, id, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            String ts = ReUtil.get("（(\\d+)）", doc.select("div#content > h1").text(), 1);
            int tn = Integer.parseInt(ts);
            t.set(tn -= tn / rn * 15);
            Elements us = doc.select("dl.obu");
            for (int i = 0, len = us.size(); i < len; i++) {
                Element user = us.get(i);
                Elements a = user.select("dd a");
                Elements img = user.select("img");

                String userId = ReUtil.get("/people/(.*?)/", a.attr("href"), 1);
                String userName = a.text();
                String gender = "保密";
                String src = img.attr("src");
                String avatarThumbUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst("/u", "/ul");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DB);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t.get());
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;
        final int limit = 10;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_BUDDY_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select("div.partners.item");
            String ts = ReUtil.get("共(\\d+)条", doc.select("span.count").text(), 1);
            t = StringUtils.isEmpty(ts) ? cs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select("div.info a").first();
                Element img = artist.select("div.pic img").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo netArtistInfo, int page) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;
        final int limit = 10;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_RADIO_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements rs = doc.select("div.grid_view > ul > li > dl");
            String ts = ReUtil.get("共(\\d+)条", doc.select("span.count").text(), 1);
            t = StringUtils.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("h6 a").first();
                Element span = radio.select("h6 span").first();
                Element img = radio.select("img").first();
                Elements dl = radio.select("dl > dd > dl");

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text();
                String dj = dl.text().trim();
                String coverImgThumbUrl = img.attr("src");
                String category = ReUtil.get("(\\d+)", span.text(), 1);

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(radioInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo netRadioInfo, int limit, int page) {
        int source = netRadioInfo.getSource();
        String id = netRadioInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(RADIO_SUBSCRIBERS_API, id))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("subscribers");
            t = userArray.size();
            for (int i = (page - 1) * limit, len = Math.min(userArray.size(), page * limit); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
//                Integer follow = userJson.getInt("follows");
//                Integer followed = userJson.getInt("followeds");
//                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
//                userInfo.setFollow(follow);
//                userInfo.setFollowed(followed);
//                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(isBook ? SIMILAR_BOOK_RADIO_DB_API : SIMILAR_RADIO_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements rs = doc.select(isBook ? "div#db-rec-section dl:not(.clear)" : "div.recommendations-bd dl");
            t = rs.size();
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("dd a").first();
                Element img = radio.select("img").first();

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text().trim();
                String coverImgThumbUrl = img.attr("src");

                NetRadioInfo ri = new NetRadioInfo();
                ri.setBook(isBook);
                ri.setSource(NetMusicSource.DB);
                ri.setId(radioId);
                ri.setName(radioName);
                ri.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    ri.setCoverImgThumb(coverImgThumb);
                });
                res.add(ri);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo netRadioInfo) {
        int source = netRadioInfo.getSource();
        String id = netRadioInfo.getId();
        boolean isBook = netRadioInfo.isBook();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 豆瓣
        if (source == NetMusicSource.DB && !isBook) {
            String artistInfoBody = HttpRequest.get(String.format(RADIO_ARTISTS_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select("li.celebrity");
            t = cs.size();
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select("span.name a").first();
                Element img = artist.select("div.avatar").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = ReUtil.get("url\\((.*?)\\)", img.attr("style"), 1);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = extractProfile(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserInfo(String id, int source) {
        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"null".equals(id) && StringUtils.isNotEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NET_CLOUD) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_API, id))
                        .execute()
                        .body();
                JSONObject userJson = JSONObject.fromObject(userInfoBody).getJSONObject("profile");

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String accAge = TimeUtils.getAccAge(userJson.getLong("createTime"));
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getInt("follows");
                Integer followed = userJson.getInt("followeds");
                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAccAge(accAge);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 喜马拉雅
            else if (source == NetMusicSource.XM) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, id))
                        .execute()
                        .body();
                JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
                JSONObject data = userInfoJson.getJSONObject("data");

                String userId = data.getString("uid");
                String userName = data.getString("nickName");
                Integer gen = data.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "http:" + data.getString("cover");
                Integer follow = data.getInt("followingCount");
                Integer followed = data.getInt("fansCount");
                Integer radioCount = data.getInt("albumsCount");
                Integer programCount = data.getInt("tracksCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 猫耳
            else if (source == NetMusicSource.ME) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(id);
                userInfo.setName(doc.getElementById("t_u_n").getElementsByTag("a").first().text());
                String avaUrl = "https:" + doc.getElementById("topusermainicon").getElementsByTag("img").first().attr("src");
                userInfo.setAvatarThumbUrl(avaUrl);
                userInfo.setAvatarUrl(avaUrl);
                userInfo.setGender("保密");
                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
                userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));
//            if (!userInfo.hasRadioCount()) userInfo.setRadioCount(Integer.parseInt(ReUtil.get(
//                    "剧集.*?\\((\\d+)\\)", userInfoBody, 1)));
//            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(Integer.parseInt(ReUtil.get(
//                    "声音.*?\\((\\d+)\\)", userInfoBody, 1)));

                String finalAvatarThumbUrl = avaUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 豆瓣
            else if (source == NetMusicSource.DB) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                Element h1 = doc.select("div.info > h1").first();
                Elements img = doc.select("div.basic-info img");

                String userName = h1.ownText().trim();
                if (StringUtils.isNotEmpty(userName)) {
                    String gender = "保密";
                    String avatarThumbUrl = img.attr("src");
                    String avatarUrl = avatarThumbUrl;

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.DB);
                    userInfo.setId(id);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setAvatarUrl(avatarUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = extractProfile(avatarThumbUrl);
                        userInfo.setAvatarThumb(coverImgThumb);
                    });

                    res.add(userInfo);
                }
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFollows(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_API, id))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("follow");
            t = userArray.size();
            for (int i = (page - 1) * limit, len = Math.min(userArray.size(), page * limit); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getInt("follows");
                Integer followed = userJson.getInt("followeds");
                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_XM_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("followingsPageInfo");
            t = data.getInt("totalCount");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("uid");
                String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getInt("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "http:" + userJson.getString("coverPath");
                Integer follow = userJson.getInt("followingCount");
                Integer followed = userJson.getInt("followerCount");
                Integer radioCount = userJson.getInt("albumCount");
                Integer programCount = userJson.getInt("trackCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
//                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWS_ME_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
//                String sign = userJson.getString("userintro");
                String avatarThumbUrl = userJson.getString("boardiconurl2");
                String avatarUrl = avatarThumbUrl;
                Integer followed = userJson.getInt("fansnum");
                Integer programCount = userJson.getInt("soundnumchecked");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFolloweds(NetUserInfo netUserInfo, int limit, int page) {
        int source = netUserInfo.getSource();
        String id = netUserInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("followeds");
            t = userInfoJson.getInt("size");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getInt("follows");
                Integer followed = userJson.getInt("followeds");
                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_XM_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("fansPageInfo");
            t = data.getInt("totalCount");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("uid");
                String userName = userJson.getString("anchorNickName");
//                Integer gen = userJson.getInt("gender");
//                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "http:" + userJson.getString("coverPath");
                Integer follow = userJson.getInt("followingCount");
                Integer followed = userJson.getInt("followerCount");
                Integer radioCount = userJson.getInt("albumCount");
                Integer programCount = userJson.getInt("trackCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
//                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_FOLLOWEDS_ME_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("Datas");
            t = data.getJSONObject("pagination").getInt("count");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
//                String sign = userJson.getString("userintro");
                String avatarThumbUrl = userJson.getString("boardiconurl2");
                String avatarUrl = avatarThumbUrl;
                Integer followed = userJson.getInt("fansnum");
                Integer programCount = userJson.getInt("soundnumchecked");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = extractProfile(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据链接获取图片
     */
    public static BufferedImage getImageFromUrl(String urlPath) {
        return ImageUtils.read(HttpRequest.get(urlPath).execute().bodyStream());
    }

//    /**
//     * 根据歌曲 id 获取专辑图片
//     */
//    public static BufferedImage getMusicAlbumImage(int albumId) throws IOException {
//        String albumBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumId))
//                .execute()
//                .body();
//        JSONObject albumJson = JSONObject.fromObject(albumBody);
//        String albumUrlPath = albumJson.getJSONObject("album").getString("blurPicUrl");
//        URL url = new URL(albumUrlPath);
//        URLConnection connection = url.openConnection();
//        connection.setRequestProperty("User-Agent", USER_AGENT);
//        connection.connect();
//        return ImageIO.read(connection.getInputStream());
//    }

    /**
     * 解析歌曲艺术家
     */
    public static String parseArtists(JSONObject json, int source) {
        JSONArray artistsArray;
        if (source == NetMusicSource.QQ) {
            artistsArray = json.optJSONArray("singer");
            if (artistsArray == null) artistsArray = json.optJSONArray("singer_list");
            if (artistsArray == null) artistsArray = json.optJSONArray("singers");
            if (artistsArray == null) artistsArray = json.getJSONArray("ar");
        } else {
            artistsArray = json.optJSONArray("artists");
            if (artistsArray == null) artistsArray = json.optJSONArray("ar");
            if (artistsArray == null) artistsArray = json.optJSONArray("artist");
            if (artistsArray == null) artistsArray = json.optJSONArray("actors");
            if (artistsArray == null) return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = artistsArray.size(); i < len; i++) {
            JSONObject artistJson = artistsArray.getJSONObject(i);
            String name = artistJson.optString("name", null);
            if (name == null) name = artistJson.getString("singer_name");
            sb.append(name);
            if (i != len - 1) sb.append("、");
        }
        return sb.toString();
    }

    /**
     * 连接 Json 数组中的所有字符串
     */
    public static String joinStrings(JSONArray array, int limit) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = array.size(); i < len; i++) {
            sb.append(array.getString(i));
            if (i != len - 1) sb.append("、");
            if (limit > 0 && i >= limit) {
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 解析歌单标签
     */
    public static String parseTags(JSONObject json, int source) {
        JSONArray tagArray;
        if (source == NetMusicSource.NET_CLOUD) {
            tagArray = json.getJSONArray("tags");
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                sb.append(tagArray.getString(i));
                if (i != len - 1) sb.append("、");
            }
            return sb.toString();
        } else if (source == NetMusicSource.QQ) {
            tagArray = json.getJSONArray("tags");
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sb.append(tagJson.getString("name"));
                if (i != len - 1) sb.append("、");
            }
            return sb.toString();
        } else if (source == NetMusicSource.MG) {
            tagArray = json.getJSONArray("tagLists");
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sb.append(tagJson.getString("tagName"));
                if (i != len - 1) sb.append("、");
            }
            return sb.toString();
        } else if (source == NetMusicSource.QI) {
            tagArray = json.getJSONArray("tagList");
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                sb.append(tagArray.getString(i));
                if (i != len - 1) sb.append("、");
            }
            return sb.toString();
        } else if (source == NetMusicSource.ME) {
            tagArray = json.getJSONArray("tags");
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sb.append(tagJson.getString("name"));
                if (i != len - 1) sb.append("、");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 解析视频作者
     */
    public static String parseCreators(JSONObject json) {
        JSONArray artistsArray = json.getJSONArray("creator");
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = artistsArray.size(); i < len; i++) {
            sb.append(artistsArray.getJSONObject(i).getString("userName"));
            if (i != len - 1) sb.append("、");
        }
        return sb.toString();
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public static String fetchMusicUrl(String songId, int source) {
        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 首选高音质接口
            String songBody = HttpRequest.get(String.format(GET_SONG_URL_API_NEW, songId))
                    .execute()
                    .body();
            JSONArray data = JSONObject.fromObject(songBody).optJSONArray("data");
            // 次选普通音质
            if (data == null) {
                songBody = HttpRequest.get(String.format(GET_SONG_URL_API, songId))
                        .execute()
                        .body();
                data = JSONObject.fromObject(songBody).optJSONArray("data");
            }
            if (data != null) {
                JSONObject urlJson = data.getJSONObject(0);
                String url = urlJson.getString("url");
                if (!"null".equals(url)) return url;
            }
        }

        // 酷狗(歌曲详情能直接请求到 url，不需要单独调用此方法)
//        else if (source == NetMusicSource.KG) {
//            // 酷狗接口请求需要带上 cookie ！
//            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, songId))
//                    .header(Header.COOKIE, COOKIE)
//                    .execute()
//                    .body();
//            JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");
//            return data.getString("play_url");
//        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playUrlBody = HttpRequest.get(String.format(GET_SONG_URL_QQ_API, songId))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.fromObject(playUrlBody);
            String url = urlJson.optString("data");
            if (!"null".equals(url)) return url;
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String playUrlBody = HttpRequest.get(String.format(GET_SONG_URL_KW_API, songId))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.fromObject(playUrlBody);
            return urlJson.getJSONObject("data").getString("url");
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = HttpRequest.get(buildQianUrl(String.format(GET_SONG_URL_QI_API, songId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.fromObject(playUrlBody).getJSONObject("data");
            String url = urlJson.optString("path");
            if (url.isEmpty()) url = urlJson.getJSONObject("trail_audio_info").getString("path");
            return url;
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String playUrlBody = HttpRequest.get(String.format(GET_SONG_URL_XM_API, songId))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.fromObject(playUrlBody);
            return urlJson.getJSONObject("data").getString("src");
        }

        return "";
    }

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public static String fetchMvUrl(NetMvInfo netMvInfo) {
        int source = netMvInfo.getSource();
        String mvId = netMvInfo.getId();
        boolean isVideo = netMvInfo.isVideo();
        boolean isMlog = netMvInfo.isMlog();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            if (isVideo || isMlog) {
                // Mlog 需要先获取视频 id，并转为视频类型
                if (isMlog) {
                    String body = HttpRequest.get(String.format(MLOG_TO_VIDEO_API, mvId))
                            .execute()
                            .body();
                    mvId = JSONObject.fromObject(body).getString("data");
                    netMvInfo.setId(mvId);
                    netMvInfo.setType(MvInfoType.VIDEO);
                }

                String mvBody = HttpRequest.get(String.format(VIDEO_URL_API, mvId))
                        .execute()
                        .body();
                JSONObject mvJson = JSONObject.fromObject(mvBody);
                JSONArray urls = mvJson.getJSONArray("urls");
                String url = urls.getJSONObject(0).getString("url");
                if (!"null".equals(url)) return url;
            }
//            else if (isMlog) {
//                String mvBody = HttpRequest.get(String.format(MLOG_URL_API, mvId))
//                        .execute()
//                        .body();
//                JSONObject mvJson = JSONObject.fromObject(mvBody);
//                JSONArray urls = mvJson.getJSONObject("data")
//                        .getJSONObject("resource")
//                        .getJSONObject("content")
//                        .getJSONObject("video")
//                        .getJSONArray("urlInfos");
//                String url = null;
//                int r = 0;
//                for (int i = 0, s = urls.size(); i < s; i++) {
//                    JSONObject urlJson = urls.getJSONObject(i);
//                    int r1 = urlJson.getInt("r");
//                    if (r < r1) {
//                        r = r1;
//                        url = urlJson.getString("url");
//                    }
//                }
//                if (!"null".equals(url)) return url;
//            }
            else {
                String mvBody = HttpRequest.get(String.format(MV_URL_API, mvId))
                        .execute()
                        .body();
                JSONObject mvJson = JSONObject.fromObject(mvBody);
                JSONObject data = mvJson.getJSONObject("data");
                String url = data.getString("url");
                if (!"null".equals(url)) return url;
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
//            long ct = System.currentTimeMillis();
//            String mvBody = HttpRequest.get(buildKgUrl(String.format(MV_URL_KG_API, ct, mvId, ct, ct)))
//                    .header("x-router", "trackermv.kugou.com")
//                    .execute()
//                    .body();
//            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("data");
//            return data.getJSONObject(mvId.toLowerCase()).getString("downurl");

            String mvBody = HttpRequest.get(String.format(MV_URL_KG_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("mvdata");
            JSONObject mvJson;
            // 高画质优先
            mvJson = data.getJSONObject("rq");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("sq");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("sd");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("le");
            if (mvJson.isEmpty()) mvJson = data.getJSONObject("hd");
            return mvJson.optString("downurl");
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvBody = HttpRequest.get(String.format(MV_URL_QQ_API, mvId))
                    .execute()
                    .body();
            JSONArray mp4Array = JSONObject.fromObject(mvBody)
                    .getJSONObject("data")
                    .getJSONArray(mvId);
            return mp4Array.getString(mp4Array.size() - 1);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String mvBody = HttpRequest.get(String.format(MV_URL_KW_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).optJSONObject("data");
            if (data != null) return data.getString("url");
        }

        // 咪咕 (暂时没有 MV url 的获取方式)
        else if (source == NetMusicSource.MG) {
            return null;
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String mvBody = HttpRequest.get(buildQianUrl(String.format(MV_URL_QI_API, mvId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONArray data = JSONObject.fromObject(mvBody).getJSONArray("data");
            JSONArray urls = data.getJSONObject(0).getJSONArray("allRate");
            return urls.getJSONObject(0).getString("path");
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String mvBody = HttpRequest.get(String.format(MV_URL_HK_API, mvId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(mvBody).getJSONObject("data");
            JSONArray urls = data.getJSONObject("apiData").getJSONObject("curVideoMeta").getJSONArray("clarityUrl");
            return urls.getJSONObject(urls.size() - 1).getString("url");
        }

        return null;
    }

    /**
     * 根据为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public static void fillLrc(NetMusicInfo netMusicInfo) {
        int source = netMusicInfo.getSource();
        String id = netMusicInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_API, id))
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.fromObject(lrcBody);
            JSONObject lrc = lrcJson.optJSONObject("lrc");
            JSONObject tLrc = lrcJson.optJSONObject("tlyric");
            JSONObject romaLrc = lrcJson.optJSONObject("romalrc");
            if (lrc != null) netMusicInfo.setLrc(lrc.getString("lyric"));
            if (tLrc != null) netMusicInfo.setTrans(tLrc.getString("lyric"));
            if (romaLrc != null) netMusicInfo.setRoma(romaLrc.getString("lyric"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            // 酷狗接口请求需要带上 cookie ！
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, id))
                    .header(Header.COOKIE, COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(songBody).getJSONObject("data");
            netMusicInfo.setLrc(data.getString("lyrics"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
                    .execute()
                    .body();
            JSONObject lrcJson = JSONObject.fromObject(lrcBody).getJSONObject("data");
            netMusicInfo.setLrc(StringUtils.removeHTMLLabel(lrcJson.getString("lyric")));
            netMusicInfo.setTrans(StringUtils.removeHTMLLabel(lrcJson.getString("trans")));
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String lrcBody = HttpRequest.get(String.format(LYRIC_KW_API, id))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(lrcBody).getJSONObject("data");
            // 酷我歌词返回的是数组，需要先处理成字符串！
            // lrclist 可能是数组也可能为 null ！
            JSONArray lrcArray = data.optJSONArray("lrclist");
            if (lrcArray != null) {
                StringBuffer sb = new StringBuffer();
                boolean hasTrans = false;
                for (int i = 0, len = lrcArray.size(); i < len; i++) {
                    JSONObject sentenceJson = lrcArray.getJSONObject(i);
                    JSONObject nextSentenceJson = lrcArray.optJSONObject(i + 1);
                    // 歌词中带有翻译时，最后一句是翻译直接跳过
                    if (hasTrans && nextSentenceJson == null) break;
                    String time = TimeUtils.formatToLrcTime(sentenceJson.getDouble("time"));
                    String nextTime = null;
                    if (nextSentenceJson != null)
                        nextTime = TimeUtils.formatToLrcTime(nextSentenceJson.getDouble("time"));
                    // 歌词中带有翻译，有多个 time 相同的歌词时取不重复的第二个
                    if (!time.equals(nextTime)) {
                        sb.append(time);
                        String lineLyric = StringUtils.removeHTMLLabel(sentenceJson.getString("lineLyric"));
                        sb.append(lineLyric);
                        sb.append("\n");
                    } else hasTrans = true;
                }
                netMusicInfo.setLrc(sb.toString());
            } else netMusicInfo.setLrc(null);

            // 酷我歌词返回的是数组，需要先处理成字符串！
            // lrclist 可能是数组也可能为 null ！
            if (lrcArray != null) {
                StringBuffer sb = new StringBuffer();
                boolean hasTrans = false;
                String lastTime = null;
                for (int i = 0, len = lrcArray.size(); i < len; i++) {
                    JSONObject sentenceJson = lrcArray.getJSONObject(i);
                    JSONObject nextSentenceJson = lrcArray.optJSONObject(i + 1);
                    String time = TimeUtils.formatToLrcTime(sentenceJson.getDouble("time"));
                    String nextTime = null;
                    if (nextSentenceJson != null)
                        nextTime = TimeUtils.formatToLrcTime(nextSentenceJson.getDouble("time"));
                    // 歌词中带有翻译，有多个 time 相同的歌词时取重复的第一个；最后一句也是翻译
                    if (hasTrans && nextTime == null || time.equals(nextTime)) {
                        sb.append(lastTime);
                        String lineLyric = StringUtils.removeHTMLLabel(sentenceJson.getString("lineLyric"));
                        sb.append(lineLyric);
                        sb.append("\n");
                        hasTrans = true;
                    }
                    lastTime = time;
                }
                netMusicInfo.setTrans(sb.toString());
            } else netMusicInfo.setTrans(null);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = HttpRequest.get(buildQianUrl(String.format(GET_SONG_URL_QI_API, id, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.fromObject(playUrlBody);
            String lrcUrl = urlJson.getJSONObject("data").getString("lyric");
            netMusicInfo.setLrc(StringUtils.isNotEmpty(lrcUrl) ? HttpRequest.get(lrcUrl).execute().body() : "");
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        } else {
            netMusicInfo.setLrc("");
            netMusicInfo.setTrans("");
            netMusicInfo.setRoma("");
        }
    }

//    /**
//     * 判断音乐 / MV 的 url 是否有效(没有过期)
//     *
//     * @param url
//     * @return
//     */
//    public static boolean isValidUrl(String url) {
//        HttpResponse resp = HttpRequest.get(url).execute();
//        return resp.getStatus() == HttpStatus.HTTP_OK;
//    }

    /**
     * 下载文件
     *
     * @param urlPath
     * @param dest
     * @throws Exception
     */
    public static void download(String urlPath, String dest) throws Exception {
        File file = new File(dest);
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[1024];
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            //读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }

    /**
     * 下载文件，同时设置等待面板百分比
     *
     * @param loading
     * @param urlPath
     * @param dest
     * @throws Exception
     */
    public static void download(LoadingPanel loading, String urlPath, String dest) throws Exception {
        File file = new File(dest);
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[1024];
        // 文件大小
        long fileSize = conn.getContentLength(), hasRead = 0;
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            hasRead += read;
            loading.setText("加载歌曲文件，" + String.format("%.1f", (double) hasRead / fileSize * 100) + "%");
            //读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }

    /**
     * 通过 Task 下载文件，设置 percent
     *
     * @param task
     * @throws Exception
     */
    public static void download(Task task) throws Exception {
        File file = new File(task.getDest());
        URL url = new URL(task.getUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[1024];
        // 文件大小
        long fileSize = conn.getContentLength(), hasRead = 0;
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            hasRead += read;
            task.setPercent((double) hasRead / fileSize * 100);
            // 读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }

//    /**
//     * 下载 MV 文件，同时设置对话框中百分比
//     * @param dialog
//     * @param urlPath
//     * @param dest
//     * @throws Exception
//     */
//    public static void download(TipDialog dialog, String urlPath, String dest) throws Exception {
//        File file = new File(dest);
//        URL url = new URL(urlPath);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestProperty("User-Agent", USER_AGENT);
//        conn.connect();
//        int code = conn.getResponseCode();
//        if (code != HttpURLConnection.HTTP_OK) {
//            throw new Exception("文件读取失败");
//        }
//        InputStream fis = new BufferedInputStream(conn.getInputStream());
//        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
//        // 以流的形式下载文件
//        byte[] buffer = new byte[1024];
//        // 文件大小
//        long fileSize = conn.getContentLength(), hasRead = 0;
//        int read;
//        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
//        while ((read = fis.read(buffer)) != -1) {
//            hasRead += read;
//            dialog.setMessage("请稍候，MV 加载中......" + String.format("%d", hasRead * 100 / fileSize) + "%");
//            //读取多少输出多少
//            toClient.write(buffer, 0, read);
//        }
//        toClient.flush();
//        toClient.close();
//        fis.close();
//    }
}
